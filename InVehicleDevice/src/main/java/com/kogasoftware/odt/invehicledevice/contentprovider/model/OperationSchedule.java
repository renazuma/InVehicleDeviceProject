package com.kogasoftware.odt.invehicledevice.contentprovider.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ScheduledExecutorService;

import org.joda.time.DateTime;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.widget.Toast;

import com.google.common.collect.Lists;
import com.kogasoftware.android.CursorReader;
import com.kogasoftware.odt.invehicledevice.contentprovider.InVehicleDeviceContentProvider;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.OperationRecords;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.OperationSchedules;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.Platforms;
import com.kogasoftware.odt.invehicledevice.contentprovider.task.PatchOperationRecordTask;
import com.kogasoftware.odt.invehicledevice.ui.BigToast;
import com.kogasoftware.odt.invehicledevice.utils.ContentValuesUtils;

public class OperationSchedule implements Serializable {
	private static final long serialVersionUID = -2332224258753742183L;

	public static enum Phase {
		DRIVE, FINISH, PLATFORM_GET_OFF, PLATFORM_GET_ON,
	};

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
	public DateTime operationDate;

	public OperationSchedule(Cursor cursor_) {
		CursorReader reader = new CursorReader(cursor_);
		id = reader.readLong(OperationSchedules.Columns._ID);
		arrivalEstimate = reader
				.readDateTime(OperationSchedules.Columns.ARRIVAL_ESTIMATE);
		departureEstimate = reader
				.readDateTime(OperationSchedules.Columns.DEPARTURE_ESTIMATE);
		arrivedAt = reader.readDateTime(OperationRecords.Columns.ARRIVED_AT);
		departedAt = reader.readDateTime(OperationRecords.Columns.DEPARTED_AT);
		platformId = reader.readLong(OperationSchedules.Columns.PLATFORM_ID);
		completeGetOff = reader
				.readBoolean(OperationSchedules.Columns.COMPLETE_GET_OFF);
		name = reader.readString(Platforms.Columns.NAME);
		nameRuby = reader.readString(Platforms.Columns.NAME_RUBY);
		memo = reader.readString(Platforms.Columns.MEMO);
		address = reader.readString(Platforms.Columns.ADDRESS);
		latitude = reader.readBigDecimal(Platforms.Columns.LATITUDE);
		longitude = reader.readBigDecimal(Platforms.Columns.LONGITUDE);
		operationDate = reader
				.readDateTime(OperationSchedules.Columns.OPERATION_DATE);
	}

