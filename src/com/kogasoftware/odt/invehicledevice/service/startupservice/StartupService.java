package com.kogasoftware.odt.invehicledevice.service.startupservice;

import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.kogasoftware.odt.invehicledevice.ui.BigToast;
import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;

public class StartupService extends Service {
	private static final String TAG = StartupService.class.getSimpleName();
	public static final long CHECK_DEVICE_INTERVAL_MILLIS = 10 * 1000;
	private final AtomicBoolean enabled = new AtomicBoolean(true);
	private final BroadcastReceiver screenOnBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "screen on");
			enabled.set(true);
		}
	};

	private final Runnable checkDeviceAndShowActivityCallback = new Runnable() {
		@Override
		public void run() {
			checkDeviceAndStartActivity();
			handler.removeCallbacks(this); // 重複を削除
			handler.postDelayed(this, CHECK_DEVICE_INTERVAL_MILLIS);
		}
	};

	private void checkDeviceAndStartActivity() {
		if (!enabled.get()) {
			Log.i(TAG, "waiting for startup enabled");
			return;
		}

		if (Settings.System.getInt(getContentResolver(),
				Settings.System.AIRPLANE_MODE_ON, 0) != 0) {
			BigToast.makeText(this, "機内モードが有効になっています。機内モードを無効にしてください。",
					Toast.LENGTH_LONG).show();
			return;
		}

		String externalStorageState = Environment.getExternalStorageState();
		if (!externalStorageState.equals(Environment.MEDIA_MOUNTED)) {
			Log.i(TAG, "Environment.getExternalStorageState() "
					+ externalStorageState + " != " + Environment.MEDIA_MOUNTED);
			BigToast.makeText(this, "SDカードを接続してください", Toast.LENGTH_LONG).show();
			return;
		}

		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if (locationManager == null) {
			Log.e(TAG, "getSystemService(Context.LOCATION_SERVICE) == null");
			BigToast.makeText(this, "LOCATION_SERVICEに接続できません",
					Toast.LENGTH_LONG).show();
			return;
		}

		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Log.e(TAG,
					"!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)");
			BigToast.makeText(this, "GPSの設定を有効にしてください", Toast.LENGTH_LONG)
					.show();
			return;
		}

		ActivityManager activityManager = (ActivityManager) getSystemService(Activity.ACTIVITY_SERVICE);
		for (RunningTaskInfo runningTaskInfo : activityManager
				.getRunningTasks(1)) {
			ComponentName topActivity = runningTaskInfo.topActivity;
			if (topActivity.getPackageName().equals(getPackageName())
					&& topActivity.getClassName().equals(
							InVehicleDeviceActivity.class.getName())) {
				Log.v(TAG, "activity " + InVehicleDeviceActivity.class
						+ " is running");
				return;
			}
		}

		Intent startIntent = new Intent(StartupService.this,
				InVehicleDeviceActivity.class);
		startIntent.setAction(Intent.ACTION_DEFAULT);
		startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(startIntent);
	}

	private final Handler handler = new Handler();

	public static class StartupBroadcastReceiver extends BroadcastReceiver {
		private static final String TAG = StartupBroadcastReceiver.class
				.getSimpleName();

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "broadcast received " + intent);
			if (intent == null) {
				Log.w(TAG, "intent is null");
				return;
			}
			if (intent.getAction() == null) {
				Log.w(TAG, "intent.getAction() is null");
				return;
			}
			if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
				String dataString = "package:" + context.getPackageName();
				if (!intent.getDataString().equals(dataString)) {
					Log.v(TAG, "!intent.getDataString().equals(\"" + dataString
							+ "\")");
					return;
				}
				PowerManager powerManager = (PowerManager) context
						.getSystemService(Context.POWER_SERVICE);
				if (powerManager == null) {
					Log.w(TAG,
							"getSystemService(Context.POWER_SERVICE) == null");
					return;
				}
				if (!powerManager.isScreenOn()) {
					Log.i(TAG, "package replaced & screen off");
					return;
				}
				Log.i(TAG, "package replaced");
			}
			context.startService(new Intent(context, StartupService.class));
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	private final IStartupService.Stub binder = new IStartupService.Stub() {
		@Override
		public void disable() throws RemoteException {
			enabled.set(false);
			Log.i(TAG, "Startup disabled");
		}

		@Override
		public void enable() throws RemoteException {
			enabled.set(true);
			Log.i(TAG, "Startup enabled");
		}
	};

	@Override
	public void onCreate() {
		Log.i(TAG, "onCreate()");
		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
		getApplicationContext().registerReceiver(screenOnBroadcastReceiver,
				intentFilter);
		handler.post(checkDeviceAndShowActivityCallback);
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy()");
		getApplicationContext().unregisterReceiver(screenOnBroadcastReceiver);
		handler.removeCallbacks(checkDeviceAndShowActivityCallback);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		Log.i(TAG, "onStartCommand(" + intent + ", " + flags + ", " + startId
				+ ")");
		enabled.set(true);
		return Service.START_STICKY;
	}
}
