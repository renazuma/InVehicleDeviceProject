package com.kogasoftware.odt.invehicledevice.contentprovider.model;

import java.io.Serializable;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import org.joda.time.DateTime;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.kogasoftware.android.CursorReader;
import com.kogasoftware.odt.invehicledevice.contentprovider.InVehicleDeviceContentProvider;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.PassengerRecords;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.Reservations;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.Users;
import com.kogasoftware.odt.invehicledevice.contentprovider.task.PatchPassengerRecordTask;
import com.kogasoftware.odt.invehicledevice.utils.ContentValuesUtils;

public class PassengerRecord implements Serializable {
	private static final long serialVersionUID = -8083144283252736834L;
	public static final Comparator<PassengerRecord> DEFAULT_COMPARATOR = new Comparator<PassengerRecord>() {
		@Override
		public int compare(PassengerRecord l, PassengerRecord r) {
			return ComparisonChain.start()
					.compare(r.reservationId, l.reservationId)
					.compareTrueFirst(r.representative, l.representative)
					.compare(r.getDisplayName(), l.getDisplayName())
					.compare(r.userId, l.userId).compare(r.id, l.id).result();
		}
	};
	public static final int SELECTED_GET_OFF_COLOR = Color
			.parseColor("#40E0D0");
	public static final int GET_OFF_COLOR = Color.parseColor("#D5E9F6");
	public static final int SELECTED_GET_ON_COLOR = Color.parseColor("#FF69B4");
	public static final int GET_ON_COLOR = Color.parseColor("#F9D9D8");
	public Long id;
	public DateTime getOnTime;
	public DateTime getOffTime;
	public String firstName;
	public String lastName;
	public Long reservationId;
	public Long userId;
	public Long arrivalScheduleId;
	public Long departureScheduleId;
	public String reservationMemo;
	public String userMemo;
	public Boolean handicapped;
	public Boolean wheelchair;
	public Boolean neededCare;
	public Boolean ignoreGetOnMiss;
	public Boolean ignoreGetOffMiss;
	public Boolean representative;
	public Long passengerCount;

	public PassengerRecord(Cursor cursor) {
		CursorReader reader = new CursorReader(cursor);
		id = reader.readLong(PassengerRecords.Columns._ID);
		getOnTime = reader.readDateTime(PassengerRecords.Columns.GET_ON_TIME);
		getOffTime = reader.readDateTime(PassengerRecords.Columns.GET_OFF_TIME);
		ignoreGetOnMiss = reader
				.readBoolean(PassengerRecords.Columns.IGNORE_GET_ON_MISS);
		ignoreGetOffMiss = reader
				.readBoolean(PassengerRecords.Columns.IGNORE_GET_OFF_MISS);
		reservationId = reader
				.readLong(PassengerRecords.Columns.RESERVATION_ID);
		userId = reader.readLong(PassengerRecords.Columns.USER_ID);
		reservationMemo = reader.readString("reservation_memo");
		userMemo = reader.readString("user_memo");
		arrivalScheduleId = reader
				.readLong(Reservations.Columns.ARRIVAL_SCHEDULE_ID);
		departureScheduleId = reader
				.readLong(Reservations.Columns.DEPARTURE_SCHEDULE_ID);
		firstName = reader.readString(Users.Columns.FIRST_NAME);
		lastName = reader.readString(Users.Columns.LAST_NAME);
		handicapped = reader.readBoolean(Users.Columns.HANDICAPPED);
		wheelchair = reader.readBoolean(Users.Columns.WHEELCHAIR);
		neededCare = reader.readBoolean(Users.Columns.NEEDED_CARE);
		representative = reader
				.readBoolean(PassengerRecords.Columns.REPRESENTATIVE);
		passengerCount = reader
				.readLong(PassengerRecords.Columns.PASSENGER_COUNT);
	}

	public List<String> getUserNotes() {
		List<String> notes = new LinkedList<String>();
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
		return notes;
	}

	public static List<PassengerRecord> getAll(Cursor cursor) {
		List<PassengerRecord> results = Lists.newLinkedList();
		if (cursor.getCount() == 0) {
			return results;
		}
		Integer position = cursor.getPosition();
		cursor.moveToFirst();
		do {
			results.add(new PassengerRecord(cursor));
		} while (cursor.moveToNext());
		cursor.moveToPosition(position);
		return results;
	}

	public ContentValues toContentValues() {
		ContentValues values = new ContentValues();
		values.put(PassengerRecords.Columns._ID, id);
		values.put(PassengerRecords.Columns.RESERVATION_ID, reservationId);
		values.put(PassengerRecords.Columns.USER_ID, userId);
		values.put(PassengerRecords.Columns.IGNORE_GET_ON_MISS, ignoreGetOnMiss);
		values.put(PassengerRecords.Columns.IGNORE_GET_OFF_MISS,
				ignoreGetOffMiss);
		ContentValuesUtils.putDateTime(values,
				PassengerRecords.Columns.GET_ON_TIME, getOnTime);
		ContentValuesUtils.putDateTime(values,
				PassengerRecords.Columns.GET_OFF_TIME, getOffTime);
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
		int affected = 0;
		String table = PassengerRecords.TABLE_NAME;
		database.beginTransaction();
		try {
			Long maxVersion = 1L;
			Cursor cursor = database.query(table, null, selection,
					selectionArgs, null, null, null);
			try {
				// TODO: MAX(local_version)で書き直す
				if (cursor.moveToFirst()) {
					do {
						Long version = cursor
								.getLong(cursor
										.getColumnIndexOrThrow(PassengerRecords.Columns.LOCAL_VERSION));
						if (version > maxVersion) {
							maxVersion = version;
						}
					} while (cursor.moveToNext());
				}
			} finally {
				cursor.close();
			}
			values.put(PassengerRecords.Columns.LOCAL_VERSION, maxVersion + 1);
			affected = database.update(table, values, selection, selectionArgs);
			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
		}
		contentResolver.notifyChange(PassengerRecords.CONTENT.URI, null);
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
		StringBuilder sql = new StringBuilder();
		sql.append(" select pr.*");
		sql.append(" , r.memo reservation_memo");
		sql.append(" , r.arrival_schedule_id");
		sql.append(" , r.departure_schedule_id");
		sql.append(" , u.first_name");
		sql.append(" , u.last_name");
		sql.append(" , u.memo user_memo");
		sql.append(" , u.handicapped");
		sql.append(" , u.needed_care");
		sql.append(" , u.wheelchair");
		sql.append(" from passenger_records pr");
		sql.append(" inner join reservations r on pr.reservation_id = r._id");
		sql.append(" inner join users u on pr.user_id = u._id");
		sql.append(" order by _id");
		Cursor cursor = database.rawQuery(sql.toString(), null);
		cursor.setNotificationUri(contentResolver, PassengerRecords.CONTENT.URI);
		return cursor;
	}
}
