package com.kogasoftware.odt.invehicledevice.service.serviceunitstatuslogservice;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.WindowManager;

import com.kogasoftware.odt.invehicledevice.R;

import java.util.List;

/**
 * ServiceUnitStatusLogを生成して保存するサービス
 * 
 * 「アンドロイド品質ガイドライン」の「FN-S1」に注意する必要がある
 */
public class ServiceUnitStatusLogService extends Service implements Runnable {
	private static final String TAG = ServiceUnitStatusLogService.class
			.getSimpleName();
	private final Handler handler = new Handler();
	private GpsLogger gpsLogger;
	private BatteryBroadcastReceiver batteryBroadcastReceiver;
	private WindowManager windowManager;
	private ConnectivityManager connectivityManager;
	private OrientationSensorEventListener orientationSensorEventListener;
	private TemperatureSensorEventListener temperatureSensorEventListener;
	private SignalStrengthListener signalStrengthListener;
	private SensorManager sensorManager;
	private TelephonyManager telephonyManager; // TODO: nullable
	private NetworkStatusLogger networkStatusLogger;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "onCreate()");

		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

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
			telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		} catch (NullPointerException e) {
			Log.w(TAG, e);
		}

		orientationSensorEventListener = new OrientationSensorEventListener(
				getContentResolver(), windowManager);
		temperatureSensorEventListener = new TemperatureSensorEventListener(
				getContentResolver());
		signalStrengthListener = new SignalStrengthListener(
				getContentResolver());

		gpsLogger = new GpsLogger(this);
		handler.post(this);

		Boolean useBatteryTemperature;
		List<Sensor> temperatureSensors = sensorManager
				.getSensorList(Sensor.TYPE_TEMPERATURE);
		if (temperatureSensors.size() > 0) {
			Sensor sensor = temperatureSensors.get(0);
			sensorManager.registerListener(temperatureSensorEventListener,
					sensor, SensorManager.SENSOR_DELAY_UI);
			useBatteryTemperature = false;
		} else {
			useBatteryTemperature = true;
		}
		Log.i(TAG, "useBatteryTemperature=" + useBatteryTemperature);
		batteryBroadcastReceiver = new BatteryBroadcastReceiver(
				getContentResolver(), useBatteryTemperature);

		List<Sensor> orientationSensors = sensorManager
				.getSensorList(Sensor.TYPE_ORIENTATION);
		if (orientationSensors.size() > 0) {
			Sensor sensor = orientationSensors.get(0);
			sensorManager.registerListener(orientationSensorEventListener,
					sensor, SensorManager.SENSOR_DELAY_UI);
		}

		if (telephonyManager != null) {
			telephonyManager.listen(signalStrengthListener,
					PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
		}

		networkStatusLogger = new NetworkStatusLogger(connectivityManager,
				telephonyManager);
		handler.post(networkStatusLogger);
		registerReceiver(batteryBroadcastReceiver, new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED));
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy()");
		super.onDestroy();
		handler.removeCallbacks(this);
		handler.removeCallbacks(networkStatusLogger);
		gpsLogger.close();

		sensorManager.unregisterListener(temperatureSensorEventListener);
		sensorManager.unregisterListener(orientationSensorEventListener);
		try {
			unregisterReceiver(batteryBroadcastReceiver);
		} catch (IllegalArgumentException e) {
			Log.i(TAG, "unregisterReceiver(batteryBroadcastReceiver) failed", e);
		}
		if (telephonyManager != null) {
			telephonyManager.listen(signalStrengthListener,
					PhoneStateListener.LISTEN_NONE);
		}
		stopForeground(true);
	}

	@Override
	public void run() {
		gpsLogger.run();
		handler.postDelayed(this, 500);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		Log.i(TAG, "onStartCommand(" + intent + ", " + flags + ", " + startId + ")");

		// TODO: OS8.0以降は、サービスをバックグラウンドで動かし続けるために、通知の実装とフォアグラウンドの偽装が必須になる。
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			// TODO: idはアプリ内で一意である必要がある。管理まで手が回らないので重複しない様に固定としている。
			String channelId = "status_log_channel";
			String channelName = "status_log_channel";
			String notificationTitle = "端末情報取得サービス";
			String notificationText = "端末の情報をサーバと連携しています";
			// TODO: idはアプリ内で一意である必要がある。管理まで手が回らないので重複しない様に固定としている。
			int notificationId = 1;

			NotificationChannel channel = new NotificationChannel(channelId, channelName, 3);
			((NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
			Notification notification = new NotificationCompat.Builder(this, channelId)
							                  .setSmallIcon(R.mipmap.ic_launcher)
							                  .setContentTitle(notificationTitle)
							                  .setContentText(notificationText)
							                  .build();
			startForeground(notificationId, notification);
		}

		return Service.START_STICKY;
	}
}
