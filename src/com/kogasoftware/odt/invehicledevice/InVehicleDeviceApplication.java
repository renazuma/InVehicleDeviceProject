package com.kogasoftware.odt.invehicledevice;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

public class InVehicleDeviceApplication extends Application {
	private static final String TAG = InVehicleDeviceApplication.class
			.getSimpleName();

	@Override
	public void onCreate() {
		Log.i(TAG, "onCreate()");
		try {
			PackageInfo packageInfo = getPackageManager().getPackageInfo(
					getPackageName(), 0);
			Log.i(TAG, "versionCode=" + packageInfo.versionCode
					+ " versionName=" + packageInfo.versionName);
		} catch (NameNotFoundException e) {
			Log.w(TAG, e);
		}
	}

	@Override
	public void onTerminate() {
		Log.i(TAG, "onDestroy()");
	}
}
