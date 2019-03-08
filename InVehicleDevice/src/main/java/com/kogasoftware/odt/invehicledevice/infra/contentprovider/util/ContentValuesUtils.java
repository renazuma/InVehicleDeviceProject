package com.kogasoftware.odt.invehicledevice.infra.contentprovider.util;

import android.content.ContentValues;

import org.joda.time.DateTime;

/**
 * ContentValues用の共通処理
 */
public class ContentValuesUtils {
	public static void putDateTime(ContentValues values, String key,
			DateTime dateTime) {
		if (dateTime == null) {
			values.putNull(key);
		} else {
			values.put(key, dateTime.getMillis());
		}
	}
}
