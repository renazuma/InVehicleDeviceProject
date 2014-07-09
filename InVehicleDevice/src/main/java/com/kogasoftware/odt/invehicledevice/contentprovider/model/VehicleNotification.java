package com.kogasoftware.odt.invehicledevice.contentprovider.model;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ScheduledExecutorService;

import org.joda.time.DateTime;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.google.common.collect.Lists;
import com.kogasoftware.android.CursorReader;
import com.kogasoftware.odt.invehicledevice.contentprovider.InVehicleDeviceContentProvider;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.InVehicleDevices;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.VehicleNotifications;
import com.kogasoftware.odt.invehicledevice.contentprovider.task.PatchVehicleNotificationTask;
import com.kogasoftware.odt.invehicledevice.utils.ContentValuesUtils;

public class VehicleNotification implements Serializable {
	private static final long serialVersionUID = -295331215588438885L;
	public static final String WHERE_SCHEDULE_VEHICLE_NOTIFICATION_FRAGMENT_CONTENT = String
			.format(Locale.US, "%s = %d AND %s IS NULL AND %s > 0",
					VehicleNotifications.Columns.NOTIFICATION_KIND,
					NotificationKind.SCHEDULE,
					VehicleNotifications.Columns.RESPONSE,
					VehicleNotifications.Columns.SCHEDULE_DOWNLOADED);

	public static class Response {
		public static final Long YES = 0L;
		public static final Long NO = 1L;
	}

	public static class NotificationKind {
		public static final Long NORMAL = 0L;
		public static final Long SCHEDULE = 1L;
	}

	public Long id;
	public String bodyRuby;
	public String body;
	public Long response;
	public DateTime readAt;
	public Long notificationKind;
	public Long scheduleDownloaded;

	public VehicleNotification(Cursor cursor) {
		CursorReader reader = new CursorReader(cursor);
		id = reader.readLong(VehicleNotifications.Columns._ID);
		body = reader.readString(VehicleNotifications.Columns.BODY);
		bodyRuby = reader.readString(VehicleNotifications.Columns.BODY_RUBY);
		notificationKind = reader
				.readLong(VehicleNotifications.Columns.NOTIFICATION_KIND);
		response = reader.readLong(VehicleNotifications.Columns.RESPONSE);
		readAt = reader.readDateTime(VehicleNotifications.Columns.READ_AT);
		scheduleDownloaded = reader
				.readLong(VehicleNotifications.Columns.SCHEDULE_DOWNLOADED);
	}

	public static List<VehicleNotification> getAll(Cursor cursor) {
		List<VehicleNotification> results = Lists.newLinkedList();
		if (cursor.getCount() == 0) {
			return results;
		}
		Integer position = cursor.getPosition();
		cursor.moveToFirst();
		do {
			results.add(new VehicleNotification(cursor));
		} while (cursor.moveToNext());
		cursor.moveToPosition(position);
		return results;
	}

	public ContentValues toContentValues() {
		ContentValues values = new ContentValues();
		values.put(VehicleNotifications.Columns._ID, id);
		values.put(VehicleNotifications.Columns.BODY, body);
		values.put(VehicleNotifications.Columns.BODY_RUBY, bodyRuby);
		values.put(VehicleNotifications.Columns.NOTIFICATION_KIND,
				notificationKind);
		values.put(VehicleNotifications.Columns.RESPONSE, response);
		values.put(VehicleNotifications.Columns.SCHEDULE_DOWNLOADED,
				scheduleDownloaded);
		ContentValuesUtils.putDateTime(values,
				VehicleNotifications.Columns.READ_AT, readAt);
		return values;
	}

	public static Uri replace(ContentValues values,
			InVehicleDeviceContentProvider contentProvider) {
		SQLiteDatabase database = contentProvider.getDatabase();
		ContentResolver contentResolver = contentProvider.getContext()
				.getContentResolver();
		ScheduledExecutorService executorService = contentProvider
				.getExecutorService();
		Long id = database.replaceOrThrow(VehicleNotifications.TABLE_NAME,
				null, values);
		Uri uri = ContentUris.withAppendedId(InVehicleDevices.CONTENT.URI, id);
		executorService.execute(new PatchVehicleNotificationTask(
				contentProvider.getContext(), database, executorService));
		contentResolver.notifyChange(VehicleNotifications.CONTENT.URI, null);
		contentResolver.notifyChange(uri, null);
		return uri;
	}

	public static Cursor query(InVehicleDeviceContentProvider contentProvider,
			String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		Cursor cursor = contentProvider.getDatabase().query(
				VehicleNotifications.TABLE_NAME, projection, selection,
				selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(contentProvider.getContext()
				.getContentResolver(), VehicleNotifications.CONTENT.URI);
		return cursor;
	}
}
