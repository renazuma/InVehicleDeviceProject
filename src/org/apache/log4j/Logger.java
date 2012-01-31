package org.apache.log4j;

import java.util.regex.Pattern;

import android.util.Log;

/**
 * Log4jのインターフェースを持つ、AndroidのLog用facade TODO リフレクションか関数オブジェクト使って書き直し
 * 
 * @author ksc
 * 
 */
public class Logger {
	static final String ANDROID_LOG_TAG = "Viridian";
	static final Boolean DEBUG = false;

	public static Logger getLogger(Class<?> c) {
		return new Logger(c);
	}

	private final String prefix;

	private Logger(Class<?> c) {
		String className = c.getName();
		String truncated = Pattern.compile("^.*\\.").matcher(className)
				.replaceFirst("");
		prefix = truncated + ": ";
	}

	public void debug(Object object) {
		if (DEBUG) {
			Log.d(ANDROID_LOG_TAG, format(object));
		}
	}

	public void error(Object object) {
		Log.e(ANDROID_LOG_TAG, format(object));
	}

	public void fatal(Object object) {
		Log.e(ANDROID_LOG_TAG, format(object));
	}

	private String format(Object object) {
		return prefix + object.toString();
	}

	public void info(Object object) {
		Log.i(ANDROID_LOG_TAG, format(object));
	}

	public void trace(Object object) {
		if (DEBUG) {
			Log.v(ANDROID_LOG_TAG, format(object));
		}
	}

	public void warn(Object object) {
		Log.w(ANDROID_LOG_TAG, format(object));
	}
}
