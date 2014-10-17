package com.kogasoftware.odt.invehicledevice.contentprovider.task;

import org.joda.time.DateTime;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.kogasoftware.odt.invehicledevice.contentprovider.table.ServiceUnitStatusLog;

/**
 * ServiceUnitStatusLogのデータをローカルのDBに事前に用意する
 */
public class InsertServiceUnitStatusLogTask implements Runnable {
	public static final Integer INTERVAL_MILLIS = 30 * 1000;
	private final SQLiteDatabase database;

	public InsertServiceUnitStatusLogTask(SQLiteDatabase database) {
		this.database = database;
	}

	@Override
	public void run() {
		ContentValues values = new ContentValues();
		Cursor cursor = database.query(ServiceUnitStatusLog.TABLE_NAME, null,
				null, null, null, null,
				ServiceUnitStatusLog.Columns.CREATED_AT + " DESC");
		try {
			if (cursor.moveToFirst()) {
				DatabaseUtils.cursorRowToContentValues(cursor, values);
				values.remove(ServiceUnitStatusLog.Columns._ID);
			}
		} finally {
			cursor.close();
		}
		values.put(ServiceUnitStatusLog.Columns.CREATED_AT, DateTime.now()
				.getMillis() + INTERVAL_MILLIS);
		database.insert(ServiceUnitStatusLog.TABLE_NAME, null, values);
	}
}
