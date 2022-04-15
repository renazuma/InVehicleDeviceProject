package com.kogasoftware.odt.invehicledevice.infra.contentprovider.table;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.kogasoftware.android.CursorReader;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.InVehicleDeviceContentProvider;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.task.PatchPassengerRecordTask;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.util.ContentValuesUtils;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 乗車実績テーブル
 */
public class PassengerRecord implements Serializable {
    private static final long serialVersionUID = -8083144283252736834L;

    public static final int TABLE_CODE = 4;
    public static final String TABLE_NAME = "passenger_records";
    public static final Content CONTENT = new Content(TABLE_CODE, TABLE_NAME);

    public static class Columns implements BaseColumns {
        public static final String GET_ON_TIME = "get_on_time";
        public static final String GET_OFF_TIME = "get_off_time";
        public static final String PASSENGER_COUNT = "passenger_count";
        public static final String RESERVATION_ID = "reservation_id";
        public static final String USER_ID = "user_id";
        public static final String LOCAL_VERSION = "local_version";
        public static final String SERVER_VERSION = "server_version";
        public static final String IGNORE_GET_ON_MISS = "ignore_get_on_miss";
        public static final String IGNORE_GET_OFF_MISS = "ignore_get_off_miss";
        public static final String REPRESENTATIVE = "representative";
        public static final String EXPECTED_CHARGE = "expected_charge";
        public static final String PAID_CHARGE = "paid_charge";
        public static final String SETTLED = "settled";
    }

    public static final Comparator<PassengerRecord> DEFAULT_COMPARATOR = (l, r) -> ComparisonChain.start()
            .compare(l.reservationId, r.reservationId)
            .compareTrueFirst(l.representative, r.representative)
            .compare(l.getDisplayName(), r.getDisplayName())
            .compare(l.userId, r.userId).compare(l.id, r.id).result();

    // 運行予定画面、乗降画面の、乗客行の背景色
    public final Long id;
    public DateTime getOnTime;
    public DateTime getOffTime;
    public final String firstName;
    public final String lastName;
    public final Long reservationId;
    public final Long userId;
    public final Long arrivalScheduleId;
    public final Long departureScheduleId;
    public final String reservationMemo;
    public final String userMemo;
    public final Boolean handicapped;
    public final Boolean wheelchair;
    public final Boolean neededCare;
    public final Boolean licenseReturned;
    public Boolean ignoreGetOnMiss;
    public Boolean ignoreGetOffMiss;
    public final Boolean representative;
    public final Long passengerCount;
    public final Long expectedCharge;
    public Long paidCharge;
    public final Boolean settled;


    public PassengerRecord(Cursor cursor) {
        CursorReader reader = new CursorReader(cursor);
        id = reader.readLong(PassengerRecord.Columns._ID);
        getOnTime = reader.readDateTime(PassengerRecord.Columns.GET_ON_TIME);
        getOffTime = reader.readDateTime(PassengerRecord.Columns.GET_OFF_TIME);
        ignoreGetOnMiss = reader
                .readBoolean(PassengerRecord.Columns.IGNORE_GET_ON_MISS);
        ignoreGetOffMiss = reader
                .readBoolean(PassengerRecord.Columns.IGNORE_GET_OFF_MISS);
        reservationId = reader.readLong(PassengerRecord.Columns.RESERVATION_ID);
        userId = reader.readLong(PassengerRecord.Columns.USER_ID);
        reservationMemo = reader.readString("reservation_memo");
        userMemo = reader.readString("user_memo");
        arrivalScheduleId = reader
                .readLong(Reservation.Columns.ARRIVAL_SCHEDULE_ID);
        departureScheduleId = reader
                .readLong(Reservation.Columns.DEPARTURE_SCHEDULE_ID);
        firstName = reader.readString(User.Columns.FIRST_NAME);
        lastName = reader.readString(User.Columns.LAST_NAME);
        handicapped = reader.readBoolean(User.Columns.HANDICAPPED);
        wheelchair = reader.readBoolean(User.Columns.WHEELCHAIR);
        neededCare = reader.readBoolean(User.Columns.NEEDED_CARE);
        licenseReturned = reader.readBoolean(User.Columns.LICENSE_RETURNED);
        representative = reader
                .readBoolean(PassengerRecord.Columns.REPRESENTATIVE);
        passengerCount = reader
                .readLong(PassengerRecord.Columns.PASSENGER_COUNT);
        expectedCharge = reader.readLong(Columns.EXPECTED_CHARGE);
        paidCharge = reader.readLong(Columns.PAID_CHARGE);
        settled = reader.readBoolean(Columns.SETTLED);
    }

