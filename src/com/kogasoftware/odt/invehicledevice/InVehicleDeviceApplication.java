package com.kogasoftware.odt.invehicledevice;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.StrictMode;
import android.util.Log;

@ReportsCrashes(formKey = "dFp5SnVVbTRuem13WmJ0YlVUb2NjaXc6MQ", mode = ReportingInteractionMode.TOAST, resToastText = R.string.crash_toast_text, customReportContent = {
		ReportField.ANDROID_VERSION, ReportField.APP_VERSION_CODE,
		ReportField.APP_VERSION_NAME, ReportField.AVAILABLE_MEM_SIZE,
		ReportField.BRAND, ReportField.BUILD, ReportField.CRASH_CONFIGURATION,
		ReportField.CUSTOM_DATA, ReportField.DEVICE_ID, ReportField.DISPLAY,
		ReportField.DROPBOX, ReportField.ENVIRONMENT, ReportField.EVENTSLOG,
		ReportField.FILE_PATH, ReportField.INITIAL_CONFIGURATION,
		ReportField.INSTALLATION_ID, ReportField.IS_SILENT, ReportField.LOGCAT,
		ReportField.PACKAGE_NAME, ReportField.PHONE_MODEL, ReportField.PRODUCT,
		ReportField.RADIOLOG, ReportField.REPORT_ID,
		ReportField.SETTINGS_SECURE, ReportField.SETTINGS_SYSTEM,
		ReportField.SHARED_PREFERENCES, ReportField.STACK_TRACE,
		ReportField.TOTAL_MEM_SIZE, ReportField.USER_APP_START_DATE,
		ReportField.USER_COMMENT, ReportField.USER_CRASH_DATE,
		ReportField.USER_EMAIL, }, logcatArguments = { "-t", "1000", "-v",
		"time" }, includeDropBoxSystemTags = true, additionalDropBoxTags = { "data_app_anr" })
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
		// enableStrictMode();
	}

	protected void enableStrictMode() {
		if (BuildConfig.DEBUG) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectAll().penaltyLog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
					.detectAll().penaltyLog().penaltyDeath().build());
			Log.i(TAG, "StrictMode enabled");
		}
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		Log.i(TAG, "onTerminate()");
	}
}
