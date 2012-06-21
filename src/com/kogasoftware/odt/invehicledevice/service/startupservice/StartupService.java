package com.kogasoftware.odt.invehicledevice.service.startupservice;

import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.kogasoftware.odt.invehicledevice.ui.BigToast;
import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;

public class StartupService extends Service {
	private static final String TAG = StartupService.class.getSimpleName();
	private static final long CHECK_DEVICE_INTERVAL_MILLIS = 20 * 1000;
	private final AtomicBoolean enabled = new AtomicBoolean(true);
	private final BroadcastReceiver screenOnBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "screen on");
			showActivity();
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
			Log.i(TAG, "Startup disabled");
			return;
		}
		String externalStorageState = Environment.getExternalStorageState();
		if (!externalStorageState.equals(Environment.MEDIA_MOUNTED)) {
			Log.i(TAG, "Environment.getExternalStorageState() "
					+ externalStorageState + " != " + Environment.MEDIA_MOUNTED);
			BigToast.makeText(this, "SDカードを接続してください",
					Toast.LENGTH_LONG).show();
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
			Log.e(TAG, "!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)");
			BigToast.makeText(this, "GPSの設定を有効にしてください",
					Toast.LENGTH_LONG).show();
			return;
		}

		Intent startIntent = new Intent(StartupService.this,
				InVehicleDeviceActivity.class);
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
		return binder;
	}

	private final IStartupService.Stub binder = new IStartupService.Stub(){
		@Override
		public void disable() throws RemoteException {
			enabled.set(false);
		}

		@Override
		public void enable() throws RemoteException {
			enabled.set(true);
		}
	};
	
	private void showActivity() {
		handler.post(checkDeviceAndShowActivityCallback);
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

		showActivity();
		return Service.START_STICKY;
	}
}
