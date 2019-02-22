package com.kogasoftware.odt.invehicledevice.model.contentprovider.table;

import java.util.concurrent.ScheduledExecutorService;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

import com.kogasoftware.odt.invehicledevice.model.contentprovider.InVehicleDeviceContentProvider;
import com.kogasoftware.odt.invehicledevice.model.contentprovider.task.GetOperationSchedulesTask;
import com.kogasoftware.odt.invehicledevice.model.contentprovider.task.GetServiceProviderTask;
import com.kogasoftware.odt.invehicledevice.model.contentprovider.task.SignInTask;

/**
 * 車載器テーブル
 */
public class InVehicleDevice {
	public static final int TABLE_CODE = 1;
	public static final String TABLE_NAME = "in_vehicle_devices";
	public static final Content CONTENT = new Content(TABLE_CODE, TABLE_NAME);

	public static class Columns implements BaseColumns {
		public static final String LOGIN = "login";
		public static final String PASSWORD = "password";
		public static final String URL = "url";
		public static final String AUTHENTICATION_TOKEN = "authentication_token";
	}

	public static Uri replaceLoginAndPassword(ContentValues values,
			InVehicleDeviceContentProvider contentProvider,
			Runnable onCompleteListener) {
		SQLiteDatabase database = contentProvider.getDatabase();
		ContentResolver contentResolver = contentProvider.getContext()
				.getContentResolver();
		ScheduledExecutorService executorService = contentProvider
				.getExecutorService();
		Uri uri;
		Long id;
		try {
			database.beginTransaction();
			database.delete(InVehicleDevice.TABLE_NAME, null, null);
			id = database.replaceOrThrow(InVehicleDevice.TABLE_NAME, null,
					values);
			uri = ContentUris.withAppendedId(InVehicleDevice.CONTENT.URI, id);
			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
		}
		contentResolver.notifyChange(InVehicleDevice.CONTENT.URI, null);
		SignInTask signInTask = new SignInTask(contentProvider.getContext(),
				database, executorService);
		signInTask.addOnCompleteListener(onCompleteListener);
		executorService.execute(signInTask);
		executorService.execute(new GetServiceProviderTask(contentProvider
				.getContext(), database, executorService));
		executorService.execute(new GetOperationSchedulesTask(contentProvider
				.getContext(), database, executorService));
		return uri;
	}

	public static Cursor query(InVehicleDeviceContentProvider contentProvider,
			String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		Cursor cursor = contentProvider.getDatabase().query(
				InVehicleDevice.TABLE_NAME, projection, selection,
				selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(contentProvider.getContext()
				.getContentResolver(), InVehicleDevice.CONTENT.URI);
		return cursor;
	}

	public static int delete(InVehicleDeviceContentProvider contentProvider,
			String selection, String[] selectionArgs) {
		return contentProvider.getDatabase().delete(InVehicleDevice.TABLE_NAME,
				selection, selectionArgs);
	}
}
