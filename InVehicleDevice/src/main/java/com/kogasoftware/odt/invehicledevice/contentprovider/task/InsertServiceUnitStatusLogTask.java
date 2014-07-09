package com.kogasoftware.odt.invehicledevice.contentprovider.task;

import org.joda.time.DateTime;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.kogasoftware.odt.invehicledevice.contentprovider.table.ServiceUnitStatusLogs;

public class InsertServiceUnitStatusLogTask implements Runnable {
	public static final Integer INTERVAL_MILLIS = 30 * 1000;
	private final SQLiteDatabase database;

	public InsertServiceUnitStatusLogTask(SQLiteDatabase database) {
		this.database = database;
	}

	@Override
	public void run() {
		ContentValues values = new ContentValues();
		Cursor cursor = database.query(ServiceUnitStatusLogs.TABLE_NAME, null,
				null, null, null, null,
				ServiceUnitStatusLogs.Columns.CREATED_AT + " DESC");
		try {
			if (cursor.moveToFirst()) {
				DatabaseUtils.cursorRowToContentValues(cursor, values);
				values.remove(ServiceUnitStatusLogs.Columns._ID);
			}
		} finally {
			cursor.close();
		}
		values.put(ServiceUnitStatusLogs.Columns.CREATED_AT, DateTime.now()
				.getMillis());
		database.insert(ServiceUnitStatusLogs.TABLE_NAME, null, values);
	}
}
