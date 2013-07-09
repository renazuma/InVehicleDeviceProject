package com.kogasoftware.odt.invehicledevice.service.trackingservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * 位置情報のトラッキング処理を行うサービス。「アンドロイド品質ガイドライン」の「FN-S1」に注意する必要がある。
 */
public class TrackingService extends Service {
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
