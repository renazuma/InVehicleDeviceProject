package com.kogasoftware.odt.invehicledevice.contentprovider.table;

import android.database.Cursor;
import android.provider.BaseColumns;

import com.kogasoftware.android.CursorReader;
import com.kogasoftware.odt.invehicledevice.contentprovider.InVehicleDeviceContentProvider;

public class ServiceProvider {
	public static final int TABLE_CODE = 10;
	public static final String TABLE_NAME = "service_providers";
	public static final Content CONTENT = new Content(TABLE_CODE, TABLE_NAME);

	public static class Columns implements BaseColumns {
		public static final String NAME = "name";
		public static final String LOG_ACCESS_KEY_ID_AWS = "log_access_key_id_aws";
		public static final String LOG_SECRET_ACCESS_KEY_AWS = "log_secret_access_key_aws";
	}

	public Long id;
	public String name;
	public Boolean operationListOnly;
	public String logSecretAccessKeyAws;
	public String logAccessKeyIdAws;

	public ServiceProvider(Cursor cursor) {
		CursorReader reader = new CursorReader(cursor);
		id = reader.readLong(ServiceProvider.Columns._ID);
		name = reader.readString(ServiceProvider.Columns.NAME);
		logAccessKeyIdAws = reader
				.readString(ServiceProvider.Columns.LOG_ACCESS_KEY_ID_AWS);
		logSecretAccessKeyAws = reader
				.readString(ServiceProvider.Columns.LOG_SECRET_ACCESS_KEY_AWS);
		operationListOnly = false;
	}

	public static Cursor query(InVehicleDeviceContentProvider contentProvider,
			String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		Cursor cursor = contentProvider.getDatabase().query(
				ServiceProvider.TABLE_NAME, projection, selection,
				selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(contentProvider.getContext()
				.getContentResolver(), ServiceProvider.CONTENT.URI);
		return cursor;
	}
}
