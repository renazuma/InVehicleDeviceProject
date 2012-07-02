package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.backgroundthread;

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.acra.ErrorReporter;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.DropBoxManager;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.datasource.DataSource;
import com.kogasoftware.odt.invehicledevice.datasource.DataSourceFactory;
import com.kogasoftware.odt.invehicledevice.datasource.WebAPIDataSource;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.SharedPreferencesKey;
import com.kogasoftware.odt.invehicledevice.service.startupservice.StartupService;
import com.kogasoftware.odt.invehicledevice.service.voiceservice.VoiceService;

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
	private final Optional<TelephonyManager> optionalTelephonyManager;
	private final ServiceUnitStatusLogSender locationSender;
	private final TemperatureSensorEventListener temperatureSensorEventListener;
	private final AccMagSensorEventListener accMagSensorEventListener;
	private final OrientationSensorEventListener orientationSensorEventListener;
	private final LocationNotifier locationListener;
	private final ScheduledExecutorService executorService = Executors
			.newScheduledThreadPool(NUM_THREADS);
	private final VehicleNotificationReceiver vehicleNotificationReceiver;
	private final NextDateNotifier nextDateChecker;
	private final SignalStrengthListener signalStrengthListener;
	private final ExitBroadcastReceiver exitBroadcastReceiver;
	private final Thread operationScheduleReceiveThread;
	private final Looper myLooper;
	private final AtomicBoolean quitCalled = new AtomicBoolean(false);
	private final InVehicleDeviceService service;

	public BackgroundTask(InVehicleDeviceService service) {
		this.service = service;
		if (Looper.myLooper() == null) {
			Looper.prepare();
		}
		myLooper = Looper.myLooper();

		sensorManager = (SensorManager) service
				.getSystemService(Context.SENSOR_SERVICE);

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
			tempTelephonyManager = Optional.of((TelephonyManager) service
					.getSystemService(Context.TELEPHONY_SERVICE));
		} catch (NullPointerException e) {
			Log.w(TAG, e);
		}
		optionalTelephonyManager = tempTelephonyManager;
		exitBroadcastReceiver = new ExitBroadcastReceiver(service);
		locationSender = new ServiceUnitStatusLogSender(service);
		accMagSensorEventListener = new AccMagSensorEventListener(service);
		orientationSensorEventListener = new OrientationSensorEventListener(
				service);
		vehicleNotificationReceiver = new VehicleNotificationReceiver(service);
		nextDateChecker = new NextDateNotifier(service);
		temperatureSensorEventListener = new TemperatureSensorEventListener(
				service);
		signalStrengthListener = new SignalStrengthListener(service);
		locationListener = new LocationNotifier(service);
		operationScheduleReceiveThread = new OperationScheduleReceiveThread(
				service);
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

	protected void onLoopStart() throws InterruptedException,
			ExecutionException {

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BackgroundTask.ACTION_EXIT);
		service.getApplicationContext().registerReceiver(exitBroadcastReceiver,
				intentFilter);

		StringBuilder trace = new StringBuilder();
		{
			DropBoxManager dropBoxManager = (DropBoxManager) service
					.getSystemService(Context.DROPBOX_SERVICE);
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.MINUTE, -10);
			Long last = calendar.getTimeInMillis();
			for (Integer i = 0; i < 5; ++i) {
				DropBoxManager.Entry entry = dropBoxManager.getNextEntry(
						"data_app_anr", last);
				if (entry == null) {
					break;
				}
				last = entry.getTimeMillis();
				trace.append(entry.getText(1024 * 1024) + "\n");
			}
		}

		ErrorReporter errorReporter = ErrorReporter.getInstance();
		String customKey = "anr_traces";
		errorReporter.putCustomData(customKey, trace.toString());
		errorReporter.handleSilentException(new Throwable(
				"APPLICATION_START_LOG"));
		errorReporter.removeCustomData(customKey);

		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(service);
		String url = preferences.getString(SharedPreferencesKey.SERVER_URL,
				WebAPIDataSource.DEFAULT_URL);
		String token = preferences.getString(
				SharedPreferencesKey.SERVER_IN_VEHICLE_DEVICE_TOKEN, "");
		File webAPIBackupFile = service.getFileStreamPath("webapi.serialized");
		if (preferences.getBoolean(SharedPreferencesKey.CLEAR_WEBAPI_BACKUP,
				false)) {
			if (webAPIBackupFile.exists() && !webAPIBackupFile.delete()) {
				Log.w(TAG, "!\"" + webAPIBackupFile + "\".delete()");
			}
			SharedPreferences.Editor editor = preferences.edit();
			editor.putBoolean(SharedPreferencesKey.CLEAR_WEBAPI_BACKUP, false);
			editor.commit();
		}
		DataSource dataSource = DataSourceFactory.newInstance(url, token,
				webAPIBackupFile);
		service.setDataSource(dataSource);
		LocalDataSource localDataSource = new LocalDataSource(service);
		service.setLocalDataSource(localDataSource);

		operationScheduleReceiveThread.start();
		if (!service.isOperationScheduleInitialized()) {
			service.startNewOperation();
			service.waitForOperationScheduleInitialize();
		} else {
			service.setInitialized();
		}

		locationListener.start();

		List<Sensor> temperatureSensors = sensorManager
				.getSensorList(Sensor.TYPE_TEMPERATURE);
		if (temperatureSensors.size() > 0) {
			Sensor sensor = temperatureSensors.get(0);
			sensorManager.registerListener(temperatureSensorEventListener,
					sensor, SensorManager.SENSOR_DELAY_UI);
		}

		// List<Sensor> accelerometerSensors = sensorManager
		// .getSensorList(Sensor.TYPE_ACCELEROMETER);
		// if (accelerometerSensors.size() > 0) {
		// Sensor sensor = accelerometerSensors.get(0);
		// sensorManager.registerListener(accMagSensorEventListener, sensor,
		// SensorManager.SENSOR_DELAY_UI);
		// }
		//
		// List<Sensor> magneticFieldSensors = sensorManager
		// .getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
		// if (magneticFieldSensors.size() > 0) {
		// Sensor sensor = magneticFieldSensors.get(0);
		// sensorManager.registerListener(accMagSensorEventListener, sensor,
		// SensorManager.SENSOR_DELAY_UI);
		// }

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

		service.startService(new Intent(service, StartupService.class));
		service.startService(new Intent(service, VoiceService.class));

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

	protected void onLoopStop() {
		operationScheduleReceiveThread.interrupt();
		locationListener.stop();
		sensorManager.unregisterListener(temperatureSensorEventListener);
		sensorManager.unregisterListener(orientationSensorEventListener);
		// sensorManager.unregisterListener(accMagSensorEventListener);
		service.getApplicationContext().unregisterReceiver(
				exitBroadcastReceiver);
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