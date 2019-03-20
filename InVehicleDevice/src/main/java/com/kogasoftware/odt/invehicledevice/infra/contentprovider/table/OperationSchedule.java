package com.kogasoftware.odt.invehicledevice.infra.contentprovider.table;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.widget.Toast;

import com.google.common.collect.Lists;
import com.kogasoftware.android.CursorReader;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.InVehicleDeviceContentProvider;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.task.PatchOperationRecordTask;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.util.ContentValuesUtils;
import com.kogasoftware.odt.invehicledevice.view.BigToast;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 運行スケジュールテーブル
 */
public class OperationSchedule implements Serializable {
	private static final long serialVersionUID = -2332224258753742183L;

	public static enum Phase {
		DRIVE, FINISH, PLATFORM_GET_OFF, PLATFORM_GET_ON,
	};

	public static final int TABLE_CODE = 3;
	public static final String TABLE_NAME = "operation_schedules";
	public static final Content CONTENT = new Content(TABLE_CODE, TABLE_NAME);

	public static class Columns implements BaseColumns {
		public static final String ARRIVAL_ESTIMATE = "arrival_estimate";
		public static final String DEPARTURE_ESTIMATE = "departure_estimate";
		public static final String PLATFORM_ID = "platform_id";
		public static final String ARRIVED_AT = "arrived_at";
		public static final String DEPARTED_AT = "departed_at";
		public static final String COMPLETE_GET_OFF = "complete_get_off";
		public static final String OPERATION_DATE = "operation_date";
	}

	public Long id;
	public DateTime arrivalEstimate;
	public DateTime departureEstimate;
	public DateTime arrivedAt;
	public DateTime departedAt;
	public String name;
	public String nameRuby;
	public String address;
	public Long platformId;
	public BigDecimal latitude;
	public BigDecimal longitude;
	public String memo;
	public Boolean completeGetOff;

	public OperationSchedule(Cursor cursor_) {
		CursorReader reader = new CursorReader(cursor_);
		id = reader.readLong(OperationSchedule.Columns._ID);
		arrivalEstimate = reader.readDateTime(OperationSchedule.Columns.ARRIVAL_ESTIMATE);
		departureEstimate = reader.readDateTime(OperationSchedule.Columns.DEPARTURE_ESTIMATE);
		arrivedAt = reader.readDateTime(OperationRecord.Columns.ARRIVED_AT);
		departedAt = reader.readDateTime(OperationRecord.Columns.DEPARTED_AT);
		platformId = reader.readLong(OperationSchedule.Columns.PLATFORM_ID);
		completeGetOff = reader.readBoolean(OperationSchedule.Columns.COMPLETE_GET_OFF);
		name = reader.readString(Platform.Columns.NAME);
		nameRuby = reader.readString(Platform.Columns.NAME_RUBY);
		memo = reader.readString(Platform.Columns.MEMO);
		address = reader.readString(Platform.Columns.ADDRESS);
		latitude = reader.readBigDecimal(Platform.Columns.LATITUDE);
		longitude = reader.readBigDecimal(Platform.Columns.LONGITUDE);
	}

	public static LinkedList<OperationSchedule> getAll(Cursor cursor) {
		LinkedList<OperationSchedule> results = Lists.newLinkedList();
		if (cursor.getCount() == 0) { return results; }
		cursor.moveToFirst();
		Integer position = cursor.getPosition();
		do {
			results.add(new OperationSchedule(cursor));
		} while (cursor.moveToNext());
		cursor.moveToPosition(position);
		return results;
	}

