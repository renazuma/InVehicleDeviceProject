package com.kogasoftware.odt.invehicledevice.infra.contentprovider.table;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

import com.google.common.collect.Lists;
import com.kogasoftware.android.CursorReader;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.InVehicleDeviceContentProvider;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.task.PatchVehicleNotificationTask;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.util.ContentValuesUtils;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 車載器通知テーブル
 */
public class VehicleNotification implements Serializable {
    private static final long serialVersionUID = -295331215588438885L;
    public static final String WHERE_SCHEDULE_VEHICLE_NOTIFICATION_FRAGMENT_CONTENT = String
            .format(Locale.US, "%s = %d AND %s IS NULL AND %s > 0",
                    VehicleNotification.Columns.NOTIFICATION_KIND,
                    NotificationKind.SCHEDULE,
                    VehicleNotification.Columns.RESPONSE,
                    VehicleNotification.Columns.SCHEDULE_DOWNLOADED);

    public static final String WHERE_ADMIN_NOTIFICATION_FRAGMENT_CONTENT = String
            .format(Locale.US, "%s = %s AND %s IS NULL",
                    VehicleNotification.Columns.NOTIFICATION_KIND,
                    NotificationKind.NORMAL,
                    VehicleNotification.Columns.RESPONSE);

    public static final String WHERE_EXPECTED_CHARGE_CHANGED_VEHICLE_NOTIFICATION_FRAGMENT_CONTENT = String
            .format(Locale.US, "%s = %d AND %s IS NULL AND %s > 0",
                    VehicleNotification.Columns.NOTIFICATION_KIND,
                    NotificationKind.EXPECTED_CHARGE_CHANGED,
                    VehicleNotification.Columns.RESPONSE,
                    VehicleNotification.Columns.SCHEDULE_DOWNLOADED);

    public static final String WHERE_MEMO_CHANGED_VEHICLE_NOTIFICATION_FRAGMENT_CONTENT = String
            .format(Locale.US, "%s = %d AND %s IS NULL AND %s > 0",
                    VehicleNotification.Columns.NOTIFICATION_KIND,
                    NotificationKind.MEMO_CHANGED,
                    VehicleNotification.Columns.RESPONSE,
                    VehicleNotification.Columns.SCHEDULE_DOWNLOADED);

    public static final int TABLE_CODE = 2;
    public static final String TABLE_NAME = "vehicle_notifications";
    public static final Content CONTENT = new Content(TABLE_CODE, TABLE_NAME);

    public static class Columns implements BaseColumns {
        public static final String BODY = "body";
        public static final String BODY_RUBY = "body_ruby";
        public static final String NOTIFICATION_KIND = "notification_kind";
        public static final String READ_AT = "read_at";
        public static final String RESPONSE = "response";
        public static final String SCHEDULE_DOWNLOADED = "schedule_downloaded";
    }

    public static class Response {
        public static final Long YES = 0L;
        public static final Long NO = 1L;
    }

    public static class NotificationKind {
        public static final Long NORMAL = 0L;
        public static final Long SCHEDULE = 1L;
        public static final Long EXPECTED_CHARGE_CHANGED = 2L;
        public static final Long MEMO_CHANGED = 3L;
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
        id = reader.readLong(VehicleNotification.Columns._ID);
        body = reader.readString(VehicleNotification.Columns.BODY);
        bodyRuby = reader.readString(VehicleNotification.Columns.BODY_RUBY);
        notificationKind = reader
                .readLong(VehicleNotification.Columns.NOTIFICATION_KIND);
        response = reader.readLong(VehicleNotification.Columns.RESPONSE);
        readAt = reader.readDateTime(VehicleNotification.Columns.READ_AT);
        scheduleDownloaded = reader
                .readLong(VehicleNotification.Columns.SCHEDULE_DOWNLOADED);
    }

    public static List<VehicleNotification> getAll(Cursor cursor) {
        List<VehicleNotification> results = Lists.newLinkedList();
        if (cursor.getCount() == 0) {
            return results;
        }
        int position = cursor.getPosition();
        cursor.moveToFirst();
        do {
            results.add(new VehicleNotification(cursor));
        } while (cursor.moveToNext());
        cursor.moveToPosition(position);
        return results;
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(VehicleNotification.Columns._ID, id);
        values.put(VehicleNotification.Columns.BODY, body);
        values.put(VehicleNotification.Columns.BODY_RUBY, bodyRuby);
        values.put(VehicleNotification.Columns.NOTIFICATION_KIND,
                notificationKind);
        values.put(VehicleNotification.Columns.RESPONSE, response);
        values.put(VehicleNotification.Columns.SCHEDULE_DOWNLOADED,
                scheduleDownloaded);
        ContentValuesUtils.putDateTime(values,
                VehicleNotification.Columns.READ_AT, readAt);
        return values;
    }

    public static Uri replace(ContentValues values,
                              InVehicleDeviceContentProvider contentProvider) {
        SQLiteDatabase database = contentProvider.getDatabase();
        ContentResolver contentResolver = contentProvider.getContext()
                .getContentResolver();
        ScheduledExecutorService executorService = contentProvider
                .getExecutorService();
        long id = database.replaceOrThrow(VehicleNotification.TABLE_NAME, null,
                values);
        Uri uri = ContentUris.withAppendedId(InVehicleDevice.CONTENT.URI, id);
        executorService.execute(new PatchVehicleNotificationTask(
                contentProvider.getContext(), database, executorService));
        contentResolver.notifyChange(VehicleNotification.CONTENT.URI, null);
        contentResolver.notifyChange(uri, null);
        return uri;
    }

    public static Cursor query(InVehicleDeviceContentProvider contentProvider,
                               String[] projection, String selection, String[] selectionArgs,
                               String sortOrder) {
        Cursor cursor = contentProvider.getDatabase().query(
                VehicleNotification.TABLE_NAME, projection, selection,
                selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(contentProvider.getContext()
                .getContentResolver(), VehicleNotification.CONTENT.URI);
        return cursor;
    }
}
