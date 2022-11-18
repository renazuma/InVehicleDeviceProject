package com.kogasoftware.odt.invehicledevice.infra.contentprovider.task;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.json.VehicleNotificationJson;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.VehicleNotification;
import com.kogasoftware.odt.invehicledevice.service.voicenotificationservice.VoiceNotificationService;
import com.kogasoftware.odt.invehicledevice.service.voicenotificationservice.voice.AdminNotification;
import com.kogasoftware.odt.invehicledevice.service.voicenotificationservice.voice.Chime;
import com.kogasoftware.odt.invehicledevice.service.voicenotificationservice.voice.ScheduleChange;
import com.kogasoftware.odt.invehicledevice.service.voicenotificationservice.voice.Voice;

import org.apache.http.HttpResponse;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
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
        Log.i(TAG, "Start schedule sync.");
        doHttpGet(baseUri, "vehicle_notifications", authenticationToken, logCallback);
        Log.i(TAG, "Finish schedule sync.");
    }

    final LogCallback logCallback = new LogCallback(TAG) {
        @Override
        public void onSuccess(HttpResponse response, byte[] entity) {
            VehicleNotificationJson[] vehicleNotifications;
            try {
                vehicleNotifications = JSON.readValue(new String(entity, Charsets.UTF_8), VehicleNotificationJson[].class);
            } catch (IOException e) {
                Log.e(TAG, "ParseError: " + Arrays.toString(entity), e);
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
        boolean existCreditPayedChargeChangedNotification = false;

        Log.i(TAG, "Start Notification Data Insert.");
        try {
            database.beginTransaction();
            for (VehicleNotificationJson json : selectTargetJson(pulledJson)) {
                database.insertOrThrow(VehicleNotification.TABLE_NAME, null, json.toContentValues());

                if (json.isAdminNotification()) {
                    existAdminNotification = true;
                } else if (json.isScheduleNotification()) {
                    existScheduleNotification = true;
                } else if (json.isExpectedChargeChangedNotification()) {
                    existExpectedChargeChangedNotification = true;
                } else if (json.isMemoChangedNotification()) {
                    existMemoChangedNotification = true;
                } else if(json.isCreditPaidChargeChangedNotification()) {
                    existCreditPayedChargeChangedNotification = true;
                }
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
        Log.i(TAG, "Finish Notification Data Insert.");

        if (existAdminNotification) {
            playAdminNotificationVoice();
            contentResolver.notifyChange(VehicleNotification.CONTENT.URI, null);
        }

        if (existScheduleNotification) {
            playScheduleNotificationVoice();
            Log.i(TAG, "Schedule Notification voice has been played.");
        }

        if (existScheduleNotification || existExpectedChargeChangedNotification || existMemoChangedNotification || existCreditPayedChargeChangedNotification) {
            // スケジュール通知はこの時点では新しい通知を表示しない（スケジュール自体の同期が終わっていない）ため、ここではpublishされない。
            executorService.execute(new GetOperationSchedulesTask(context, database, executorService, true));
            Log.i(TAG, "Schedule sync executor set.");
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
        try (Cursor cursor = database.query(VehicleNotification.TABLE_NAME,
                new String[]{VehicleNotification.Columns._ID}, null, null,
                null, null, null)) {
            if (cursor.moveToFirst()) {
                do {
                    existIds.add(cursor.getLong(cursor.getColumnIndexOrThrow(VehicleNotification.Columns._ID)));
                } while (cursor.moveToNext());
            }
        }
        return existIds;
    }

    // TODO: 音声再生の細かい処理は本来はcontentProviderにあるべきではないので、クラスを分けるべきかも
    private void playAdminNotificationVoice() {
        Voice chimeVoice = new Chime();
        Voice adminNotificationVoice = new AdminNotification();
        VoiceNotificationService.playVoice(context, chimeVoice);
        VoiceNotificationService.playVoice(context, adminNotificationVoice);
        VoiceNotificationService.playVoice(context, chimeVoice);
        VoiceNotificationService.playVoice(context, adminNotificationVoice);
    }

    // TODO: 音声再生の細かい処理は本来はcontentProviderにあるべきではないので、クラスを分けるべきかも
    private void playScheduleNotificationVoice() {
        Voice chimeVoice = new Chime();
        Voice scheduleChange = new ScheduleChange();
        VoiceNotificationService.playVoice(context, chimeVoice);
        VoiceNotificationService.playVoice(context, scheduleChange);
        VoiceNotificationService.playVoice(context, chimeVoice);
        VoiceNotificationService.playVoice(context, scheduleChange);
    }
}
