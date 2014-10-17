package com.kogasoftware.odt.invehicledevice.service.startupservice;

import java.io.IOException;

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
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.kogasoftware.odt.invehicledevice.BuildConfig;
import com.kogasoftware.odt.invehicledevice.ui.BigToast;
import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;

/**
 * 自動起動処理
 */
public class StartupService extends Service {
	private static final String TAG = StartupService.class.getSimpleName();
	public static final long CHECK_DEVICE_INTERVAL_MILLIS = 10 * 1000;
	private final Handler handler = new Handler();
	private final BroadcastReceiver screenOnBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "screen on");
			handler.post(startActivityIfReadyCallback);
		}
	};

	private final Runnable startActivityIfReadyCallback = new Runnable() {
		@Override
		public void run() {
			handler.removeCallbacks(this); // 重複を削除
			if (startActivityIfReady()) {
				handler.postDelayed(checkDeviceAndAlertCallback,
						CHECK_DEVICE_INTERVAL_MILLIS);
			} else {
				handler.postDelayed(this, CHECK_DEVICE_INTERVAL_MILLIS);
			}
		}
	};

	private final Runnable checkDeviceAndAlertCallback = new Runnable() {
		@Override
		public void run() {
			checkDeviceAndAlert();
			handler.postDelayed(this, CHECK_DEVICE_INTERVAL_MILLIS);
		}
	};

	public Boolean checkDeviceAndAlert() {
		// 機内モードは強制的にOFF
		try {
			AirplaneModeSetting.set(this, false);
		} catch (IOException e) {
			sendBroadcast(new AirplaneModeOnBroadcastIntent());
			Log.w(TAG, e);
			return false;
		}

		if (!isDeviceReady()) {
			return false;
		}
		return true;
	}

	public Boolean startActivityIfReady() {
		if (!checkDeviceAndAlert()) {
			return false;
		}
		if (BuildConfig.DEBUG) {
			Log.i(TAG, "testing");
			return true;
		}
		Intent startIntent = new Intent(StartupService.this,
				InVehicleDeviceActivity.class);
		startIntent.setAction(Intent.ACTION_DEFAULT);
		startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(startIntent);
		return true;
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
		return null;
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
		handler.removeCallbacks(startActivityIfReadyCallback);
		handler.removeCallbacks(checkDeviceAndAlertCallback);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		Log.i(TAG, "onStartCommand(" + intent + ", " + flags + ", " + startId
				+ ")");
		handler.post(startActivityIfReadyCallback);
		return Service.START_STICKY;
	}
}
