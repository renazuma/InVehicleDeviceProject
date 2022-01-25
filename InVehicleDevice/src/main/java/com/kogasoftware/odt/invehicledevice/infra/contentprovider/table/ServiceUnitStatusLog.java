package com.kogasoftware.odt.invehicledevice.infra.contentprovider.table;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.kogasoftware.odt.invehicledevice.infra.contentprovider.InVehicleDeviceContentProvider;

/**
 * ServiceUnitStatusLogテーブル
 */
public class ServiceUnitStatusLog {
    public static final int TABLE_CODE = 7;
    public static final String TABLE_NAME = "service_unit_status_logs";
    public static final Content CONTENT = new Content(TABLE_CODE, TABLE_NAME);

    public static class Columns implements BaseColumns {
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String ORIENTATION = "orientation";
        public static final String TEMPERATURE = "temperature";
        public static final String SIGNAL_STRENGTH = "signal_strength";
        public static final String CREATED_AT = "created_at";
    }

    public static int update(InVehicleDeviceContentProvider contentProvider,
                             ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase database = contentProvider.getDatabase();
        if (selection != null) {
            throw new IllegalArgumentException(
                    "selection argument is not supported");
        }
        int affected;
        try {
            long id;
            database.beginTransaction();
            try (Cursor cursor = database.query(ServiceUnitStatusLog.TABLE_NAME,
                    null, null, null, null, null,
                    Columns.CREATED_AT + " DESC", "1")) {
                if (!cursor.moveToFirst()) {
                    return 0;
                }
                id = cursor.getLong(cursor
                        .getColumnIndex(Columns._ID));
            }
            String where = ServiceUnitStatusLog.Columns._ID + " = ?";
            String[] whereArgs = new String[]{Long.toString(id)};
            affected = database.update(ServiceUnitStatusLog.TABLE_NAME, values,
                    where, whereArgs);
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
        return affected;
    }
}
