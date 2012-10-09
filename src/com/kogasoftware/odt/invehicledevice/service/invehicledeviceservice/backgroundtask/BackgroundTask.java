package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.backgroundtask;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.acra.ACRA;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.datasource.DataSource;
import com.kogasoftware.odt.invehicledevice.datasource.DataSourceFactory;
import com.kogasoftware.odt.invehicledevice.datasource.WebAPIDataSource;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.SharedPreferencesKeys;
import com.kogasoftware.odt.invehicledevice.ui.BigToast;

/**
 * バックグランドでの処理を管理するクラス
 *
 * 注意: quit以外のメソッドは全て同じスレッドで実行する
 */
public class BackgroundTask {
	private static final String TAG = BackgroundTask.class.getSimpleName();
	private static final long POLLING_PERIOD_MILLIS = 30 * 1000;
	private static final Integer NUM_THREADS = 3;
	private static final Integer ERROR_MESSAGE_THREAD_EXIT_MILLIS = 15 * 1000;

	private final SensorManager sensorManager;
	private final Optional<TelephonyManager> optionalTelephonyManager;
	private final ServiceUnitStatusLogSender serviceUnitStatusLogSender;
	private final TemperatureSensorEventListener temperatureSensorEventListener;
	private final AccMagSensorEventListener accMagSensorEventListener;
	private final OrientationSensorEventListener orientationSensorEventListener;
	private final LocationNotifier locationNotifier;
	private final ScheduledExecutorService executorService = Executors
			.newScheduledThreadPool(NUM_THREADS);
	private final VehicleNotificationReceiver vehicleNotificationReceiver;
	private final NextDateNotifier nextDateChecker;
	private final SignalStrengthListener signalStrengthListener;
	private final ExitBroadcastReceiver exitBroadcastReceiver;
	private final BatteryBroadcastReceiver batteryBroadcastReceiver;
	private final Thread operationScheduleReceiveThread;
	private final Thread serviceProviderReceiveThread;
	private final Looper myLooper;
	private final AtomicBoolean quitCalled = new AtomicBoolean(false);
	private final InVehicleDeviceService service;
	private final Context applicationContext;
	private final Handler handler;
	private final AtomicBoolean loopStopped = new AtomicBoolean(false);

