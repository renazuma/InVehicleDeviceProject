package com.kogasoftware.odt.invehicledevice.infra.contentprovider.table;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Pair;

import com.kogasoftware.odt.invehicledevice.infra.contentprovider.InVehicleDeviceContentProvider;

import org.apache.commons.lang3.ObjectUtils;

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
                             ContentValues values, String selection) {
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

    public static Cursor query(InVehicleDeviceContentProvider contentProvider, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = contentProvider.getDatabase().query(ServiceUnitStatusLog.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(contentProvider.getContext().getContentResolver(), ServiceUnitStatusLog.CONTENT.URI);
        return cursor;
    }

    public static Pair<String, String> getLatestLocation(ContentResolver contentResolver) {
        String latitude = "";
        String longitude = "";

        Cursor c = contentResolver.query(ServiceUnitStatusLog.CONTENT.URI, null, null, null, null);
        Integer latIndex = c.getColumnIndex(ServiceUnitStatusLog.Columns.LATITUDE);
        Integer longIndex = c.getColumnIndex(ServiceUnitStatusLog.Columns.LONGITUDE);
        if (latIndex != null && longIndex != null && c.moveToLast()) {
            latitude = c.getString(latIndex);
            longitude = c.getString(longIndex);
        }
        c.close();
        return new Pair(latitude, longitude);
    }
}
