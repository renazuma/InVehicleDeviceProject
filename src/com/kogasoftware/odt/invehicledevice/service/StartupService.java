package com.kogasoftware.odt.invehicledevice.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.widget.Toast;

import com.kogasoftware.odt.invehicledevice.ui.BigToast;
import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;

public class StartupService extends Service {
	private static final String TAG = StartupService.class.getSimpleName();
	private static final Integer WAKE_LOCK_PERIOD_MILLIS = 1000;
	private static final long WAIT_FOR_EXTERNAL_STORAGE_MILLIS = 15 * 1000;
	private final BroadcastReceiver screenOnBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "screen on");
			showActivity();
		}
	};

	private final Runnable waitExternalStorageAndShowActivityCallback = new Runnable() {
		@Override
		public void run() {
			handler.removeCallbacks(this);
			String externalStorageState = Environment.getExternalStorageState();
			if (!externalStorageState.equals(Environment.MEDIA_MOUNTED)) {
				handler.postDelayed(this, WAIT_FOR_EXTERNAL_STORAGE_MILLIS);
				Log.i(TAG, "Environment.getExternalStorageState() "
						+ externalStorageState + " != "
						+ Environment.MEDIA_MOUNTED);
				BigToast.makeText(StartupService.this, "SDカードを接続してください",
						Toast.LENGTH_LONG).show();
				return;
			}

			Intent startIntent = new Intent(StartupService.this,
					InVehicleDeviceActivity.class);
			startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			showScreen();
			startActivity(startIntent);
		}
	};

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
			if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)
					&& !intent.getDataString().equals(
							"package:com.kogasoftware.odt.invehicledevice")) {
				return;
			}
			context.startService(new Intent(context, StartupService.class));
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON のみでスクリーンをONしようとすると、
	 * GalaxyNexusのロックスクリーンからの復帰と組み合わせると画面がちらつく。 それを防ぐために、明示的に一定時間WakeLockを取得する。
	 */
	private void showScreen() {
		PowerManager powerManager = ((PowerManager) getSystemService(Context.POWER_SERVICE));
		if (powerManager == null) {
			Log.e(TAG, "PowerManager not found");
			return;
		}
		if (powerManager.isScreenOn()) {
			return;
		}
		final WakeLock wakeLock = powerManager.newWakeLock(
				PowerManager.SCREEN_DIM_WAKE_LOCK
						| PowerManager.ACQUIRE_CAUSES_WAKEUP
						| PowerManager.ON_AFTER_RELEASE,
				StartupService.class.getSimpleName());
		Runnable release = new Runnable() {
			@Override
			public void run() {
				wakeLock.release();
			}
		};
		if (handler.postDelayed(release, WAKE_LOCK_PERIOD_MILLIS)) {
			wakeLock.acquire();
		}
	}

	private void showActivity() {
		handler.post(waitExternalStorageAndShowActivityCallback);
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
