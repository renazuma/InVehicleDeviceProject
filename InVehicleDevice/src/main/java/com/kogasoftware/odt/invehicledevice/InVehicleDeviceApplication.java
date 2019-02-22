package com.kogasoftware.odt.invehicledevice;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.ActivityManager;
import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.kogasoftware.android.StrictModes;
import com.kogasoftware.odt.invehicledevice.presenter.service.logservice.LogServiceReportSender;

/**
 * 車載器アプリケーション
 */
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
		ReportField.USER_EMAIL,}, includeDropBoxSystemTags = true, additionalDropBoxTags = {"data_app_anr"})
public class InVehicleDeviceApplication extends Application {
	private static final String TAG = InVehicleDeviceApplication.class
			.getSimpleName();

	@Override
	public void onCreate() {
		if (ActivityManager.isRunningInTestHarness()) {
			super.onCreate();
		} else {
			ACRA.init(this);
			super.onCreate();
			ACRA.getErrorReporter().setReportSender(
					new LogServiceReportSender(this));
		}

		try {
			PackageInfo packageInfo = getPackageManager().getPackageInfo(
					getPackageName(), 0);
			Log.i(TAG, "versionCode=" + packageInfo.versionCode
					+ " versionName=" + packageInfo.versionName
					+ " BuildConfig.DEBUG=" + BuildConfig.DEBUG);
		} catch (NameNotFoundException e) {
			Log.w(TAG, e);
		}

		if (BuildConfig.DEBUG) {
			StrictModes.enable();
		}
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}
}
