package com.kogasoftware.odt.invehicledevice.contentprovider.model;

import java.util.concurrent.ScheduledExecutorService;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.kogasoftware.odt.invehicledevice.contentprovider.InVehicleDeviceContentProvider;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.InVehicleDevices;
import com.kogasoftware.odt.invehicledevice.contentprovider.task.GetOperationSchedulesTask;
import com.kogasoftware.odt.invehicledevice.contentprovider.task.GetServiceProviderTask;
import com.kogasoftware.odt.invehicledevice.contentprovider.task.SignInTask;

public class InVehicleDevice {
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
			database.delete(InVehicleDevices.TABLE_NAME, null, null);
			id = database.replaceOrThrow(InVehicleDevices.TABLE_NAME, null,
					values);
			uri = ContentUris.withAppendedId(InVehicleDevices.CONTENT.URI, id);
			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
		}
		contentResolver.notifyChange(InVehicleDevices.CONTENT.URI, null);
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
				InVehicleDevices.TABLE_NAME, projection, selection,
				selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(contentProvider.getContext()
				.getContentResolver(), InVehicleDevices.CONTENT.URI);
		return cursor;
	}

	public static int delete(InVehicleDeviceContentProvider contentProvider,
			String selection, String[] selectionArgs) {
		return contentProvider.getDatabase().delete(
				InVehicleDevices.TABLE_NAME, selection, selectionArgs);
	}
}
