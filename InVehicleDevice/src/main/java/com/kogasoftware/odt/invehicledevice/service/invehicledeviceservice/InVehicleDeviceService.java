package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.WeakHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.acra.ACRA;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.Pair;

import android.app.Activity;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.Uninterruptibles;
import com.kogasoftware.odt.invehicledevice.BuildConfig;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.apiclient.InVehicleDeviceApiClient;
import com.kogasoftware.odt.invehicledevice.apiclient.InVehicleDeviceApiClientFactory;
import com.kogasoftware.odt.invehicledevice.apiclient.model.VehicleNotification;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.EventDispatcher.OnPauseActivityListener;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData.VehicleNotificationStatus;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage.BackgroundReader;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage.Reader;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.broadcast.BatteryBroadcastReceiver;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.broadcast.Broadcasts;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.broadcast.ExitBroadcastReceiver;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.logic.OperationScheduleLogic;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.logic.ServiceUnitStatusLogLogic;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.logic.VehicleNotificationLogic;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.scheduledtask.NetworkStatusLogger;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.scheduledtask.NextDateNotifier;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.scheduledtask.ServiceUnitStatusLogSender;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.scheduledtask.VehicleNotificationReceiver;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.sensor.AccMagSensorEventListener;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.sensor.OrientationSensorEventListener;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.sensor.SignalStrengthListener;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.sensor.TemperatureSensorEventListener;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.thread.OperationScheduleReceiveThread;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.thread.ServiceProviderReceiveThread;
import com.kogasoftware.odt.invehicledevice.service.trackingservice.TrackingIntent;
import com.kogasoftware.odt.invehicledevice.ui.BigToast;