    public List<String> getUserNotes() {
        List<String> notes = new LinkedList<>();
        if (!userMemo.isEmpty()) {
            notes.add(userMemo);
        }
        if (handicapped) {
            notes.add("※身体障害者");
        }
        if (wheelchair) {
            notes.add("※要車椅子");
        }
        if (neededCare) {
            notes.add("※要介護");
        }
        if (licenseReturned) {
            notes.add("※免許返納");
        }
        return notes;
    }

    public static List<PassengerRecord> getAll(Cursor cursor) {
        List<PassengerRecord> results = Lists.newLinkedList();
        if (cursor.getCount() == 0) {
            return results;
        }
        int position = cursor.getPosition();
        cursor.moveToFirst();
        do {
            results.add(new PassengerRecord(cursor));
        } while (cursor.moveToNext());
        cursor.moveToPosition(position);
        return results;
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(PassengerRecord.Columns._ID, id);
        values.put(PassengerRecord.Columns.RESERVATION_ID, reservationId);
        values.put(PassengerRecord.Columns.USER_ID, userId);
        values.put(PassengerRecord.Columns.IGNORE_GET_ON_MISS, ignoreGetOnMiss);
        values.put(PassengerRecord.Columns.IGNORE_GET_OFF_MISS,
                ignoreGetOffMiss);
        ContentValuesUtils.putDateTime(values,
                PassengerRecord.Columns.GET_ON_TIME, getOnTime);
        ContentValuesUtils.putDateTime(values,
                PassengerRecord.Columns.GET_OFF_TIME, getOffTime);
        values.put(Columns.EXPECTED_CHARGE, expectedCharge);
        values.put(Columns.PAID_CHARGE, paidCharge);
        return values;
    }

    public String getDisplayName() {
        return lastName + " " + firstName;
    }

    public static int update(InVehicleDeviceContentProvider contentProvider,
                             ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase database = contentProvider.getDatabase();
        ContentResolver contentResolver = contentProvider.getContext()
                .getContentResolver();
        ScheduledExecutorService executorService = contentProvider
                .getExecutorService();
        int affected;
        String table = PassengerRecord.TABLE_NAME;
        database.beginTransaction();
        try {
            long maxVersion = 1L;
            try (Cursor cursor = database.query(table, null, selection,
                    selectionArgs, null, null, null)) {
                // TODO: MAX(local_version)で書き直す
                if (cursor.moveToFirst()) {
                    do {
                        long version = cursor
                                .getLong(cursor
                                        .getColumnIndexOrThrow(Columns.LOCAL_VERSION));
                        if (version > maxVersion) {
                            maxVersion = version;
                        }
                    } while (cursor.moveToNext());
                }
            }
            values.put(PassengerRecord.Columns.LOCAL_VERSION, maxVersion + 1);
            affected = database.update(table, values, selection, selectionArgs);
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
        contentResolver.notifyChange(PassengerRecord.CONTENT.URI, null);
        executorService.execute(new PatchPassengerRecordTask(contentProvider
                .getContext(), database, executorService));
        return affected;
    }

    public static Cursor query(InVehicleDeviceContentProvider contentProvider,
                               String[] projection, String selection, String[] selectionArgs,
                               String sortOrder) {
        SQLiteDatabase database = contentProvider.getDatabase();
        ContentResolver contentResolver = contentProvider.getContext()
                .getContentResolver();
        // TODO:
        String sql = " select pr.*" +
                " , r.memo reservation_memo" +
                " , r.arrival_schedule_id" +
                " , r.departure_schedule_id" +
                " , r.settled" +
                " , u.first_name" +
                " , u.last_name" +
                " , u.memo user_memo" +
                " , u.handicapped" +
                " , u.needed_care" +
                " , u.wheelchair" +
                " , u.license_returned" +
                " from passenger_records pr" +
                " inner join reservations r on pr.reservation_id = r._id" +
                " inner join users u on pr.user_id = u._id" +
                " order by _id";
        Cursor cursor = database.rawQuery(sql, null);
        cursor.setNotificationUri(contentResolver, PassengerRecord.CONTENT.URI);
        return cursor;
    }
}
