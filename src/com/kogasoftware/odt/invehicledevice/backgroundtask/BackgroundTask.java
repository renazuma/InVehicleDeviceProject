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
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.os.Looper;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.WindowManager;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.event.CommonLogicLoadCompleteEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.NewOperationStartEvent;
import com.kogasoftware.odt.invehicledevice.service.startupservice.StartupService;
import com.kogasoftware.odt.invehicledevice.service.voiceservice.VoiceService;
import com.kogasoftware.odt.invehicledevice.ui.modalview.NotificationModalView;

/**
 * バックグランドでの処理を管理するクラス
 * 
 * 注意: quit以外のメソッドは全て同じスレッドで実行する
 */
public class BackgroundTask {
	private static final String TAG = BackgroundTask.class.getSimpleName();
	public static final String ACTION_EXIT = BackgroundTask.class.getName()
			+ ".ACTION_EXIT";
	private static final long POLLING_PERIOD_MILLIS = 30 * 1000;
	private static final Integer NUM_THREADS = 3;

	private final SensorManager sensorManager;
	private final ConnectivityManager connectivityManager;
	private final WindowManager windowManager;
	private final Optional<TelephonyManager> optionalTelephonyManager;
	private final LocationSender locationSender;
	private final TemperatureSensorEventListener temperatureSensorEventListener;
	private final AccMagSensorEventListener accMagSensorEventListener;
	private final OrientationSensorEventListener orientationSensorEventListener;
	private final CountDownLatch completeLatch = new CountDownLatch(1);
	private final ScheduledExecutorService executorService = Executors
			.newScheduledThreadPool(NUM_THREADS);
	private final VehicleNotificationReceiver vehicleNotificationReceiver;
	private final NextDateChecker nextDateChecker;
	private final SignalStrengthListener signalStrengthListener;
	private final ExitBroadcastReceiver exitBroadcastReceiver;
	private final CommonLogic commonLogic;
	private final Thread operationScheduleReceiveThread;
	private final Looper myLooper;
	private final AtomicBoolean quitCalled = new AtomicBoolean(false);
	private final Context applicationContext;
	private final VoiceServiceConnector voiceServiceConnector;

	public BackgroundTask(CommonLogic commonLogic, Context context) {
		if (Looper.myLooper() == null) {
			Looper.prepare();
		}
		myLooper = Looper.myLooper();
		this.commonLogic = commonLogic;
		applicationContext = context.getApplicationContext();

		sensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
		connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);

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
		exitBroadcastReceiver = new ExitBroadcastReceiver(commonLogic);
		locationSender = new LocationSender(commonLogic);
		accMagSensorEventListener = new AccMagSensorEventListener(commonLogic,
				windowManager);
		orientationSensorEventListener = new OrientationSensorEventListener(
				commonLogic, windowManager);
		vehicleNotificationReceiver = new VehicleNotificationReceiver(
				commonLogic);
		nextDateChecker = new NextDateChecker(commonLogic);
		temperatureSensorEventListener = new TemperatureSensorEventListener(
				commonLogic);
		signalStrengthListener = new SignalStrengthListener(commonLogic,
				connectivityManager);
		operationScheduleReceiveThread = new OperationScheduleReceiveThread(
				commonLogic);
		voiceServiceConnector = new VoiceServiceConnector(context);
	}

	/**
	 * Looper.loop()のループに入る。
	 * quitの実行で終了する。またLooper.loop()が始まる前にスレッドがinterruptされていた場合も終了する。
	 */
	public void loop() {
		try {
			onLoopStart();
			Thread.sleep(0); // interruption point
			Looper.loop();
		} catch (InterruptedException e) {
			// do nothing
		} catch (ExecutionException e) {
			Log.w(TAG, e);
		} finally {
			onLoopStop();
		}
	}

	private void onLoopStart() throws InterruptedException, ExecutionException {
		for (Object object : new Object[] { operationScheduleReceiveThread,
				vehicleNotificationReceiver, locationSender,
				temperatureSensorEventListener, orientationSensorEventListener,
				exitBroadcastReceiver, signalStrengthListener,
				voiceServiceConnector }) {
			commonLogic.registerEventListener(object);
		}

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BackgroundTask.ACTION_EXIT);
		applicationContext
				.registerReceiver(exitBroadcastReceiver, intentFilter);

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

		List<Sensor> temperatureSensors = sensorManager
				.getSensorList(Sensor.TYPE_TEMPERATURE);
		if (temperatureSensors.size() > 0) {
			Sensor sensor = temperatureSensors.get(0);
			sensorManager.registerListener(temperatureSensorEventListener,
					sensor, SensorManager.SENSOR_DELAY_UI);
		}

		List<Sensor> accelerometerSensors = sensorManager
				.getSensorList(Sensor.TYPE_ACCELEROMETER);
		if (accelerometerSensors.size() > 0) {
			Sensor sensor = accelerometerSensors.get(0);
			sensorManager.registerListener(accMagSensorEventListener, sensor,
					SensorManager.SENSOR_DELAY_UI);
		}

		List<Sensor> magneticFieldSensors = sensorManager
				.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
		if (magneticFieldSensors.size() > 0) {
			Sensor sensor = magneticFieldSensors.get(0);
			sensorManager.registerListener(accMagSensorEventListener, sensor,
					SensorManager.SENSOR_DELAY_UI);
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

		applicationContext.startService(new Intent(applicationContext,
				StartupService.class));
		applicationContext.startService(new Intent(applicationContext,
				VoiceService.class));

		try {
			executorService.scheduleWithFixedDelay(vehicleNotificationReceiver,
					0, POLLING_PERIOD_MILLIS, TimeUnit.MILLISECONDS);
			executorService.scheduleWithFixedDelay(nextDateChecker, 0,
					POLLING_PERIOD_MILLIS, TimeUnit.MILLISECONDS);
			executorService.scheduleWithFixedDelay(locationSender, 0,
					POLLING_PERIOD_MILLIS, TimeUnit.MILLISECONDS);
		} catch (RejectedExecutionException e) {
			Log.w(TAG, e);
			quit();
		}
	}

	private void onLoopStop() {
		operationScheduleReceiveThread.interrupt();
		sensorManager.unregisterListener(temperatureSensorEventListener);
		sensorManager.unregisterListener(orientationSensorEventListener);
		sensorManager.unregisterListener(accMagSensorEventListener);
		applicationContext.unregisterReceiver(exitBroadcastReceiver);
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