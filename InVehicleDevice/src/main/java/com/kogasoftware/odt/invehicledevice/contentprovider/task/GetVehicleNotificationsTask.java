package com.kogasoftware.odt.invehicledevice.contentprovider.task;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.contentprovider.json.VehicleNotificationJson;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.VehicleNotification;
import com.kogasoftware.odt.invehicledevice.service.staticvoiceplayservice.StaticVoicePlayService;
import com.kogasoftware.odt.invehicledevice.service.staticvoiceplayservice.voice.AdminNotificationVoice;
import com.kogasoftware.odt.invehicledevice.service.staticvoiceplayservice.voice.ChimeVoice;
import com.kogasoftware.odt.invehicledevice.service.staticvoiceplayservice.voice.ScheduleChangeVoice;
import com.kogasoftware.odt.invehicledevice.service.staticvoiceplayservice.voice.Voice;

import org.apache.http.HttpResponse;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 車載器通知の取得APIとの通信
 */
public class GetVehicleNotificationsTask extends SynchronizationTask {
	static final String TAG = GetVehicleNotificationsTask.class.getSimpleName();
	public static final Integer INTERVAL_MILLIS = 20 * 1000;

	public GetVehicleNotificationsTask(Context context,
									   SQLiteDatabase database, ScheduledExecutorService executorService) {
		super(context, database, executorService);
	}

	@Override
	protected void runSession(URI baseUri, String authenticationToken) {
		doHttpGet(baseUri, "vehicle_notifications", authenticationToken,
				new LogCallback(TAG) {
					@Override
					public void onSuccess(HttpResponse response, byte[] entity) {
						save(entity);
					}
				});
	}

	private void save(byte[] entity) {
		VehicleNotificationJson[] vehicleNotifications;
		try {
			vehicleNotifications = JSON.readValue(new String(entity, Charsets.UTF_8), VehicleNotificationJson[].class);
		} catch (IOException e) {
			Log.e(TAG, "ParseError: " + entity, e);
			return;
		}
		List<Uri> committedUris;
		try {
			List<Uri> uris = Lists.newLinkedList();
			List<Long> ids = Lists.newLinkedList();
			database.beginTransaction();
			Cursor cursor = database.query(VehicleNotification.TABLE_NAME,
					new String[]{VehicleNotification.Columns._ID}, null, null,
					null, null, null);
			try {
				if (cursor.moveToFirst()) {
					do {
						ids.add(cursor.getLong(cursor.getColumnIndexOrThrow(VehicleNotification.Columns._ID)));
					} while (cursor.moveToNext());
				}
			} finally {
				cursor.close();
			}
			boolean contactTabChangeFlg = false;
			boolean scheduleChangeFlg = false;
			for (VehicleNotificationJson vehicleNotification : vehicleNotifications) {
				if (ids.contains(vehicleNotification.id)) {
					continue;
				}
				database.insertOrThrow(VehicleNotification.TABLE_NAME, null, vehicleNotification.toContentValues());
				uris.add(ContentUris.withAppendedId(VehicleNotification.CONTENT.URI, vehicleNotification.id));

				switch (String.valueOf(vehicleNotification.notificationKind)) {
					case "0":
						contactTabChangeFlg = true;
						break;
					case "1":
						scheduleChangeFlg = true;
						break;
				}
			}

			playAdminNotificationVoice(contactTabChangeFlg);
			playScheduleNotificationVoice(scheduleChangeFlg);

			database.setTransactionSuccessful();
			committedUris = uris;
		} finally {
			database.endTransaction();
		}
		for (Uri changedUri : committedUris) {
			contentResolver.notifyChange(changedUri, null);
		}
		if (!committedUris.isEmpty()) {
			contentResolver.notifyChange(VehicleNotification.CONTENT.URI, null);
		}
		executorService.execute(new GetOperationSchedulesTask(context, database, executorService, true));
	}

	private void playAdminNotificationVoice(boolean contactTabChangeFlg) {
		if (!contactTabChangeFlg) {
			return;
		}
		Voice chimeVoice = new ChimeVoice();
		Voice adminNotificationVoice = new AdminNotificationVoice();
		StaticVoicePlayService.playVoice(context, chimeVoice);
		StaticVoicePlayService.playVoice(context, adminNotificationVoice);
		StaticVoicePlayService.playVoice(context, chimeVoice);
		StaticVoicePlayService.playVoice(context, adminNotificationVoice);
	}

	private void playScheduleNotificationVoice(boolean scheduleChangeFlg) {
		if (!scheduleChangeFlg) {
			return;
		}
		Voice chimeVoice = new ChimeVoice();
		Voice scheduleChange = new ScheduleChangeVoice();
		StaticVoicePlayService.playVoice(context, chimeVoice);
		StaticVoicePlayService.playVoice(context, scheduleChange);
		StaticVoicePlayService.playVoice(context, chimeVoice);
		StaticVoicePlayService.playVoice(context, scheduleChange);
	}
}
