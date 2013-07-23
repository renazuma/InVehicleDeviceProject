package com.kogasoftware.odt.invehicledevice.service.startupservice;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.google.common.base.Objects;
import com.kogasoftware.odt.invehicledevice.BuildConfig;
import com.kogasoftware.odt.invehicledevice.service.logservice.LogService;
import com.kogasoftware.odt.invehicledevice.service.trackingservice.TrackingService;
import com.kogasoftware.odt.invehicledevice.service.voiceservice.VoiceService;
import com.kogasoftware.odt.invehicledevice.ui.BigToast;
import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;

public class StartupService extends Service {
	private static final String TAG = StartupService.class.getSimpleName();
	public static final long CHECK_DEVICE_INTERVAL_MILLIS = 10 * 1000;
	private final Handler handler = new Handler();
	private final AtomicBoolean enabled = new AtomicBoolean(true);
	private final BroadcastReceiver screenOnBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "screen on");
			handler.post(checkDeviceAndShowActivityCallback);
		}
	};
	private final IStartupService.Stub binder = new IStartupService.Stub() {
		@Override
		public void disable() {
			setEnabled(false);
		}

		@Override
		public void enable() {
			setEnabled(true);
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

	public void disableAirplaneMode() {
		if (Build.VERSION.SDK_INT >= 17) {
			return;
		}
		ContentResolver contentResolver = getContentResolver();
		Boolean airplaneModeOn = Settings.System.getInt(contentResolver,
				Settings.System.AIRPLANE_MODE_ON, 0) == 1;
		if (!airplaneModeOn) {
			return;
		}
		Settings.System.putInt(contentResolver,
				Settings.System.AIRPLANE_MODE_ON, 0);
		// Post an intent to reload
		Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
		intent.putExtra("state", true);
		sendBroadcast(intent);
	}

	public void checkDeviceAndStartActivity() {
		// テスト中は起動しない
		if (BuildConfig.DEBUG) {
			ActivityManager activityManager = (ActivityManager) getSystemService(Activity.ACTIVITY_SERVICE);
			for (RunningAppProcessInfo info : Objects.firstNonNull(
					activityManager.getRunningAppProcesses(),
					new ArrayList<RunningAppProcessInfo>())) {
				for (String pkg : info.pkgList) {
					if (pkg.startsWith("com.kogasoftware.odt.invehicledevice.test")) {
						Log.i(TAG, "pkg=" + pkg + " found. start canceled.");
						return;
					}
				}
			}
		}

		// 機内モードは強制的にOFF
		disableAirplaneMode();

		if (!enabled.get()) {
			Log.i(TAG, "waiting for startup enabled");
			return;
		}

		if (!isDeviceReady()) {
			return;
		}

		Intent startIntent = new Intent(StartupService.this,
				InVehicleDeviceActivity.class);
		startIntent.setAction(Intent.ACTION_DEFAULT);
		startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(startIntent);
	}

	public Boolean isDeviceReady() {
		return isDeviceReady(Environment.getExternalStorageState());
	}

	public Boolean isDeviceReady(String externalStorageState) {
		if (!externalStorageState.equals(Environment.MEDIA_MOUNTED)) {
			Log.i(TAG, "Environment.getExternalStorageState() "
					+ externalStorageState + " != " + Environment.MEDIA_MOUNTED);
			BigToast.makeText(this, "SDカードを接続してください", Toast.LENGTH_LONG).show();
			return false;
		}

		PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		if (!powerManager.isScreenOn()) {
			Log.i(TAG, "screen off");
			return false;
		}

		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if (isGpsRequired()
				&& !locationManager
						.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Log.e(TAG,
					"!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)");
			BigToast.makeText(this, "GPSの設定を有効にしてください", Toast.LENGTH_LONG)
					.show();
			return false;
		}

		ActivityManager activityManager = (ActivityManager) getSystemService(Activity.ACTIVITY_SERVICE);
		for (RunningTaskInfo runningTaskInfo : activityManager
				.getRunningTasks(1)) {
			String packageName = getPackageName();
			String className = InVehicleDeviceActivity.class.getName();
			ComponentName topActivity = runningTaskInfo.topActivity;
			if (topActivity.getPackageName().equals(packageName)
					&& topActivity.getClassName().equals(className)) {
				Log.v(TAG, "activity " + InVehicleDeviceActivity.class
						+ " is running");
				return false;
			}
		}

		return true;
	}

	public static Boolean isGpsRequired(Boolean isDebug, String buildModel) {
		if (!isDebug) {
			return true;
		}
		if (buildModel.startsWith("androVM for VirtualBox")) {
			return false;
		} else if (buildModel.startsWith("Buildroid for VirtualBox")) {
			return false;
		}
		return true;
	}

	public static Boolean isGpsRequired() {
		return isGpsRequired(BuildConfig.DEBUG, Build.MODEL);
	}

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
				Log.i(TAG, "package replaced");
			}
			context.startService(new Intent(context, StartupService.class));
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	@Override
	public void onCreate() {
		Log.i(TAG, "onCreate()");
		getApplicationContext().registerReceiver(screenOnBroadcastReceiver,
				new IntentFilter(Intent.ACTION_SCREEN_ON));
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
		if (intent != null) {
			Bundle extras = intent.getExtras();
			if (extras != null
					&& extras.containsKey(Intents.EXTRA_BOOLEAN_ENABLED)) {
				setEnabled(extras.getBoolean(Intents.EXTRA_BOOLEAN_ENABLED));
			}
		}
		handler.post(checkDeviceAndShowActivityCallback);
		startService(new Intent(this, VoiceService.class));
		startService(new Intent(this, LogService.class));
		startService(new Intent(this, TrackingService.class));
		return Service.START_STICKY;
	}

	private void setEnabled(Boolean value) {
		enabled.set(value);
		Log.i(TAG, "Startup enabled=" + value);
		handler.post(checkDeviceAndShowActivityCallback);
	}
}
