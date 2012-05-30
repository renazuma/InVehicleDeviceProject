package com.kogasoftware.odt.invehicledevice.backgroundtask;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess;
import com.kogasoftware.odt.invehicledevice.logic.event.CommonLogicLoadCompleteEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.NewOperationStartEvent;
import com.kogasoftware.odt.invehicledevice.ui.modalview.NotificationModalView;

/**
 * バックグランドでの処理を管理するクラス
 * 
 * 注意: quit以外のメソッドは全て同じスレッドで実行する
 */
public class BackgroundTask {
	private static final String TAG = BackgroundTaskThread.class
			.getSimpleName();
	private static final long POLLING_PERIOD_MILLIS = 30 * 1000;
	private static final Integer NUM_THREADS = 3;

	private final LocationManager locationManager;
	private final SensorManager sensorManager;
	private final ConnectivityManager connectivityManager;
	private final Optional<TelephonyManager> optionalTelephonyManager;
	private final SharedPreferences sharedPreferences;
	private final LocationSender locationSender;
	private final ExitRequiredPreferenceChangeListener exitRequiredPreferenceChangeListener;
	private final TemperatureSensorEventListener temperatureSensorEventListener;
	private final OrientationSensorEventListener orientationSensorEventListener;
	private final CountDownLatch completeLatch = new CountDownLatch(1);
	private final ScheduledExecutorService executorService = Executors
			.newScheduledThreadPool(NUM_THREADS);
	private final VehicleNotificationReceiver vehicleNotificationReceiver;
	private final NextDateChecker nextDateChecker;
	private final SignalStrengthListener signalStrengthListener;
	private final CommonLogic commonLogic;
	private final Thread voiceThread;
	private final Thread operationScheduleReceiveThread;
	private final Looper myLooper;
	private final AtomicBoolean quitCalled = new AtomicBoolean(false);

	public BackgroundTask(CommonLogic commonLogic, Context context,
			StatusAccess statusAccess) {
		if (Looper.myLooper() == null) {
			Looper.prepare();
		}
		myLooper = Looper.myLooper();
		this.commonLogic = commonLogic;

		locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		sensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
		connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		// TODO:内容精査
		// TelephonyManagerはNullPointerExceptionを発生させる
		// E/AndroidRuntime(24190):FATAL EXCEPTION: Thread-4030
		// E/AndroidRuntime(24190):java.lang.NullPointerException
		// E/AndroidRuntime(24190):at_android.telephony.TelephonyManager.<init>(TelephonyManager.java:71)
		// E/AndroidRuntime(24190):at_android.app.ContextImpl$26.createService(ContextImpl.java:410)
		// E/AndroidRuntime(24190):at_android.app.ContextImpl$ServiceFetcher.getService(ContextImpl.java:198)
		// E/AndroidRuntime(24190):at_android.app.ContextImpl.getSystemService(ContextImpl.java:1176)
		// E/AndroidRuntime(24190):at_com.kogasoftware.odt.invehicledevice.backgroundtask.BackgroundTask.<init>(BackgroundTask.java:78)
		// E/AndroidRuntime(24190):at_com.kogasoftware.odt.invehicledevice.test.unit.backgroundtask.BackgroundTaskTestCase$1.run(BackgroundTaskTestCase.java:44)
		Optional<TelephonyManager> tempTelephonyManager = Optional.absent();
		try {
			tempTelephonyManager = Optional.of((TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE));
		} catch (NullPointerException e) {
			Log.w(TAG, e);
		}
		optionalTelephonyManager = tempTelephonyManager;

		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);

