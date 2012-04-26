package com.kogasoftware.odt.invehicledevice.backgroundtask;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
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
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.CommonLogic;
import com.kogasoftware.odt.invehicledevice.Status.Phase;
import com.kogasoftware.odt.invehicledevice.event.CommonLogicLoadCompleteEvent;

class BackgroundTask {
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

	public BackgroundTask(Activity activity) {
		Looper.prepare();
		myLooper = Looper.myLooper();

		commonLogic = new CommonLogic(activity);

		locationManager = (LocationManager) activity
				.getSystemService(Context.LOCATION_SERVICE);
		sensorManager = (SensorManager) activity
				.getSystemService(Context.SENSOR_SERVICE);
		telephonyManager = (TelephonyManager) activity
				.getSystemService(Context.TELEPHONY_SERVICE);
		connectivityManager = (ConnectivityManager) activity
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(activity);

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
		voiceThread = new VoiceThread(activity);
		operationScheduleReceiveThread = new OperationScheduleReceiveThread(
				commonLogic);
	}

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

		EventBus eventBus = commonLogic.getEventBus();
		for (Object object : new Object[] { voiceThread,
				operationScheduleReceiveThread, vehicleNotificationReceiver,
				vehicleNotificationSender, operationScheduleSender,
				passengerRecordSender, locationSender,
				temperatureSensorEventListener, orientationSensorEventListener,
				exitRequiredPreferenceChangeListener, signalStrengthListener }) {
			eventBus.register(object);
		}

		eventBus.register(new Function<CommonLogicLoadCompleteEvent, Void>() {
			@Subscribe
			@Override
			public Void apply(CommonLogicLoadCompleteEvent e) {
				completeLatch.countDown();
				return null;
			}
		});

		eventBus.post(new CommonLogicLoadCompleteEvent(commonLogic));
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
		commonLogic.dispose();
		executorService.shutdownNow();
	}

	public void quit() {
		myLooper.quit();
	}
}