	public static LinkedList<OperationSchedule> getAll(Cursor cursor) {
		LinkedList<OperationSchedule> results = Lists.newLinkedList();
		if (cursor.getCount() == 0) {
			return results;
		}
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
		Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
				Uri.parse(uri));
		intent.setClassName("com.google.android.apps.maps",
				"com.google.android.maps.MapsActivity");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			context.startActivity(intent);
		} catch (ActivityNotFoundException e) {
			BigToast.makeText(context, "GoogleMapsが存在しないため、地図を表示できません",
					Toast.LENGTH_LONG).show();
		}
	}

	public ContentValues toContentValues() {
		ContentValues values = new ContentValues();
		values.put(OperationSchedules.Columns._ID, id);
		ContentValuesUtils.putDateTime(values,
				OperationSchedules.Columns.ARRIVAL_ESTIMATE, arrivalEstimate);
		ContentValuesUtils.putDateTime(values,
				OperationSchedules.Columns.DEPARTURE_ESTIMATE,
				departureEstimate);
		ContentValuesUtils.putDateTime(values,
				OperationSchedules.Columns.ARRIVED_AT, arrivedAt);
		ContentValuesUtils.putDateTime(values,
				OperationSchedules.Columns.DEPARTED_AT, departedAt);
		values.put(OperationSchedules.Columns.PLATFORM_ID, platformId);
		values.put(OperationSchedules.Columns.COMPLETE_GET_OFF, completeGetOff);
		ContentValuesUtils.putDateTime(values,
				OperationSchedules.Columns.OPERATION_DATE, operationDate);
		return values;
	}

	public static OperationSchedule getCurrent(
			List<OperationSchedule> operationSchedules) {
		return getCurrentOffset(operationSchedules, 0);
	}

	public static OperationSchedule getCurrentOffset(
			List<OperationSchedule> operationSchedules, Integer offset) {
		for (Integer i = 0; i < operationSchedules.size() - offset; i++) {
			if (operationSchedules.get(i).arrivedAt == null
					|| operationSchedules.get(i).departedAt == null) {
				return operationSchedules.get(i + offset);
			}
		}
		return null;
	}

	public static Phase getPhase(List<OperationSchedule> operationSchedules,
			List<PassengerRecord> passengerRecords) {
		OperationSchedule operationSchedule = OperationSchedule
				.getCurrent(operationSchedules);
		if (operationSchedule == null) {
			return Phase.FINISH;
		} else if (operationSchedule.arrivedAt == null
				&& operationSchedule.departedAt == null) {
			return Phase.DRIVE;
		} else if (operationSchedule.completeGetOff
				|| operationSchedule.getGetOffScheduledPassengerRecords(
						passengerRecords).isEmpty()) {
			return Phase.PLATFORM_GET_ON;
		} else {
			return Phase.PLATFORM_GET_OFF;
		}
	}

	public List<PassengerRecord> getNoGetOffErrorPassengerRecords(
			List<PassengerRecord> passengerRecords) {
		List<PassengerRecord> results = Lists.newLinkedList();
		for (PassengerRecord passengerRecord : getGetOffScheduledPassengerRecords(passengerRecords)) {
			if (passengerRecord.getOffTime == null) {
				results.add(passengerRecord);
			}
		}
		return results;
	}

	public List<PassengerRecord> getGetOnScheduledPassengerRecords(
			List<PassengerRecord> passengerRecords) {
		List<PassengerRecord> results = Lists.newLinkedList();
		for (PassengerRecord passengerRecord : passengerRecords) {
			if (id.equals(passengerRecord.departureScheduleId)) {
				results.add(passengerRecord);
			}
		}
		return results;
	}

	public List<PassengerRecord> getNoGetOnErrorPassengerRecords(
			List<PassengerRecord> passengerRecords) {
		List<PassengerRecord> results = Lists.newLinkedList();
		for (PassengerRecord passengerRecord : getGetOnScheduledPassengerRecords(passengerRecords)) {
			if (passengerRecord.getOnTime == null) {
				results.add(passengerRecord);
			}
		}
		return results;
	}

	public List<PassengerRecord> getGetOffScheduledPassengerRecords(
			List<PassengerRecord> passengerRecords) {
		List<PassengerRecord> results = Lists.newLinkedList();
		for (PassengerRecord passengerRecord : passengerRecords) {
			if (id.equals(passengerRecord.arrivalScheduleId)) {
				results.add(passengerRecord);
			}
		}
		return results;
	}

	public static OperationSchedule getById(
			List<OperationSchedule> operationSchedules, Long operationScheduleId) {
		for (OperationSchedule operationSchedule : operationSchedules) {
			if (operationScheduleId.equals(operationSchedule.id)) {
				return operationSchedule;
			}
		}
		return null;
	}

	public static Uri replace(ContentValues values,
			InVehicleDeviceContentProvider contentProvider) {
		SQLiteDatabase database = contentProvider.getDatabase();
		ContentResolver contentResolver = contentProvider.getContext()
				.getContentResolver();
		ScheduledExecutorService executorService = contentProvider
				.getExecutorService();
		database.beginTransaction();
		try {
			Long id = database.replaceOrThrow(OperationSchedules.TABLE_NAME,
					null, values);
			Uri uri = ContentUris.withAppendedId(
					OperationSchedules.CONTENT.URI, id);
			ContentValues operationRecordValues = new ContentValues();
			operationRecordValues.put(OperationRecords.Columns.ARRIVED_AT,
					values.getAsLong(OperationSchedules.Columns.ARRIVED_AT));
			operationRecordValues.put(OperationRecords.Columns.DEPARTED_AT,
					values.getAsLong(OperationSchedules.Columns.DEPARTED_AT));

			// TODO: MAX(local_version)で書き直す
			String where = OperationRecords.Columns.OPERATION_SCHEDULE_ID
					+ " = ?";
			String[] whereArgs = new String[] { id.toString() };
			Long maxVersion = 1L;
			Cursor cursor = database.query(OperationRecords.TABLE_NAME, null,
					where, whereArgs, null, null, null);
			try {
				if (cursor.moveToFirst()) {
					do {
						Long version = cursor
								.getLong(cursor
										.getColumnIndexOrThrow(OperationRecords.Columns.LOCAL_VERSION));
						if (version > maxVersion) {
							maxVersion = version;
						}
					} while (cursor.moveToNext());
				}
			} finally {
				cursor.close();
			}
			operationRecordValues.put(OperationRecords.Columns.LOCAL_VERSION,
					maxVersion + 1);
			database.update(OperationRecords.TABLE_NAME, operationRecordValues,
					where, whereArgs);
			executorService.execute(new PatchOperationRecordTask(
					contentProvider.getContext(), database, executorService));
			contentResolver.notifyChange(OperationSchedules.CONTENT.URI, null);
			contentResolver.notifyChange(uri, null);
			database.setTransactionSuccessful();
			return uri;
		} finally {
			database.endTransaction();
		}
	}

	public static Cursor query(InVehicleDeviceContentProvider contentProvider,
			String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		SQLiteDatabase database = contentProvider.getDatabase();
		ContentResolver contentResolver = contentProvider.getContext()
				.getContentResolver();
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
		sql.append(" order by _id;");
		Cursor cursor = database.rawQuery(sql.toString(), null);
		cursor.setNotificationUri(contentResolver,
				OperationSchedules.CONTENT.URI);
		return cursor;
	}
}
