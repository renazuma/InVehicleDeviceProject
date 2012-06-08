package com.kogasoftware.odt.invehicledevice.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;

public class LauncherService extends Service {
	private static final String TAG = LauncherService.class.getSimpleName();
	private static final Integer WAKE_LOCK_PERIOD_MILLIS = 1000;

	private BroadcastReceiver screenOnBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "screen on");
			showActivity();
		}
	};

	public static class StartBroadcastReceiver extends BroadcastReceiver {
		private static final String TAG = StartBroadcastReceiver.class
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
			if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)
					&& !intent.getDataString().equals(
							"package:com.kogasoftware.odt.invehicledevice")) {
				return;
			}
			context.startService(new Intent(context, LauncherService.class));
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private void showActivity() {
		Intent startIntent = new Intent(LauncherService.this,
				InVehicleDeviceActivity.class);
		startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		PowerManager powerManager = ((PowerManager) getSystemService(Context.POWER_SERVICE));
		if (powerManager == null) {
			Log.e(TAG, "PowerManager not found");
			return;
		}
		final WakeLock wakeLock = powerManager.newWakeLock(
				PowerManager.SCREEN_DIM_WAKE_LOCK
						| PowerManager.ACQUIRE_CAUSES_WAKEUP
						| PowerManager.ON_AFTER_RELEASE,
				LauncherService.class.getSimpleName());
		wakeLock.acquire();
		Runnable release = new Runnable() {
			@Override
			public void run() {
				wakeLock.release();
			}
		};
		if (!(new Handler()).postDelayed(release, WAKE_LOCK_PERIOD_MILLIS)) {
			wakeLock.release();
		}
		startActivity(startIntent);
	}

	@Override
	public void onCreate() {
		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
		getApplicationContext().registerReceiver(screenOnBroadcastReceiver,
				intentFilter);
	}

	@Override
	public void onDestroy() {
		getApplicationContext().unregisterReceiver(screenOnBroadcastReceiver);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		Log.i(TAG, "onStartCommand(" + intent + ", " + flags + ", " + startId
				+ ")");

		// onStartCommandはすぐに処理を返さなければいけないため、Activityの起動はHandler経由にする
		(new Handler()).post(new Runnable() {
			@Override
			public void run() {
				showActivity();
			}
		});
		return Service.START_STICKY;
	}
}