	public BackgroundTask(InVehicleDeviceService service) {
		this.service = service;
		if (Looper.myLooper() == null) {
			Looper.prepare();
		}
		applicationContext = service.getApplicationContext();
		myLooper = Looper.myLooper();
		handler = new Handler();

		sensorManager = (SensorManager) applicationContext
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
			tempTelephonyManager = Optional
					.of((TelephonyManager) applicationContext
							.getSystemService(Context.TELEPHONY_SERVICE));
		} catch (NullPointerException e) {
			Log.w(TAG, e);
		}
		optionalTelephonyManager = tempTelephonyManager;
		exitBroadcastReceiver = new ExitBroadcastReceiver(service);
		batteryBroadcastReceiver = new BatteryBroadcastReceiver();
		serviceUnitStatusLogSender = new ServiceUnitStatusLogSender(service);
		accMagSensorEventListener = new AccMagSensorEventListener(service);
		orientationSensorEventListener = new OrientationSensorEventListener(
				service);
		vehicleNotificationReceiver = new VehicleNotificationReceiver(service);
		nextDateChecker = new NextDateNotifier(service);
		temperatureSensorEventListener = new TemperatureSensorEventListener(
				service);
		signalStrengthListener = new SignalStrengthListener(service);
		locationNotifier = new LocationNotifier(service);
		operationScheduleReceiveThread = new OperationScheduleReceiveThread(
				service);
		serviceProviderReceiveThread = new ServiceProviderReceiveThread(service);
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
		} catch (ExecutionException e) {
			Log.w(TAG, e);
		} finally {
			onLoopStop();
		}
	}

	protected void onLoopStart() throws InterruptedException,
			ExecutionException {
		Log.i(TAG, "onLoopStart()");

		ACRA.getErrorReporter().handleSilentException(
				new Throwable("APPLICATION_START_LOG"));

		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(service);
		Boolean initialized = preferences.getBoolean(
				SharedPreferencesKeys.INITIALIZED, false);
		if (!initialized) {
			Log.w(TAG,
					"!SharedPreferences.getBoolean(SharedPreferencesKeys.INITIALIZED, false)");
			new HandlerThread("") {
				@Override
				protected void onLooperPrepared() {
					BigToast.makeText(
							service.getApplicationContext(),
							applicationContext
									.getString(R.string.settings_are_not_initialized),
							Toast.LENGTH_LONG).show();
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_MAIN);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					String packageName = "com.kogasoftware.odt.invehicledevice.preference";
					intent.setClassName(packageName, packageName
							+ ".InVehicleDevicePreferenceActivity");
					try {
						applicationContext.startActivity(intent);
					} catch (ActivityNotFoundException e) {
						Log.w(TAG, e);
					}
					Handler handler = new Handler(getLooper());
					Boolean posted = handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							quit();
						}
					}, ERROR_MESSAGE_THREAD_EXIT_MILLIS);
					if (!posted) {
						quit();
					}
				}
			}.start();
			myLooper.quit();
			return;
		}

		String url = preferences.getString(SharedPreferencesKeys.SERVER_URL,
				WebAPIDataSource.DEFAULT_URL);
		String token = preferences.getString(
				SharedPreferencesKeys.SERVER_IN_VEHICLE_DEVICE_TOKEN, "");
		File webAPIBackupFile = service.getFileStreamPath("webapi.serialized");
		if (preferences.getBoolean(SharedPreferencesKeys.CLEAR_WEBAPI_BACKUP,
				false)) {
			if (webAPIBackupFile.exists() && !webAPIBackupFile.delete()) {
				Log.w(TAG, "!\"" + webAPIBackupFile + "\".delete()");
			}
			SharedPreferences.Editor editor = preferences.edit();
			editor.putBoolean(SharedPreferencesKeys.CLEAR_WEBAPI_BACKUP, false);
			editor.commit();
		}
		DataSource remoteDataSource = DataSourceFactory.newInstance(url, token,
				webAPIBackupFile);
		service.setRemoteDataSource(remoteDataSource);
		LocalDataSource localDataSource = new LocalDataSource(service);
		service.setLocalDataSource(localDataSource);

		IntentFilter exitIntentFilter = new IntentFilter();
		exitIntentFilter.addAction(Broadcasts.ACTION_EXIT);
		applicationContext.registerReceiver(exitBroadcastReceiver,
				exitIntentFilter);

		IntentFilter batteryIntentFilter = new IntentFilter();
		batteryIntentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
		applicationContext.registerReceiver(batteryBroadcastReceiver,
				batteryIntentFilter);

		locationNotifier.start();

		operationScheduleReceiveThread.start();
		serviceProviderReceiveThread.start();

		if (!service.isOperationInitialized()) {
			service.startNewOperation();
			service.waitForOperationInitialize();
		}

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

		try {
			executorService.scheduleWithFixedDelay(vehicleNotificationReceiver,
					0, POLLING_PERIOD_MILLIS, TimeUnit.MILLISECONDS);
			executorService.scheduleWithFixedDelay(nextDateChecker, 0,
					POLLING_PERIOD_MILLIS, TimeUnit.MILLISECONDS);
			executorService.scheduleWithFixedDelay(serviceUnitStatusLogSender,
					0, POLLING_PERIOD_MILLIS, TimeUnit.MILLISECONDS);
		} catch (RejectedExecutionException e) {
			Log.w(TAG, e);
			quit();
		}
	}

	protected void onLoopStop() {
		if (loopStopped.getAndSet(true)) {
			return;
		}
		Log.i(TAG, "onLoopStop()");
		operationScheduleReceiveThread.interrupt();
		serviceProviderReceiveThread.interrupt();
		locationNotifier.stop();
		sensorManager.unregisterListener(temperatureSensorEventListener);
		sensorManager.unregisterListener(orientationSensorEventListener);
		// sensorManager.unregisterListener(accMagSensorEventListener);
		try {
			applicationContext.unregisterReceiver(exitBroadcastReceiver);
		} catch (IllegalArgumentException e) {
			Log.i(TAG, "unregisterReceiver(exitBroadcastReceiver) failed", e);
		}
		try {
			applicationContext.unregisterReceiver(batteryBroadcastReceiver);
		} catch (IllegalArgumentException e) {
			Log.i(TAG, "unregisterReceiver(batteryBroadcastReceiver) failed", e);
		}
		for (TelephonyManager telephonyManager : optionalTelephonyManager
				.asSet()) {
			telephonyManager.listen(signalStrengthListener,
					PhoneStateListener.LISTEN_NONE);
		}
		executorService.shutdownNow();
		service.exit();
	}

	/**
	 * loop()を終了する。loop()に入っていない状態でもloop()は終了する。
	 */
	public void quit() {
		// 二回以上呼ばれないようにする
		if (!quitCalled.getAndSet(true)) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					onLoopStop();
					myLooper.quit();
				}
			});
		}
	}
}