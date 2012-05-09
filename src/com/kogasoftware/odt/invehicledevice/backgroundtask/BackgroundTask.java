package com.kogasoftware.odt.invehicledevice.backgroundtask;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.ServiceUnitStatusLogs;
import com.kogasoftware.odt.invehicledevice.logic.Status;
import com.kogasoftware.odt.invehicledevice.logic.Status.Phase;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.VoidReader;
import com.kogasoftware.odt.invehicledevice.logic.event.CommonLogicLoadCompleteEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.PauseEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.StopEvent;
import com.kogasoftware.odt.invehicledevice.ui.modalview.NotificationModalView;

/**
 * バックグランドでの処理を管理するクラス
 * 
 * 注意: quit以外のメソッドは全て同じスレッドで実行する
 */
public class BackgroundTask {
	private static final String TAG = BackgroundTaskThread.class
			.getSimpleName();
	private static final long POLLING_PERIOD_MILLIS = 5000;
	private static final Integer NUM_THREADS = 3;

	private final LocationManager locationManager;
	private final SensorManager sensorManager;
	private final TelephonyManager telephonyManager;
	private final ConnectivityManager connectivityManager;
	private final SharedPreferences sharedPreferences;
	private final LocationSender locationSender;
	private final ExitRequiredPreferenceChangeListener exitRequiredPreferenceChangeListener;
	private final TemperatureSensorEventListener temperatureSensorEventListener;
	private final OrientationSensorEventListener orientationSensorEventListener;
	private final CountDownLatch completeLatch = new CountDownLatch(1);
	private final ScheduledExecutorService executorService = Executors
			.newScheduledThreadPool(NUM_THREADS);
	private final VehicleNotificationReceiver vehicleNotificationReceiver;
	private final VehicleNotificationSender vehicleNotificationSender;
	private final OperationScheduleSender operationScheduleSender;
	private final PassengerRecordSender passengerRecordSender;
	private final SignalStrengthListener signalStrengthListener;
	private final CommonLogic commonLogic;
	private final Thread voiceThread;
	private final Thread operationScheduleReceiveThread;
	private final Looper myLooper;

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
		telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);

		exitRequiredPreferenceChangeListener = new ExitRequiredPreferenceChangeListener(
				commonLogic);
		locationSender = new LocationSender(commonLogic);
		orientationSensorEventListener = new OrientationSensorEventListener(
				commonLogic);
		vehicleNotificationReceiver = new VehicleNotificationReceiver(
				commonLogic);
		vehicleNotificationSender = new VehicleNotificationSender(commonLogic);
		operationScheduleSender = new OperationScheduleSender(commonLogic);
		passengerRecordSender = new PassengerRecordSender(commonLogic);
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

		executorService.scheduleWithFixedDelay(vehicleNotificationReceiver, 0,
				POLLING_PERIOD_MILLIS, TimeUnit.MILLISECONDS);
		executorService.scheduleWithFixedDelay(vehicleNotificationSender, 0,
				POLLING_PERIOD_MILLIS, TimeUnit.MILLISECONDS);
		executorService.scheduleWithFixedDelay(operationScheduleSender, 0,
				POLLING_PERIOD_MILLIS, TimeUnit.MILLISECONDS);
		executorService.scheduleWithFixedDelay(passengerRecordSender, 0,
				POLLING_PERIOD_MILLIS, TimeUnit.MILLISECONDS);
		executorService.scheduleWithFixedDelay(locationSender, 0,
				POLLING_PERIOD_MILLIS, TimeUnit.MILLISECONDS);

		voiceThread.start();
		operationScheduleReceiveThread.start();

		if (commonLogic.getPhase() != Phase.FINISH
				&& commonLogic.getRemainingOperationSchedules().isEmpty()) {
			commonLogic.waitForOperationScheduleInitialize();
		}

		for (Object object : new Object[] { voiceThread,
				operationScheduleReceiveThread, vehicleNotificationReceiver,
				vehicleNotificationSender, operationScheduleSender,
				passengerRecordSender, locationSender,
				temperatureSensorEventListener, orientationSensorEventListener,
				exitRequiredPreferenceChangeListener, signalStrengthListener, }) {
			commonLogic.registerEventListener(object);
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
				LocationManager.NETWORK_PROVIDER, 2000, 1, locationSender);

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

		telephonyManager.listen(signalStrengthListener,
				PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
		commonLogic.postEvent(new NotificationModalView.ShowEvent());
		commonLogic.getStatusAccess().read(new VoidReader() {
			@Override
			public void read(Status status) {
				if (!status.serviceUnitStatusLog.getStatus().isPresent()) {
					return;
				}

				if (status.serviceUnitStatusLog.getStatus().get()
						.equals(ServiceUnitStatusLogs.Status.STOP)) {
					commonLogic.postEvent(new StopEvent());
				} else if (status.serviceUnitStatusLog.getStatus().get()
						.equals(ServiceUnitStatusLogs.Status.PAUSE)) {
					commonLogic.postEvent(new PauseEvent());
				}
			}
		});
	}

	private void onLoopStop() {
		voiceThread.interrupt();
		operationScheduleReceiveThread.interrupt();
		sharedPreferences
				.unregisterOnSharedPreferenceChangeListener(exitRequiredPreferenceChangeListener);
		locationManager.removeUpdates(locationSender);
		sensorManager.unregisterListener(temperatureSensorEventListener);
		sensorManager.unregisterListener(orientationSensorEventListener);
		telephonyManager.listen(signalStrengthListener,
				PhoneStateListener.LISTEN_NONE);
		executorService.shutdownNow();
	}

	/**
	 * loop()を終了する。loop()に入っていない状態でもloop()は終了する。
	 */
	public void quit() {
		myLooper.quit();
	}
}