	public void startNavigation(Context context) {
		String uri = String.format(Locale.US, "google.navigation:q=%f,%f",
				latitude.doubleValue(), longitude.doubleValue());
		Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
		intent.setClassName("com.google.android.apps.maps","com.google.android.maps.MapsActivity");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			context.startActivity(intent);
		} catch (ActivityNotFoundException e) {
			BigToast.makeText(context, "GoogleMapsが存在しないため、地図を表示できません", Toast.LENGTH_LONG).show();
		}
	}

	public ContentValues toContentValues() {
		ContentValues values = new ContentValues();
		values.put(OperationSchedule.Columns._ID, id);
		ContentValuesUtils.putDateTime(values, OperationSchedule.Columns.ARRIVAL_ESTIMATE, arrivalEstimate);
		ContentValuesUtils.putDateTime(values, OperationSchedule.Columns.DEPARTURE_ESTIMATE, departureEstimate);
		ContentValuesUtils.putDateTime(values, OperationSchedule.Columns.ARRIVED_AT, arrivedAt);
		ContentValuesUtils.putDateTime(values, OperationSchedule.Columns.DEPARTED_AT, departedAt);
		values.put(OperationSchedule.Columns.PLATFORM_ID, platformId);
		values.put(OperationSchedule.Columns.COMPLETE_GET_OFF, completeGetOff);
		return values;
	}

	public static OperationSchedule getCurrent(List<OperationSchedule> operationSchedules) {
		return getCurrentOffset(operationSchedules, 0);
	}

	public static OperationSchedule getCurrentOffset(List<OperationSchedule> operationSchedules, Integer offset) {
		for (Integer i = 0; i < operationSchedules.size() - offset; i++) {
			if (operationSchedules.get(i).arrivedAt == null	|| operationSchedules.get(i).departedAt == null) {
				return operationSchedules.get(i + offset);
			}
		}
		return null;
	}

	public static Phase getPhase(List<OperationSchedule> operationSchedules, List<PassengerRecord> passengerRecords) {
		OperationSchedule operationSchedule = OperationSchedule.getCurrent(operationSchedules);
		if (operationSchedule == null) {
			return Phase.FINISH;
		} else if (operationSchedule.arrivedAt == null && operationSchedule.departedAt == null) {
			return Phase.DRIVE;
		} else if (operationSchedule.completeGetOff
				|| operationSchedule.getGetOffScheduledPassengerRecords(passengerRecords).isEmpty()) {
			return Phase.PLATFORM_GET_ON;
		} else {
			return Phase.PLATFORM_GET_OFF;
		}
	}

	public List<PassengerRecord> getNoGetOffErrorPassengerRecords(List<PassengerRecord> passengerRecords) {
		List<PassengerRecord> results = Lists.newLinkedList();
		for (PassengerRecord passengerRecord : getGetOffScheduledPassengerRecords(passengerRecords)) {
			if (passengerRecord.getOffTime == null) { results.add(passengerRecord); }
		}
		return results;
	}

	public List<PassengerRecord> getGetOnScheduledPassengerRecords(List<PassengerRecord> passengerRecords) {
		List<PassengerRecord> results = Lists.newLinkedList();
		for (PassengerRecord passengerRecord : passengerRecords) {
			if (id.equals(passengerRecord.departureScheduleId)) { results.add(passengerRecord); }
		}
		return results;
	}

	public List<PassengerRecord> getNoGetOnErrorPassengerRecords(List<PassengerRecord> passengerRecords) {
		List<PassengerRecord> results = Lists.newLinkedList();
		for (PassengerRecord passengerRecord : getGetOnScheduledPassengerRecords(passengerRecords)) {
			if (passengerRecord.getOnTime == null) { results.add(passengerRecord); }
		}
		return results;
	}

	public List<PassengerRecord> getGetOffScheduledPassengerRecords(List<PassengerRecord> passengerRecords) {
		List<PassengerRecord> results = Lists.newLinkedList();
		for (PassengerRecord passengerRecord : passengerRecords) {
			if (id.equals(passengerRecord.arrivalScheduleId)) { results.add(passengerRecord); }
		}
		return results;
	}

	public static OperationSchedule getById(List<OperationSchedule> operationSchedules, Long operationScheduleId) {
		for (OperationSchedule operationSchedule : operationSchedules) {
			if (operationScheduleId.equals(operationSchedule.id)) { return operationSchedule; }
		}
		return null;
	}

	public static Uri replace(ContentValues values,	InVehicleDeviceContentProvider contentProvider) {
		SQLiteDatabase database = contentProvider.getDatabase();
		ContentResolver contentResolver = contentProvider.getContext().getContentResolver();
		ScheduledExecutorService executorService = contentProvider.getExecutorService();
		database.beginTransaction();
		try {
			Long id = database.replaceOrThrow(OperationSchedule.TABLE_NAME, null, values);
			Uri uri = ContentUris.withAppendedId(OperationSchedule.CONTENT.URI, id);
			ContentValues operationRecordValues = new ContentValues();
			operationRecordValues.put(OperationRecord.Columns.ARRIVED_AT, values.getAsLong(OperationSchedule.Columns.ARRIVED_AT));
			operationRecordValues.put(OperationRecord.Columns.DEPARTED_AT, values.getAsLong(OperationSchedule.Columns.DEPARTED_AT));

			// TODO: MAX(local_version)で書き直す
			String where = OperationRecord.Columns.OPERATION_SCHEDULE_ID + " = ?";
			String[] whereArgs = new String[]{id.toString()};
			Long maxVersion = 1L;
			Cursor cursor = database.query(OperationRecord.TABLE_NAME, null,
					where, whereArgs, null, null, null);
			try {
				if (cursor.moveToFirst()) {
					do {
						Long version = cursor.getLong(cursor.getColumnIndexOrThrow(OperationRecord.Columns.LOCAL_VERSION));
						if (version > maxVersion) {	maxVersion = version; }
					} while (cursor.moveToNext());
				}
			} finally {
				cursor.close();
			}
			operationRecordValues.put(OperationRecord.Columns.LOCAL_VERSION, maxVersion + 1);
			database.update(OperationRecord.TABLE_NAME, operationRecordValues, where, whereArgs);
			executorService.execute(new PatchOperationRecordTask(contentProvider.getContext(), database, executorService));
			contentResolver.notifyChange(OperationSchedule.CONTENT.URI, null);
			contentResolver.notifyChange(uri, null);
			database.setTransactionSuccessful();
			return uri;
		} finally {
			database.endTransaction();
		}
	}

	public static Cursor query(InVehicleDeviceContentProvider contentProvider,
			String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteDatabase database = contentProvider.getDatabase();
		ContentResolver contentResolver = contentProvider.getContext().getContentResolver();
		// TODO:
		StringBuilder sql = new StringBuilder();
		sql.append(" select os.*");
		sql.append(" , p.name");
		sql.append(" , p.name_ruby");
		sql.append(" , p.memo");
		sql.append(" , p.address");
		sql.append(" , p.latitude");
		sql.append(" , p.longitude");
		sql.append(" from operation_schedules os");
		sql.append(" inner join platforms p on os.platform_id = p._id");
		sql.append(" order by arrival_estimate;");
		Cursor cursor = database.rawQuery(sql.toString(), null);
		cursor.setNotificationUri(contentResolver, OperationSchedule.CONTENT.URI);
		return cursor;
	}
}
