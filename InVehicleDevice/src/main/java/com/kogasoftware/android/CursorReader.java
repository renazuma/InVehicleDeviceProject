package com.kogasoftware.android;

import java.math.BigDecimal;

import org.joda.time.DateTime;

import android.database.Cursor;
import android.util.Log;

/**
 * カーソル読み込み用クラス
 */
public class CursorReader {
	private static final String TAG = CursorReader.class.getSimpleName();
	private final Cursor cursor;

	public CursorReader(Cursor cursor) {
		this.cursor = cursor;
	}

	public Long readLong(String columnName) {
		Integer index = cursor.getColumnIndexOrThrow(columnName);
		if (cursor.isNull(index)) {
			return null;
		}
		return cursor.getLong(index);
	}

	public DateTime readDateTime(String columnName) {
		Integer index = cursor.getColumnIndexOrThrow(columnName);
		if (cursor.isNull(index)) {
			return null;
		}
		return new DateTime(cursor.getLong(index));
	}

	public String readString(String columnName) {
		Integer index = cursor.getColumnIndexOrThrow(columnName);
		if (cursor.isNull(index)) {
			return null;
		}
		return cursor.getString(index);
	}

	public BigDecimal readBigDecimal(String columnName) {
		Integer index = cursor.getColumnIndexOrThrow(columnName);
		if (cursor.isNull(index)) {
			return null;
		}
		return new BigDecimal(cursor.getString(index));
	}

	public Boolean readBoolean(String columnName) {
		Integer index = cursor.getColumnIndexOrThrow(columnName);
		if (cursor.isNull(index)) {
			Log.w(TAG, "Unboxing null to boolean causes NullPointerException!");
			return null;
		}
		return cursor.getInt(index) > 0;
	}
}
