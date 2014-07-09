package com.kogasoftware.odt.invehicledevice.utils;

import org.joda.time.DateTime;

import android.content.ContentValues;

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
