package com.kogasoftware.odt.invehicledevice.infra.contentprovider.table;

import android.database.Cursor;
import android.provider.BaseColumns;

import com.kogasoftware.android.CursorReader;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.InVehicleDeviceContentProvider;

/**
 * 基本料金テーブル
 */
public class DefaultCharge {
    public static final int TABLE_CODE = 11;
    public static final String TABLE_NAME = "default_charges";
    public static final Content CONTENT = new Content(TABLE_CODE, TABLE_NAME);

    public static class Columns implements BaseColumns {
        public static final String VALUE = "value";
    }

    public final Long id;
    public final Long value;

    public DefaultCharge(Cursor cursor) {
        CursorReader reader = new CursorReader(cursor);
        id = reader.readLong(DefaultCharge.Columns._ID);
        value = reader.readLong(DefaultCharge.Columns.VALUE);
    }

    public static Cursor query(InVehicleDeviceContentProvider contentProvider, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = contentProvider.getDatabase().query(DefaultCharge.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(contentProvider.getContext().getContentResolver(), DefaultCharge.CONTENT.URI);
        return cursor;
    }
}
