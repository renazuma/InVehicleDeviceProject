package com.kogasoftware.odt.invehicledevice;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

@ReportsCrashes(formKey = "dGVjZDhsb3JLVXctSlJXWV9vU1FHSWc6MQ")
public class InVehicleDeviceApplication extends Application {
	private static final String TAG = InVehicleDeviceApplication.class
			.getSimpleName();

	@Override
	public void onCreate() {
		ACRA.init(this);
		super.onCreate();
		Log.i(TAG, "onCreate()");
		try {
			PackageInfo packageInfo = getPackageManager().getPackageInfo(
					getPackageName(), 0);
			Log.i(TAG, "versionCode=" + packageInfo.versionCode
					+ " versionName=" + packageInfo.versionName
					+ " BuildConfig.DEBUG=" + BuildConfig.DEBUG);
		} catch (NameNotFoundException e) {
			Log.w(TAG, e);
		}
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		Log.i(TAG, "onTerminate()");
	}
}
