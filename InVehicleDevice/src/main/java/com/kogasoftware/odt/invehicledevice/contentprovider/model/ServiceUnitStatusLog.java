package com.kogasoftware.odt.invehicledevice.contentprovider.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.kogasoftware.odt.invehicledevice.contentprovider.InVehicleDeviceContentProvider;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.ServiceUnitStatusLogs;

public class ServiceUnitStatusLog {
	public static int update(InVehicleDeviceContentProvider contentProvider,
			ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase database = contentProvider.getDatabase();
		if (selection != null) {
			throw new IllegalArgumentException(
					"selection argument is not supported");
		}
		int affected;
		try {
			Long id;
			database.beginTransaction();
			Cursor cursor = database.query(ServiceUnitStatusLogs.TABLE_NAME,
					null, null, null, null, null,
					ServiceUnitStatusLogs.Columns.CREATED_AT + " DESC", "1");
			try {
				if (!cursor.moveToFirst()) {
					return 0;
				}
				id = cursor.getLong(cursor
						.getColumnIndex(ServiceUnitStatusLogs.Columns._ID));
			} finally {
				cursor.close();
			}
			String where = ServiceUnitStatusLogs.Columns._ID + " = ?";
			String[] whereArgs = new String[]{id.toString()};
			affected = database.update(ServiceUnitStatusLogs.TABLE_NAME,
					values, where, whereArgs);
			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
		}
		return affected;
	}
}