public class InVehicleDeviceService extends Service implements
		OnPauseActivityListener {
	public class LocalBinder extends Binder {
		public InVehicleDeviceService getService() {
			return InVehicleDeviceService.this;
		}
	}

	private final EventDispatcher eventDispatcher = new EventDispatcher();

	public EventDispatcher getEventDispatcher() {
		return eventDispatcher;
	}

	public static enum PayTiming {
		GET_ON, GET_OFF,
	}

	public static final Integer NEW_SCHEDULE_DOWNLOAD_HOUR = 0;
	public static final Integer NEW_SCHEDULE_DOWNLOAD_MINUTE = 5;
	private static final Integer NUM_THREADS = 3;
	private static final Integer ERROR_MESSAGE_THREAD_EXIT_MILLIS = 15 * 1000;
	private final ScheduledExecutorService scheduledExecutorService = Executors
			.newScheduledThreadPool(NUM_THREADS);
	private static final WeakHashMap<Thread, Handler> HANDLERS = new WeakHashMap<Thread, Handler>();
	public static final Handler DEFAULT_HANDLER = new Handler(
			Looper.getMainLooper());
	public static final Integer EXECUTOR_SERVICE_THREADS = 3;

	@VisibleForTesting
	public static void putThreadHandler(Thread thread, Handler handler) {
		synchronized (HANDLERS) {
			HANDLERS.put(thread, handler);
		}
	}

	public static Handler getThreadHandler() {
		synchronized (HANDLERS) {
			Thread currentThread = Thread.currentThread();
			if (HANDLERS.containsKey(currentThread)) {
				return HANDLERS.get(currentThread);
			}
			Looper looper = Looper.myLooper();
			if (looper == null) {
				if (BuildConfig.DEBUG) { 
					Log.w(TAG, "getThreadHandler() invoked no looper thread id="
							+ currentThread.getId() + " " + currentThread + " "
							+ ExceptionUtils.getStackTrace(new Throwable()));
				}
				return DEFAULT_HANDLER;
			} else {
				Handler handler = new Handler(looper);
				HANDLERS.put(currentThread, handler);
				return handler;
			}
		}
	}

	private static final String TAG = InVehicleDeviceService.class
			.getSimpleName();
	public static final String DEFAULT_URL = "http://127.0.0.1";

	public static Handler getActivityHandler(Activity activity)
			throws InterruptedException {
		final CountDownLatch latch = new CountDownLatch(1);
		final AtomicReference<Handler> handler = new AtomicReference<Handler>();
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				handler.set(new Handler());
				latch.countDown();
			}
		});
		latch.await();
		Preconditions.checkNotNull(handler.get());
		return handler.get();
	}

	/**
	 * initializeInBackground()を裏のスレッドで実行し 結果オブジェクトを表のスレッドでonPostInitialize()に渡す
	 * 
	 * 何故か普通にAsyncTaskやThreadを使うと裏のスレッドから結果オブジェクトやサービスへの参照が消えずに残ることがある。これは
	 * eclipseのMemory Analyzer Toolで確認できる。この現象の原因の解明がまだできていないため、次善策として防御的に
	 * 上記処理を行う際にサービスとのお互いの参照を弱参照で保持しておき、可能な限り参照が残らないように配慮するThread
	 */
	private static class InitializeThread extends Thread {
		private static class PostInitializeCallback implements Runnable {
			private final WeakReference<InVehicleDeviceService> serviceReference;
			private final Pair<LocalStorage, InVehicleDeviceApiClient> result;
			private final CountDownLatch initializeCompleted;

			public PostInitializeCallback(
					WeakReference<InVehicleDeviceService> serviceReference,
					Pair<LocalStorage, InVehicleDeviceApiClient> result,
					CountDownLatch initializeCompleted) {
				this.serviceReference = serviceReference;
				this.result = result;
				this.initializeCompleted = initializeCompleted;
			}

			@Override
			public void run() {
				InVehicleDeviceService service = serviceReference.get();
				if (service != null) {
					service.onPostInitialize(result);
				}
				initializeCompleted.countDown();
			}
		}

		private final Handler handler;
		private final WeakReference<InVehicleDeviceService> serviceReference;

		public InitializeThread(InVehicleDeviceService service, Handler handler) {
			super("InitializeThread");
			serviceReference = new WeakReference<InVehicleDeviceService>(
					service);
			this.handler = handler;
		}

		private Optional<Pair<LocalStorage, InVehicleDeviceApiClient>> initializeInBackground() {
			InVehicleDeviceService service = serviceReference.get();
			if (service != null) {
				return service.initializeInBackground();
			}
			return Optional.absent();
		}

		private void exit() {
			InVehicleDeviceService service = serviceReference.get();
			if (service != null) {
				service.exit();
			}
		}

		@Override
		public void run() {
			for (Pair<LocalStorage, InVehicleDeviceApiClient> result : initializeInBackground()
					.asSet()) {
				CountDownLatch initializeCompleted = new CountDownLatch(1);
				Runnable postInitializeCallback = new PostInitializeCallback(
						serviceReference, result, initializeCompleted);
				handler.post(postInitializeCallback);
				try {
					initializeCompleted.await();
					return;
				} catch (InterruptedException interruptedException) {
					// 処理が中断された場合、結果オブジェクトをクローズして終了
					result.getLeft().close();
					try {
						result.getRight().close();
					} catch (IOException ioException) {
						Log.w(TAG, ioException);
					}
				} finally {
					handler.removeCallbacks(postInitializeCallback);
				}
			}
			exit();
		}
	}

	protected final IBinder binder = new LocalBinder();
	protected final Handler handler = new Handler(Looper.getMainLooper());
	protected final VoiceServiceConnector voiceServiceConnector;
	protected InVehicleDeviceApiClient apiClient = InVehicleDeviceApiClientFactory
			.newInstance();
	protected LocalStorage localStorage = new LocalStorage();

	protected OperationScheduleLogic operationScheduleLogic;
	protected ServiceUnitStatusLogLogic serviceUnitStatusLogLogic;
	protected SensorManager sensorManager;
	protected Optional<TelephonyManager> optionalTelephonyManager;
	protected WindowManager windowManager;
	protected LocationManager locationManager;
	protected PowerManager powerManager;
	protected ConnectivityManager connectivityManager;
	protected ExitBroadcastReceiver exitBroadcastReceiver;
	protected BatteryBroadcastReceiver batteryBroadcastReceiver;
	protected final BroadcastReceiver trackingBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			TrackingIntent trackingIntent = new TrackingIntent(intent);
			serviceUnitStatusLogLogic.changeLocation(
					trackingIntent.getLocation(),
					Optional.of(trackingIntent.getSatellitesCount()));
		}
	};
	protected AccMagSensorEventListener accMagSensorEventListener;
	protected OrientationSensorEventListener orientationSensorEventListener;
	protected TemperatureSensorEventListener temperatureSensorEventListener;
	protected SignalStrengthListener signalStrengthListener;
	protected OperationScheduleReceiveThread operationScheduleReceiveThread;
	protected ServiceProviderReceiveThread serviceProviderReceiveThread;
	protected WeakReference<InitializeThread> initializeThread;
	protected Boolean destroyed = false;
	private WeakReference<InitializeThread> initializeThreadReference;

	public InVehicleDeviceService() {
		super();
		voiceServiceConnector = new VoiceServiceConnector(this);
	}

	public void exit() {
		eventDispatcher.dispatchExit();
		stopSelf();
	}

	public InVehicleDeviceApiClient getApiClient() {
		return apiClient;
	}

	public LocalStorage getLocalStorage() {
		return localStorage;
	}

	public EnumSet<PayTiming> getPayTiming() {
		return EnumSet.of(PayTiming.GET_ON);
	}

	public String getToken() {
		return localStorage.withReadLock(new Reader<String>() {
			@Override
			public String read(LocalData status) {
				return status.token;
			}
		});
	}

	public Boolean isOperationInitialized() {
		return localStorage.withReadLock(new Reader<Boolean>() {
			@Override
			public Boolean read(LocalData localData) {
				return isOperationInitialized(localData);
			}
		});
	}

	public Boolean isOperationInitialized(LocalData localData) {
		return (!localData.operation.operationScheduleReceiveSequence.equals(0) && localData.serviceProviderInitialized);
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.i(TAG, "onBind()");
		return binder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.i(TAG, "onUnbind()");
		return false;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "onCreate()");
		destroyed = false; // onDestroy()後にインスタンスが再利用「されない」という記述が見当たらないため、いちおう再設定する。

		ACRA.getErrorReporter().handleSilentException(
				new Throwable("APPLICATION_START_LOG"));

		operationScheduleLogic = new OperationScheduleLogic(this);
		serviceUnitStatusLogLogic = new ServiceUnitStatusLogLogic(this);

		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

		// TODO:内容精査
		// TelephonyManagerはNullPointerExceptionを発生させる
		// E/AndroidRuntime(24190):FATAL EXCEPTION: Thread-4030
		// E/AndroidRuntime(24190):java.lang.NullPointerException
		// E/AndroidRuntime(24190):at_android.telephony.TelephonyManager.<init>(TelephonyManager.java:71)
		// E/AndroidRuntime(24190):at_android.app.ContextImpl$26.createService(ContextImpl.java:410)
		// E/AndroidRuntime(24190):at_android.app.ContextImpl$ServiceFetcher.getService(ContextImpl.java:198)
		// E/AndroidRuntime(24190):at_android.app.ContextImpl.getSystemService(ContextImpl.java:1176)
		// E/AndroidRuntime(24190):at_com.kogasoftware.odt.invehicledevice.backgroundtask.BackgroundTask.<init>(BackgroundTask.java:78)
		// E/AndroidRuntime(24190):at_com.kogasoftware.odt.invehicledevice.backgroundtask.BackgroundTaskTestCase$1.run(BackgroundTaskTestCase.java:44)
		try {
			optionalTelephonyManager = Optional
					.of((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE));
		} catch (NullPointerException e) {
			Log.w(TAG, e);
		}

		windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		exitBroadcastReceiver = new ExitBroadcastReceiver(this);
		batteryBroadcastReceiver = new BatteryBroadcastReceiver();
		accMagSensorEventListener = new AccMagSensorEventListener(
				serviceUnitStatusLogLogic, windowManager);
		orientationSensorEventListener = new OrientationSensorEventListener(
				serviceUnitStatusLogLogic, windowManager);
		temperatureSensorEventListener = new TemperatureSensorEventListener(
				serviceUnitStatusLogLogic);
		signalStrengthListener = new SignalStrengthListener(
				serviceUnitStatusLogLogic);
		operationScheduleReceiveThread = new OperationScheduleReceiveThread(
				this);
		serviceProviderReceiveThread = new ServiceProviderReceiveThread(this);

		getEventDispatcher().addOnPauseActivityListener(voiceServiceConnector);
		getEventDispatcher().addOnResumeActivityListener(voiceServiceConnector);
		getEventDispatcher().addOnStartNewOperationListener(
				operationScheduleReceiveThread);
		getEventDispatcher().addOnStartReceiveUpdatedOperationScheduleListener(
				operationScheduleReceiveThread);
		getEventDispatcher().addOnStartNewOperationListener(
				serviceProviderReceiveThread);
		getEventDispatcher().addOnPauseActivityListener(this);

		{ // 弱参照から取り出した強参照の変数のスコープを限定
			InitializeThread initializeThread = new InitializeThread(this,
					new Handler());
			initializeThreadReference = new WeakReference<InitializeThread>(
					initializeThread);
			initializeThread.start();
		}
	}

	/**
	 * 設定アプリによって設定が完了していない場合の警告を出力するスレッド
	 */
	private static class PreferencesNotInitializedAlertThread extends HandlerThread {
		private final Context applicationContext; // Activityを起動する関係か参照を非常に長く保持するようなので、ApplicationContextを使う。

		@Override
		protected void onLooperPrepared() {
			super.onLooperPrepared();
			BigToast.makeText(
					applicationContext,
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
			Handler handler = new Handler();
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

		public PreferencesNotInitializedAlertThread(Context context) {
			super(PreferencesNotInitializedAlertThread.class.getSimpleName());
			applicationContext = context.getApplicationContext();
		}
	}

	protected Optional<Pair<LocalStorage, InVehicleDeviceApiClient>> initializeInBackground() {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		Boolean initialized = preferences.getBoolean(
				SharedPreferencesKeys.INITIALIZED, false);
		if (!initialized) {
			Log.w(TAG,
					"!SharedPreferences.getBoolean(SharedPreferencesKeys.INITIALIZED, false)");
			new PreferencesNotInitializedAlertThread(this).start();
			Uninterruptibles
					.sleepUninterruptibly(ERROR_MESSAGE_THREAD_EXIT_MILLIS / 3,
							TimeUnit.MILLISECONDS);
			return Optional.absent();
		}
		String url = preferences.getString(SharedPreferencesKeys.SERVER_URL,
				InVehicleDeviceService.DEFAULT_URL);
		String token = preferences.getString(
				SharedPreferencesKeys.SERVER_IN_VEHICLE_DEVICE_TOKEN, "");
		File webAPIBackupFile = getFileStreamPath("webapi.serialized");
		if (preferences.getBoolean(SharedPreferencesKeys.CLEAR_WEBAPI_BACKUP,
				false)) {
			if (webAPIBackupFile.exists() && !webAPIBackupFile.delete()) {
				Log.w(TAG, "!\"" + webAPIBackupFile + "\".delete()");
			}
			SharedPreferences.Editor editor = preferences.edit();
			editor.putBoolean(SharedPreferencesKeys.CLEAR_WEBAPI_BACKUP, false);
			editor.commit();
		}
		InVehicleDeviceApiClient apiClient = InVehicleDeviceApiClientFactory
				.newInstance(url, token, webAPIBackupFile);
		LocalStorage localStorage = new LocalStorage(this);
		return Optional.of(Pair.of(localStorage, apiClient)); // これらのオブジェクトは確実にclose()しなければならない
	}

	protected void onPostInitialize(
			Pair<LocalStorage, InVehicleDeviceApiClient> result) {
		if (destroyed) {
			result.getLeft().close();
			try {
				result.getRight().close();
			} catch (IOException e) {
				Log.w(TAG, e);
			}
			return;
		}
		localStorage = result.getLeft();
		apiClient = result.getRight();

		registerReceiver(exitBroadcastReceiver, new IntentFilter(
				Broadcasts.ACTION_EXIT));

		registerReceiver(batteryBroadcastReceiver, new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED));

		registerReceiver(trackingBroadcastReceiver, new IntentFilter(
				TrackingIntent.ACTION_TRACKING));

		operationScheduleReceiveThread.start();
		serviceProviderReceiveThread.start();

		operationScheduleLogic.startNewOperation(false);

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

		ServiceUnitStatusLogSender serviceUnitStatusLogSender = new ServiceUnitStatusLogSender(
				serviceUnitStatusLogLogic, operationScheduleLogic);
		NextDateNotifier nextDateNotifier = new NextDateNotifier(
				operationScheduleLogic);
		NetworkStatusLogger networkStatusLogger = new NetworkStatusLogger(
				connectivityManager, optionalTelephonyManager);
		try {
			scheduledExecutorService.scheduleWithFixedDelay(nextDateNotifier,
					0, NextDateNotifier.RUN_INTERVAL_MILLIS,
					TimeUnit.MILLISECONDS);
			scheduledExecutorService.scheduleWithFixedDelay(
					serviceUnitStatusLogSender, 0,
					ServiceUnitStatusLogSender.RUN_INTERVAL_MILLIS,
					TimeUnit.MILLISECONDS);
			scheduledExecutorService.scheduleWithFixedDelay(
					networkStatusLogger, 0,
					NetworkStatusLogger.RUN_INTERVAL_MILLIS,
					TimeUnit.MILLISECONDS);
		} catch (RejectedExecutionException e) {
			Log.w(TAG, e);
			exit();
		}

		restoreUnhandledVehicleNotifications();
	}

	private void restoreUnhandledVehicleNotifications() {
		getLocalStorage()
				.read(new BackgroundReader<Pair<LinkedList<VehicleNotification>, LinkedList<VehicleNotification>>>() {
					@Override
					public Pair<LinkedList<VehicleNotification>, LinkedList<VehicleNotification>> readInBackground(
							LocalData localData) {
						LinkedList<VehicleNotification> fromOperator = VehicleNotificationLogic
								.get(VehicleNotification.NotificationKind.FROM_OPERATOR,
										VehicleNotificationStatus.UNHANDLED,
										localData.vehicleNotifications);
						LinkedList<VehicleNotification> reservationChanged = VehicleNotificationLogic
								.get(VehicleNotification.NotificationKind.RESERVATION_CHANGED,
										VehicleNotificationStatus.UNHANDLED,
										localData.vehicleNotifications);
						return Pair.of(fromOperator, reservationChanged);
					}

					@Override
					public void onRead(
							Pair<LinkedList<VehicleNotification>, LinkedList<VehicleNotification>> result) {
						if (destroyed) {
							return;
						}
						LinkedList<VehicleNotification> fromOperator = result
								.getLeft();
						LinkedList<VehicleNotification> reservationChanged = result
								.getRight();
						if (!fromOperator.isEmpty()) {
							getEventDispatcher()
									.dispatchAlertVehicleNotificationReceive(
											fromOperator);
						}
						if (!reservationChanged.isEmpty()) {
							getEventDispatcher()
									.dispatchStartReceiveUpdatedOperationSchedule();
						}
						try {
							scheduledExecutorService
									.scheduleWithFixedDelay(
											new VehicleNotificationReceiver(
													InVehicleDeviceService.this),
											0,
											VehicleNotificationReceiver.RUN_INTERVAL_MILLIS,
											TimeUnit.MILLISECONDS);
						} catch (RejectedExecutionException e) {
							Log.w(TAG, e);
						}
					}
				});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "onDestroy()");
		destroyed = true;

		{ // 弱参照から取り出した強参照の変数のスコープを限定
			InitializeThread initializeThread = initializeThreadReference.get();
			if (initializeThread != null) {
				initializeThread.interrupt();
			}
		}

		getEventDispatcher().removeOnPauseActivityListener(
				voiceServiceConnector);
		getEventDispatcher().removeOnResumeActivityListener(
				voiceServiceConnector);
		getEventDispatcher().removeOnStartNewOperationListener(
				operationScheduleReceiveThread);
		getEventDispatcher()
				.removeOnStartReceiveUpdatedOperationScheduleListener(
						operationScheduleReceiveThread);
		getEventDispatcher().removeOnStartNewOperationListener(
				serviceProviderReceiveThread);
		getEventDispatcher().removeOnPauseActivityListener(this);

		operationScheduleReceiveThread.interrupt();
		serviceProviderReceiveThread.interrupt();
		sensorManager.unregisterListener(temperatureSensorEventListener);
		sensorManager.unregisterListener(orientationSensorEventListener);
		// sensorManager.unregisterListener(accMagSensorEventListener);
		try {
			unregisterReceiver(exitBroadcastReceiver);
		} catch (IllegalArgumentException e) {
			Log.i(TAG, "unregisterReceiver(exitBroadcastReceiver) failed", e);
		}
		try {
			unregisterReceiver(batteryBroadcastReceiver);
		} catch (IllegalArgumentException e) {
			Log.i(TAG, "unregisterReceiver(batteryBroadcastReceiver) failed", e);
		}
		try {
			unregisterReceiver(trackingBroadcastReceiver);
		} catch (IllegalArgumentException e) {
			Log.i(TAG, "unregisterReceiver(trackingBroadcastReceiver) failed",
					e);
		}
		for (TelephonyManager telephonyManager : optionalTelephonyManager
				.asSet()) {
			telephonyManager.listen(signalStrengthListener,
					PhoneStateListener.LISTEN_NONE);
		}
		scheduledExecutorService.shutdownNow();

		eventDispatcher.close();
		try {
			apiClient.close();
		} catch (IOException e) {
			Log.w(TAG, e);
		}
		localStorage.close();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	public void speak(String message) {
		voiceServiceConnector.speak(message);
	}

	public ScheduledExecutorService getScheduledExecutorService() {
		return scheduledExecutorService;
	}

	private Boolean mapAutoZoom = true;
	private Integer mapZoomLevel = 12;
	private Optional<Bitmap> lastMapBitmap = Optional.absent();

	public Boolean getMapAutoZoom() {
		return mapAutoZoom;
	}

	public Integer getMapZoomLevel() {
		return mapZoomLevel;
	}

	public void setMapAutoZoom(Boolean mapAutoZoom) {
		this.mapAutoZoom = mapAutoZoom;
	}

	public void setMapZoomLevel(Integer mapZoomLevel) {
		this.mapZoomLevel = mapZoomLevel;
	}

	public void setLastMapBitmap(Bitmap lastMapBitmap) {
		for (Bitmap presentLastMapBitmap : getLastMapBitmap().asSet()) {
			presentLastMapBitmap.recycle();
		}
		this.lastMapBitmap = Optional.of(lastMapBitmap);
	}

	public Optional<Bitmap> getLastMapBitmap() {
		return lastMapBitmap;
	}

	@Override
	public void onPauseActivity() {
		// メモリ枯渇の際、onDestroyを呼ばれずにサービスが終了してしまうことがあるため、
		// 必ず呼ばれるActivityのonPause時にデータを強制的に保存する
		localStorage.flush();
	}
}
