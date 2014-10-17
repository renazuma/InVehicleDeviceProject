package com.kogasoftware.android;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.reflect.MethodUtils;

import android.util.Log;

/**
 * StrictMode関連処理
 */
public class StrictModes {
	private static final String TAG = StrictModes.class.getSimpleName();

	public static void enable() {
		try {
			Class<?> strictModeClass = Class.forName("android.os.StrictMode");
			Object threadPolicyBuilder = Class.forName(
					"android.os.StrictMode$ThreadPolicy$Builder").newInstance();
			// try {
			// threadPolicyBuilder = MethodUtils.invokeMethod(
			// threadPolicyBuilder, "penaltyDialog");
			// } catch (NoSuchMethodException e) {
			// }
			// try {
			// threadPolicyBuilder = MethodUtils.invokeMethod(
			// threadPolicyBuilder, "penaltyFlashScreen");
			// } catch (NoSuchMethodException e) {
			// }
			threadPolicyBuilder = MethodUtils.invokeMethod(threadPolicyBuilder,
					"detectAll");
			threadPolicyBuilder = MethodUtils.invokeMethod(threadPolicyBuilder,
					"penaltyLog");
			Object threadPolicy = MethodUtils.invokeMethod(threadPolicyBuilder,
					"build");
			MethodUtils.invokeStaticMethod(strictModeClass, "setThreadPolicy",
					threadPolicy);
			Object vmPolicyBuilder = Class.forName(
					"android.os.StrictMode$VmPolicy$Builder").newInstance();
			vmPolicyBuilder = MethodUtils.invokeMethod(vmPolicyBuilder,
					"detectAll");
			vmPolicyBuilder = MethodUtils.invokeMethod(vmPolicyBuilder,
					"penaltyLog");
			// vmPolicyBuilder = MethodUtils.invokeMethod(vmPolicyBuilder,
			// "penaltyDeath");
			Object vmPolicy = MethodUtils
					.invokeMethod(vmPolicyBuilder, "build");
			MethodUtils.invokeStaticMethod(strictModeClass, "setVmPolicy",
					vmPolicy);
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
}