		exitRequiredPreferenceChangeListener = new ExitRequiredPreferenceChangeListener(
				commonLogic);
		locationSender = new LocationSender(commonLogic);
		orientationSensorEventListener = new OrientationSensorEventListener(
				commonLogic);
		vehicleNotificationReceiver = new VehicleNotificationReceiver(
				commonLogic);
		nextDateChecker = new NextDateChecker(commonLogic);
		temperatureSensorEventListener = new TemperatureSensorEventListener(
				commonLogic);
		signalStrengthListener = new SignalStrengthListener(commonLogic,
				connectivityManager);
		voiceThread = new VoiceThread(context);
		operationScheduleReceiveThread = new OperationScheduleReceiveThread(
				commonLogic);
	}

	/**
	 * Looper.loop()のループに入る。
	 * quitの実行で終了する。またLooper.loop()が始まる前にスレッドがinterruptされていた場合も終了する。
	 */
	public void loop() {
		try {
			Thread.sleep(0); // interruption point
			onLoopStart();
			Thread.sleep(0); // interruption point
			Looper.loop();
		} catch (InterruptedException e) {
			// do nothing
		} catch (RejectedExecutionException e) {
			Log.w(TAG, e);
		} catch (ExecutionException e) {
			Log.w(TAG, e);
		} finally {
			onLoopStop();
		}
	}

	private void onLoopStart() throws InterruptedException,
			RejectedExecutionException, ExecutionException {

		for (Object object : new Object[] { voiceThread,
				operationScheduleReceiveThread, vehicleNotificationReceiver,
				locationSender, temperatureSensorEventListener,
				orientationSensorEventListener,
				exitRequiredPreferenceChangeListener, signalStrengthListener, }) {
			commonLogic.registerEventListener(object);
		}

		voiceThread.start();
		operationScheduleReceiveThread.start();

		if (!commonLogic.isOperationScheduleInitialized()) {
			commonLogic.postEvent(new NewOperationStartEvent());
			commonLogic.waitForOperationScheduleInitialize();
		}

		commonLogic
				.registerEventListener(new Function<CommonLogicLoadCompleteEvent, Void>() {
					@Subscribe
					@Override
					public Void apply(CommonLogicLoadCompleteEvent e) {
						completeLatch.countDown();
						return null;
					}
				});

		commonLogic.postEvent(new CommonLogicLoadCompleteEvent(commonLogic));
		completeLatch.await();

		sharedPreferences
				.registerOnSharedPreferenceChangeListener(exitRequiredPreferenceChangeListener);
		locationManager.requestLocationUpdates(
				LocationManager.GPS_PROVIDER, 2000, 1, locationSender);

		List<Sensor> temperatureSensors = sensorManager
				.getSensorList(Sensor.TYPE_TEMPERATURE);
		if (temperatureSensors.size() > 0) {
			Sensor sensor = temperatureSensors.get(0);
			sensorManager.registerListener(temperatureSensorEventListener,
					sensor, SensorManager.SENSOR_DELAY_UI);
		}

		List<Sensor> orientationSensors = sensorManager
				.getSensorList(Sensor.TYPE_ORIENTATION);
		if (orientationSensors.size() > 0) {
			Sensor sensor = orientationSensors.get(0);
			sensorManager.registerListener(orientationSensorEventListener,
					sensor, SensorManager.SENSOR_DELAY_UI);
		}

		for (TelephonyManager telephonyManager : optionalTelephonyManager
				.asSet()) {
			telephonyManager.listen(signalStrengthListener,
					PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
		}
		commonLogic.postEvent(new NotificationModalView.ShowEvent());

		executorService.scheduleWithFixedDelay(vehicleNotificationReceiver, 0,
				POLLING_PERIOD_MILLIS, TimeUnit.MILLISECONDS);
		executorService.scheduleWithFixedDelay(nextDateChecker, 0,
				POLLING_PERIOD_MILLIS, TimeUnit.MILLISECONDS);
		executorService.scheduleWithFixedDelay(locationSender, 0,
				POLLING_PERIOD_MILLIS, TimeUnit.MILLISECONDS);
	}

	private void onLoopStop() {
		voiceThread.interrupt();
		operationScheduleReceiveThread.interrupt();
		sharedPreferences
				.unregisterOnSharedPreferenceChangeListener(exitRequiredPreferenceChangeListener);
		locationManager.removeUpdates(locationSender);
		sensorManager.unregisterListener(temperatureSensorEventListener);
		sensorManager.unregisterListener(orientationSensorEventListener);
		for (TelephonyManager telephonyManager : optionalTelephonyManager
				.asSet()) {
			telephonyManager.listen(signalStrengthListener,
					PhoneStateListener.LISTEN_NONE);
		}
		executorService.shutdownNow();
	}

	/**
	 * loop()を終了する。loop()に入っていない状態でもloop()は終了する。
	 */
	public void quit() {
		// 二回以上呼ばれないようにする
		if (!quitCalled.getAndSet(true)) {
			myLooper.quit();
		}
	}
}