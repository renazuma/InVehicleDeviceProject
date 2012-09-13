package com.kogasoftware.odt.invehicledevice;

import java.util.concurrent.atomic.AtomicInteger;

import org.acra.ACRA;
import org.acra.ErrorReporter;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.StrictMode;
import android.util.Log;

import com.google.common.base.Throwables;
import com.kogasoftware.odt.invehicledevice.service.logservice.LogService;
import com.kogasoftware.odt.invehicledevice.service.logservice.LogServiceReportSender;
import com.kogasoftware.odt.invehicledevice.service.startupservice.StartupService;
import com.kogasoftware.odt.invehicledevice.service.voiceservice.VoiceService;

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
		ReportField.USER_EMAIL, }, includeDropBoxSystemTags = true, additionalDropBoxTags = { "data_app_anr" })
public class InVehicleDeviceApplication extends Application {
	private static final String TAG = InVehicleDeviceApplication.class
			.getSimpleName();
	private static final AtomicInteger ACRA_INIT_ONCE_WORKAROUND = new AtomicInteger(1);

	@Override
	public void onCreate() {
		try { // Applicationオブジェクトが生成できない旨の例外が起きることがあり、このonCreateが怪しいためデバッグ用にwtfとして記録する。
			tryOnCreate();
		} catch (Throwable t) {
			Log.wtf(TAG, t);
			Log.wtf(TAG, Throwables.getStackTraceAsString(t));
			throw Throwables.propagate(t);
		}
	}

	public void tryOnCreate() {
		Log.i(TAG, "onCreate()");
		if (ACRA_INIT_ONCE_WORKAROUND.getAndSet(0) != 0) {
			ACRA.init(this);
			super.onCreate();
			ErrorReporter errorReporter = ACRA.getErrorReporter();
			errorReporter.setReportSender(new LogServiceReportSender(this));
		} else {
			super.onCreate();
			String message = "ACRA.init() called more than once";
			Log.e(TAG, message);
			Log.wtf(TAG, message);
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

		enableStrictMode();

		startService(new Intent(this, StartupService.class));
		startService(new Intent(this, VoiceService.class));
		startService(new Intent(this, LogService.class));
	}

	protected void enableStrictMode() {
		if (BuildConfig.DEBUG) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectAll().penaltyLog().penaltyDropBox().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
					.detectAll().penaltyLog().penaltyDropBox().build());
			Log.i(TAG, "StrictMode enabled");
		}
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		Log.i(TAG, "onTerminate()");

		stopService(new Intent(this, StartupService.class));
		stopService(new Intent(this, VoiceService.class));
		stopService(new Intent(this, LogService.class));
	}
}
