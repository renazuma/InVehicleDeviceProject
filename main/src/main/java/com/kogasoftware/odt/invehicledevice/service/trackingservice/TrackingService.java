package com.kogasoftware.odt.invehicledevice.service.trackingservice;

import com.google.common.io.Closeables;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

/**
 * 位置情報のトラッキング処理を行うサービス「アンドロイド品質ガイドライン」の「FN-S1」に注意する必要がある
 */
public class TrackingService extends Service implements Runnable {
	private static final String TAG = TrackingService.class.getSimpleName();
	private final Handler handler = new Handler();
	private TrackingNotifier trackingNotifier;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "onCreate()");
		Location location = new Location("");
		trackingNotifier = new TrackingNotifier(this, location);
		handler.post(this);
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy()");
		super.onDestroy();
		handler.removeCallbacks(this);
		Closeables.closeQuietly(trackingNotifier);
	}

	@Override
	public void run() {
		trackingNotifier.run();
		handler.postDelayed(this, 500);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		Log.i(TAG, "onStartCommand(" + intent + ", " + flags + ", " + startId
				+ ")");
		return Service.START_STICKY;
	}
}
