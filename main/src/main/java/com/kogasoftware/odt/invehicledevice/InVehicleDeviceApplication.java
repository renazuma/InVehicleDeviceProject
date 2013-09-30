package com.kogasoftware.odt.invehicledevice;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.apache.commons.lang3.reflect.MethodUtils;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.io.Closeables;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage;
import com.kogasoftware.odt.invehicledevice.service.logservice.LogServiceReportSender;

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
	private static final AtomicInteger ACRA_INIT_ONCE_WORKAROUND = new AtomicInteger(
			1);

	static {
		try {
			Runtime.getRuntime().addShutdownHook(new VmShutdownHook());
		} catch (IllegalStateException e) {
			Log.e(TAG, "Adding shutdown hook was Failed", e);
		} catch (IllegalArgumentException e) {
			Log.e(TAG, "Adding shutdown hook was Failed", e);
		}
	}

	@Override
	public void onCreate() {
		Log.i(TAG, "onCreate()");
		if (ACRA_INIT_ONCE_WORKAROUND.getAndSet(0) != 0) {
			ACRA.init(this);
			super.onCreate();
			ACRA.getErrorReporter().setReportSender(
					new LogServiceReportSender(this));
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

		// enableStrictMode();
	}

	protected void enableStrictMode() {
		if (!BuildConfig.DEBUG) {
			return;
		}
		
        try {
        	Boolean hard = false;
        	
            Class<?> strictModeClass = Class.forName("android.os.StrictMode");

            Object threadPolicyBuilder = Class.forName(
                    "android.os.StrictMode$ThreadPolicy$Builder").newInstance();
            try {
                threadPolicyBuilder = MethodUtils
                        .invokeMethod(threadPolicyBuilder, "penaltyDialog");
            } catch (NoSuchMethodException e) {
            }
            try {
                threadPolicyBuilder = MethodUtils.invokeMethod(threadPolicyBuilder,
                        "penaltyFlashScreen");
            } catch (NoSuchMethodException e) {
            }
            if (hard) {
                threadPolicyBuilder =
                        MethodUtils.invokeMethod(threadPolicyBuilder, "detectAll");
            } else {
                threadPolicyBuilder =
                        MethodUtils.invokeMethod(threadPolicyBuilder, "detectNetwork");
            }
            threadPolicyBuilder = MethodUtils.invokeMethod(threadPolicyBuilder, "penaltyLog");
            Object threadPolicy = MethodUtils.invokeMethod(threadPolicyBuilder, "build");
            MethodUtils.invokeStaticMethod(strictModeClass, "setThreadPolicy", threadPolicy);

            Object vmPolicyBuilder = Class.forName(
                    "android.os.StrictMode$VmPolicy$Builder").newInstance();
            vmPolicyBuilder = MethodUtils.invokeMethod(vmPolicyBuilder, "detectAll");
            vmPolicyBuilder = MethodUtils.invokeMethod(vmPolicyBuilder, "penaltyLog");
            if (hard) {
                vmPolicyBuilder = MethodUtils.invokeMethod(vmPolicyBuilder,
                        "penaltyDeath");
            }
            Object vmPolicy = MethodUtils.invokeMethod(vmPolicyBuilder, "build");
            MethodUtils.invokeStaticMethod(strictModeClass, "setVmPolicy", vmPolicy);

            Log.d(TAG, "StrictMode enabled");
        } catch (ClassNotFoundException e) {
            // do nothing
        } catch (IllegalArgumentException e) {
            Log.w(TAG, e);
        } catch (IllegalAccessException e) {
            Log.w(TAG, e);
        } catch (InvocationTargetException e) {
            Log.w(TAG, e);
        } catch (SecurityException e) {
            Log.w(TAG, e);
        } catch (NoSuchMethodException e) {
            Log.w(TAG, e);
        } catch (InstantiationException e) {
            Log.w(TAG, e);
        }
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		Log.i(TAG, "onTerminate()");
	}

	/**
	 * LocalStorageクラスで確実に終了処理を実行したいが、exit()が呼び出されている可能性がありfinally節が実行されない
	 * ことがある疑いがあるため、shutdown hookを使う。この処理は、exit()が呼ばれていないことが確認できたら消す
	 */
	public static class VmShutdownHook extends Thread {
		public static final String TAG = VmShutdownHook.class.getSimpleName();
		private static final WeakHashMap<LocalStorage, Boolean> LOCAL_STORAGES = new WeakHashMap<LocalStorage, Boolean>();

		public static void addLocalStorage(LocalStorage localStorage) {
			synchronized (LOCAL_STORAGES) {
				LOCAL_STORAGES.put(localStorage, true);
			}
		}

		@Override
		public void run() {
			Log.i(TAG, "start");
			synchronized (LOCAL_STORAGES) {
				Iterable<LocalStorage> localStorages = Iterables.filter(
						LOCAL_STORAGES.keySet(), Predicates.notNull());
				LOCAL_STORAGES.clear();
				for (LocalStorage localStorage : localStorages) {
					localStorage.close();
				}
				for (LocalStorage localStorage : localStorages) {
					localStorage.joinUninterruptibly(200, TimeUnit.MILLISECONDS);
				}
			}
			Log.i(TAG, "complete");
		}
	}
}
