package com.kogasoftware.odt.invehicledevice.infra.contentprovider.task;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.json.VehicleNotificationJson;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.VehicleNotification;
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

	public GetVehicleNotificationsTask(Context context, SQLiteDatabase database, ScheduledExecutorService executorService) {
		super(context, database, executorService);
	}

	@Override
	protected void runSession(URI baseUri, String authenticationToken) {
		doHttpGet(baseUri, "vehicle_notifications", authenticationToken, logCallback);
	}

	LogCallback logCallback = new LogCallback(TAG) {
		@Override
		public void onSuccess(HttpResponse response, byte[] entity) {
			VehicleNotificationJson[] vehicleNotifications;
			// TODO: レスポンスのjson化と例外を拾うだけのチェックなので、親クラスの共通処理にした方が良いかも
			try {
				vehicleNotifications = JSON.readValue(new String(entity, Charsets.UTF_8), VehicleNotificationJson[].class);
			} catch (IOException e) {
				Log.e(TAG, "ParseError: " + entity, e);
				return;
			}
			save(vehicleNotifications);
		}
	};


	private void save(VehicleNotificationJson[] pulledJson) {
		boolean existAdminNotification = false;
		boolean existScheduleNotification = false;
		boolean existExpectedChargeChangedNotification = false;
		boolean existMemoChangedNotification = false;

		try {
			database.beginTransaction();
			for (VehicleNotificationJson json : selectTargetJson(pulledJson)) {
				database.insertOrThrow(VehicleNotification.TABLE_NAME, null, json.toContentValues());

				if (json.isAdminNotification()) {
					existAdminNotification = true;
				} else if (json.isScheduleNotification()) {
					existScheduleNotification = true;
				} else if (json.isExpectedChargeChangedNotification()){
				    existExpectedChargeChangedNotification = true;
				} else if (json.isMemoChangedNotification()){
					existMemoChangedNotification = true;
				}
			}
			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
		}

		if (existAdminNotification) {
			playAdminNotificationVoice();
			contentResolver.notifyChange(VehicleNotification.CONTENT.URI, null);
		}

		if (existScheduleNotification) {
			playScheduleNotificationVoice();
		}

		if (existScheduleNotification || existExpectedChargeChangedNotification || existMemoChangedNotification) {
			// スケジュール通知はこの時点では新しい通知を表示しない（スケジュール自体の同期が終わっていない）ため、ここではpublishされない。
			executorService.execute(new GetOperationSchedulesTask(context, database, executorService, true));
		}
	}

	private List<VehicleNotificationJson> selectTargetJson(VehicleNotificationJson[] pulledJson) {
		List<VehicleNotificationJson> writeTargetJson = Lists.newLinkedList();
		List<Long> existIds = getExistIds();
		for (VehicleNotificationJson json : pulledJson) {
			if (existIds.contains(json.id)) continue;
			writeTargetJson.add(json);
		}
		return writeTargetJson;
	}

	private List<Long> getExistIds() {
		List<Long> existIds = Lists.newLinkedList();
		Cursor cursor = database.query(VehicleNotification.TABLE_NAME,
				new String[]{VehicleNotification.Columns._ID}, null, null,
				null, null, null);
		try {
			if (cursor.moveToFirst()) {
				do {
					existIds.add(cursor.getLong(cursor.getColumnIndexOrThrow(VehicleNotification.Columns._ID)));
				} while (cursor.moveToNext());
			}
		} finally {
			cursor.close();
		}
		return existIds;
	}

	// TODO: 音声再生の細かい処理は本来はcontentProviderにあるべきではないので、クラスを分けるべきかも
	private void playAdminNotificationVoice() {
		Voice chimeVoice = new ChimeVoice();
		Voice adminNotificationVoice = new AdminNotificationVoice();
		StaticVoicePlayService.playVoice(context, chimeVoice);
		StaticVoicePlayService.playVoice(context, adminNotificationVoice);
		StaticVoicePlayService.playVoice(context, chimeVoice);
		StaticVoicePlayService.playVoice(context, adminNotificationVoice);
	}

	// TODO: 音声再生の細かい処理は本来はcontentProviderにあるべきではないので、クラスを分けるべきかも
	private void playScheduleNotificationVoice() {
		Voice chimeVoice = new ChimeVoice();
		Voice scheduleChange = new ScheduleChangeVoice();
		StaticVoicePlayService.playVoice(context, chimeVoice);
		StaticVoicePlayService.playVoice(context, scheduleChange);
		StaticVoicePlayService.playVoice(context, chimeVoice);
		StaticVoicePlayService.playVoice(context, scheduleChange);
	}
}