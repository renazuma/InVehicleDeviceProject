package com.kogasoftware.odt.invehicledevice.contentprovider.model;

import android.database.Cursor;

import com.kogasoftware.android.CursorReader;
import com.kogasoftware.odt.invehicledevice.contentprovider.InVehicleDeviceContentProvider;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.ServiceProviders;

public class ServiceProvider {
	public Long id;
	public String name;
	public Boolean operationListOnly;
	public String logSecretAccessKeyAws;
	public String logAccessKeyIdAws;

	public ServiceProvider(Cursor cursor) {
		CursorReader reader = new CursorReader(cursor);
		id = reader.readLong(ServiceProviders.Columns._ID);
		name = reader.readString(ServiceProviders.Columns.NAME);
		logAccessKeyIdAws = reader
				.readString(ServiceProviders.Columns.LOG_ACCESS_KEY_ID_AWS);
		logSecretAccessKeyAws = reader
				.readString(ServiceProviders.Columns.LOG_SECRET_ACCESS_KEY_AWS);
		operationListOnly = false;
	}

	public static Cursor query(InVehicleDeviceContentProvider contentProvider,
			String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		Cursor cursor = contentProvider.getDatabase().query(
				ServiceProviders.TABLE_NAME, projection, selection,
				selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(contentProvider.getContext()
				.getContentResolver(), ServiceProviders.CONTENT.URI);
		return cursor;
	}
}
