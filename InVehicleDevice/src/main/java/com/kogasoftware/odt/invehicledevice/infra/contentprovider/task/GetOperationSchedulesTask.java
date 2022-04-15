package com.kogasoftware.odt.invehicledevice.infra.contentprovider.task;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Charsets;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.json.OperationRecordJson;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.json.OperationScheduleJson;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.json.PassengerRecordJson;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.json.PlatformJson;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.json.ReservationJson;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.json.UserJson;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.OperationRecord;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.Platform;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.Reservation;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.User;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.VehicleNotification;

import org.apache.http.HttpResponse;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 運行スケジュールの取得APIとの通信
 */
public class GetOperationSchedulesTask extends SynchronizationTask {
    static final String TAG = GetOperationSchedulesTask.class.getSimpleName();
    private final Boolean scheduleVehicleNotificationRequired;

    public GetOperationSchedulesTask(Context context, SQLiteDatabase database,
                                     ScheduledExecutorService executorService) {
        this(context, database, executorService, false);
    }

    public GetOperationSchedulesTask(Context context, SQLiteDatabase database,
                                     ScheduledExecutorService executorService, Boolean scheduleVehicleNotificationRequired) {
        super(context, database, executorService);
        this.scheduleVehicleNotificationRequired = scheduleVehicleNotificationRequired;
    }

    // operationSchedulesという名前だが、userやreservationといった関連データもすべて持っている。
    void insert(List<OperationScheduleJson> operationSchedules, List<Long> scheduleVehicleNotificationIds) {
        Log.i(TAG, "Start schedule insert.");

        List<UserJson> users = Lists.newLinkedList();
        List<ReservationJson> reservations = Lists.newLinkedList();
        List<PassengerRecordJson> passengerRecords = Lists.newLinkedList();
        List<OperationRecordJson> operationRecords = Lists.newLinkedList();
        List<PlatformJson> platforms = Lists.newLinkedList();
        Collections.sort(operationSchedules,
                (l, r) -> ComparisonChain.start()
                        .compare(l.arrivalEstimate, r.arrivalEstimate)
                        .compare(l.id, r.id).result());

        // 各モデルを配列に展開する
        for (OperationScheduleJson operationSchedule : operationSchedules) {
            OperationRecordJson operationRecord = operationSchedule.operationRecord;
            operationRecords.add(operationRecord);
            platforms.add(operationSchedule.platform);
            ReservationJson reservation = operationSchedule.departureReservation;
            if (reservation != null) {
                reservations.add(reservation);
                users.addAll(reservation.fellowUsers);

                passengerRecords.addAll(reservation.passengerRecords);
            }
        }

        // insert実行
        try {
            database.beginTransaction();

            Log.i(TAG, "Start vehicle notifications update.");
            for (Long vehicleNotificationId : scheduleVehicleNotificationIds) {
                ContentValues values = new ContentValues();
                values.put(VehicleNotification.Columns.SCHEDULE_DOWNLOADED, 1);
                String where = VehicleNotification.Columns._ID + " = ?";
                String[] whereArgs = new String[]{vehicleNotificationId.toString()};
                database.update(VehicleNotification.TABLE_NAME, values, where, whereArgs);
            }
            Log.i(TAG, "Finish vehicle notifications update.");

            Log.i(TAG, "Start exist data delete.");
            for (String table : new String[]{User.TABLE_NAME,
                    Reservation.TABLE_NAME, OperationSchedule.TABLE_NAME,
                    OperationRecord.TABLE_NAME, PassengerRecord.TABLE_NAME,
                    Platform.TABLE_NAME}) {
                database.delete(table, null, null);
            }
            Log.i(TAG, "Finish exist data delete.");

            Log.i(TAG, "Start users insert.");
            for (UserJson user : users) {
                database.replaceOrThrow(User.TABLE_NAME, null, user.toContentValues());
            }
            Log.i(TAG, "Finish users insert.");

            Log.i(TAG, "Start passenger records insert.");
            for (PassengerRecordJson passengerRecord : passengerRecords) {
                ContentValues values;
                try {
                    values = passengerRecord.toContentValues(reservations);
                } catch (IOException e) {
                    Log.e(TAG, "Can't create passenger_records values for id=" + passengerRecord.id, e);
                    continue;
                }
                database.replaceOrThrow(PassengerRecord.TABLE_NAME, null, values);
            }
            Log.i(TAG, "Finish passenger records insert.");

            Log.i(TAG, "Start operation records insert.");
            for (OperationRecordJson operationRecord : operationRecords) {
                database.replaceOrThrow(OperationRecord.TABLE_NAME, null, operationRecord.toContentValues());
            }
            Log.i(TAG, "Finish operation records insert.");

            Log.i(TAG, "Start platforms insert.");
            for (PlatformJson platform : platforms) {
                database.replaceOrThrow(Platform.TABLE_NAME, null, platform.toContentValues());
            }
            Log.i(TAG, "Finish platforms insert.");

            Log.i(TAG, "Start operation schedules insert.");
            for (OperationScheduleJson operationSchedule : operationSchedules) {
                ContentValues values = operationSchedule.toContentValues();
                // 乗り降り時刻を更新
                Long arrivedAt = null;
                Long departedAt = null;
                for (OperationRecordJson operationRecord : operationRecords) {
                    if (!operationSchedule.id.equals(operationRecord.operationScheduleId)) {
                        continue;
                    }

                    if (operationRecord.arrivedAt != null) {
                        long nextArrivedAt = operationRecord.arrivedAt.getMillis();
                        if (arrivedAt == null || arrivedAt > nextArrivedAt) {
                            arrivedAt = nextArrivedAt;
                        }
                    }
                    if (operationRecord.departedAt != null) {
                        long nextDepartedAt = operationRecord.departedAt.getMillis();
                        if (departedAt == null || departedAt < nextDepartedAt) {
                            departedAt = nextDepartedAt;
                        }
                    }
                }
                values.put(OperationSchedule.Columns.ARRIVED_AT, arrivedAt);
                values.put(OperationSchedule.Columns.DEPARTED_AT, departedAt);
                database.replaceOrThrow(OperationSchedule.TABLE_NAME, null, values);
            }
            Log.i(TAG, "Finish operation schedules insert.");

            Log.i(TAG, "Start reservations insert.");
            for (ReservationJson reservation : reservations) {
                database.replaceOrThrow(Reservation.TABLE_NAME, null, reservation.toContentValues());
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
        Log.i(TAG, "Finish reservations insert.");

        for (Long scheduleVehicleNotificationId : scheduleVehicleNotificationIds) {
            Uri notifyUri = ContentUris.withAppendedId(VehicleNotification.CONTENT.URI, scheduleVehicleNotificationId);
            contentResolver.notifyChange(notifyUri, null);
            Log.i(TAG, "Notify change: " + notifyUri.toString());
        }

        contentResolver.notifyChange(VehicleNotification.CONTENT.URI, null);
        Log.i(TAG, "Notify change: " + VehicleNotification.CONTENT.URI.toString());

        Uri[] notifyUris = new Uri[]{
                User.CONTENT.URI, Reservation.CONTENT.URI,
                OperationSchedule.CONTENT.URI, OperationRecord.CONTENT.URI,
                PassengerRecord.CONTENT.URI, Platform.CONTENT.URI
        };
        for (Uri uri : notifyUris) {
            contentResolver.notifyChange(uri, null);
            Log.i(TAG, "Notify change: " + uri.toString());
        }

        Log.i(TAG, "Finish  schedule insert.");
    }

    @Override
    protected void runSession(URI baseUri, String authenticationToken) {
        Log.i(TAG, "Start schedule sync.");
        if (isOperationRecordNotYetSync() || isPassengerRecordNotYetSync()) {
            Log.i(TAG, "dirty, retry");
            submitRetry();
            return;
        }

        final List<Long> oldScheduleVehicleNotificationIds = queryBeforeSyncScheduleVehicleNotificationIds();
        if (scheduleVehicleNotificationRequired && oldScheduleVehicleNotificationIds.isEmpty()) {
            Log.i(TAG, "required VehicleNotifications not found");
            return;
        }

        doHttpGet(baseUri, "operation_schedules", authenticationToken,
                new Callback() {
                    @Override
                    public void onSuccess(HttpResponse response, byte[] entity) {
                        save(new String(entity, Charsets.UTF_8), oldScheduleVehicleNotificationIds);
                    }

                    @Override
                    public void onFailure(HttpResponse response, byte[] entity) {
                        if (response.getStatusLine().getStatusCode() % 100 == 5) {
                            Log.i(TAG, "status 5xx, retry");
                            submitRetry();
                        } else {
                            Log.e(TAG, "onFailure: " + response.getStatusLine() + " entity=" + dumpEntity(entity));
                        }
                    }

                    @Override
                    public void onException(IOException e) {
                        Log.e(TAG, "onException: " , e);
                        submitRetry();
                    }
                });

        Log.i(TAG, "Finish schedule sync.");
    }

    // 通知は受け取ったがサーバからスケジュールの実態をまだ受け取っていない状態の、スケジュール通知のリストを返す
    private List<Long> queryBeforeSyncScheduleVehicleNotificationIds() {
        List<Long> ids = Lists.newLinkedList();
        String where = "("
                + VehicleNotification.Columns.NOTIFICATION_KIND + " = " + VehicleNotification.NotificationKind.SCHEDULE
                + " OR "
                + VehicleNotification.Columns.NOTIFICATION_KIND + " = " + VehicleNotification.NotificationKind.EXPECTED_CHARGE_CHANGED
                + " OR "
                + VehicleNotification.Columns.NOTIFICATION_KIND + " = " + VehicleNotification.NotificationKind.MEMO_CHANGED
                + " OR "
                + VehicleNotification.Columns.NOTIFICATION_KIND + "=" + VehicleNotification.NotificationKind.CREDIT_PAID_CHARGE_CHANGED
                + ")"
                + " AND " + VehicleNotification.Columns.SCHEDULE_DOWNLOADED + " = 0 ";
        try (Cursor cursor = database.query(VehicleNotification.TABLE_NAME, null, where, null, null, null, null)) {
            if (cursor.moveToFirst()) {
                do {
                    ids.add(cursor.getLong(cursor.getColumnIndexOrThrow(VehicleNotification.Columns._ID)));
                } while (cursor.moveToNext());
            }
        }
        return ids;
    }

    private Boolean isOperationRecordNotYetSync() {
        try (Cursor orCursor = database.query(OperationRecord.TABLE_NAME, null,
                OperationRecord.Columns.LOCAL_VERSION + " > "
                        + OperationRecord.Columns.SERVER_VERSION, null, null,
                null, null)) {
            // TODO:count
            if (orCursor.getCount() > 0) {
                Log.i(TAG, "modified OperationRecord found");
                return true;
            }
        }

        Log.i(TAG, "operation records are clean");
        return false;
    }

    private Boolean isPassengerRecordNotYetSync() {
        try (Cursor prCursor = database.query(PassengerRecord.TABLE_NAME, null,
                PassengerRecord.Columns.LOCAL_VERSION + " > "
                        + PassengerRecord.Columns.SERVER_VERSION, null, null,
                null, null)) {
            // TODO:count
            if (prCursor.getCount() > 0) {
                Log.i(TAG, "modified PassengerRecord found");
                return true;
            }
        }
        Log.i(TAG, "passenger records are clean");
        return false;
    }

    public void save(String entity, List<Long> oldScheduleVehicleNotificationIds) {
        if (isOperationRecordNotYetSync() || isPassengerRecordNotYetSync()) {
            Log.i(TAG, "dirty, retry");
            submitRetry();
            return;
        }

        // スケジュール通知の内、まだサーバから実際のスケジュールのデータを受け取っていない通知に変化がある場合に、再実行をする
        // TODO: APIでのスケジュールの取得中に、別枠で連携が完了してしまった様な場合を想定している？無限ループにならない？(0件は除外済みなのでならない？）
        // TODO: 上の処理のretryでこの処理に入るまでに時間が空くことがあるため、他の処理で終わっていないかを確認する必要がある？
        // TODO: 分かりにくいので目的をはっきりさせたい。不要なら消したい。
        List<Long> newScheduleVehicleNotificationIds = queryBeforeSyncScheduleVehicleNotificationIds();
        if (!oldScheduleVehicleNotificationIds.equals(newScheduleVehicleNotificationIds)) {
            Log.i(TAG, "required VehicleNotification doesn't match, retry");
            submitRetry();
            return;
        }

        List<OperationScheduleJson> operationScheduleJsons;
        try {
            operationScheduleJsons = JSON.readValue(entity, new TypeReference<List<OperationScheduleJson>>() {
            });
        } catch (IOException e) {
            Log.e(TAG, "IOException while parsing entity: " + entity, e);
            return;
        }

        insert(operationScheduleJsons, oldScheduleVehicleNotificationIds);
    }
}
