package com.kogasoftware.odt.invehicledevice.model.contentprovider.task;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.http.HttpResponse;

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
import com.kogasoftware.odt.invehicledevice.model.contentprovider.json.OperationRecordJson;
import com.kogasoftware.odt.invehicledevice.model.contentprovider.json.OperationScheduleJson;
import com.kogasoftware.odt.invehicledevice.model.contentprovider.json.PassengerRecordJson;
import com.kogasoftware.odt.invehicledevice.model.contentprovider.json.PlatformJson;
import com.kogasoftware.odt.invehicledevice.model.contentprovider.json.ReservationJson;
import com.kogasoftware.odt.invehicledevice.model.contentprovider.json.UserJson;
import com.kogasoftware.odt.invehicledevice.model.contentprovider.table.OperationRecord;
import com.kogasoftware.odt.invehicledevice.model.contentprovider.table.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.model.contentprovider.table.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.model.contentprovider.table.Platform;
import com.kogasoftware.odt.invehicledevice.model.contentprovider.table.Reservation;
import com.kogasoftware.odt.invehicledevice.model.contentprovider.table.User;
import com.kogasoftware.odt.invehicledevice.model.contentprovider.table.VehicleNotification;

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
			ScheduledExecutorService executorService,
			Boolean scheduleVehicleNotificationRequired) {
		super(context, database, executorService);
		this.scheduleVehicleNotificationRequired = scheduleVehicleNotificationRequired;
	}

	void insert(List<OperationScheduleJson> operationSchedules,
			List<Long> scheduleVehidleNotificationIds) {
		List<UserJson> users = Lists.newLinkedList();
		List<ReservationJson> reservations = Lists.newLinkedList();
		List<PassengerRecordJson> passengerRecords = Lists.newLinkedList();
		List<OperationRecordJson> operationRecords = Lists.newLinkedList();
		List<PlatformJson> platforms = Lists.newLinkedList();
		Collections.sort(operationSchedules,
				new Comparator<OperationScheduleJson>() {
					@Override
					public int compare(OperationScheduleJson l,
							OperationScheduleJson r) {
						return ComparisonChain.start()
								.compare(l.arrivalEstimate, r.arrivalEstimate)
								.compare(l.id, r.id).result();
					}
				});

		// 各モデルを配列に展開する
		for (OperationScheduleJson operationSchedule : operationSchedules) {
			OperationRecordJson operationRecord = operationSchedule.operationRecord;
			operationRecords.add(operationRecord);
			platforms.add(operationSchedule.platform);
			ReservationJson reservation = operationSchedule.departureReservation;
			if (reservation != null) {
				reservations.add(reservation);
				for (UserJson user : reservation.fellowUsers) {
					users.add(user);
				}

				for (PassengerRecordJson passengerRecord : reservation.passengerRecords) {
					passengerRecords.add(passengerRecord);
				}
			}
		}

		// 運行スケジュールをマージする
		Map<Long, OperationScheduleJson> toMergedOperationSchedule = new HashMap<Long, OperationScheduleJson>();
		LinkedList<OperationScheduleJson> mergedOperationSchedules = Lists
				.newLinkedList();
		if (!operationSchedules.isEmpty()) {
			Boolean first = true;
			OperationScheduleJson previous = null;
			for (OperationScheduleJson current : operationSchedules) {
				if (first || !current.platform.id.equals(previous.platform.id)) {
					previous = current;
					mergedOperationSchedules.add(current);
					first = false;
				} else {
					if (previous.departureEstimate
							.isBefore(current.departureEstimate)) {
						previous.departureEstimate = current.departureEstimate;
					}
				}
				toMergedOperationSchedule.put(current.id, previous);
			}
		}

		// 予約が参照する運行スケジュールをつなぎかえる
		for (ReservationJson reservation : reservations) {
			reservation.departureScheduleId = toMergedOperationSchedule
					.get(reservation.departureScheduleId).id;
			reservation.arrivalScheduleId = toMergedOperationSchedule
					.get(reservation.arrivalScheduleId).id;
		}

		// 運行実績が参照する運行スケジュールをつなぎかえる
		for (OperationRecordJson operationRecord : operationRecords) {
			operationRecord.operationScheduleId = toMergedOperationSchedule
					.get(operationRecord.operationScheduleId).id;
		}

		try {
			database.beginTransaction();
			for (Long vehicleNotificationId : scheduleVehidleNotificationIds) {
				ContentValues values = new ContentValues();
				values.put(VehicleNotification.Columns.SCHEDULE_DOWNLOADED, 1);
				String where = VehicleNotification.Columns._ID + " = ?";
				String[] whereArgs = new String[]{vehicleNotificationId
						.toString()};
				database.update(VehicleNotification.TABLE_NAME, values, where,
						whereArgs);
			}
			for (String table : new String[]{User.TABLE_NAME,
					Reservation.TABLE_NAME, OperationSchedule.TABLE_NAME,
					OperationRecord.TABLE_NAME, PassengerRecord.TABLE_NAME,
					Platform.TABLE_NAME}) {
				database.delete(table, null, null);
			}
			for (UserJson user : users) {
				database.replaceOrThrow(User.TABLE_NAME, null,
						user.toContentValues());
			}
			for (PassengerRecordJson passengerRecord : passengerRecords) {
				ContentValues values;
				try {
					values = passengerRecord.toContentValues(reservations);
				} catch (IOException e) {
					Log.e(TAG, "Can't create passenger_records values for id="
							+ passengerRecord.id, e);
					continue;
				}
				database.replaceOrThrow(PassengerRecord.TABLE_NAME, null,
						values);
			}
			for (OperationRecordJson operationRecord : operationRecords) {
				database.replaceOrThrow(OperationRecord.TABLE_NAME, null,
						operationRecord.toContentValues());
			}
			for (PlatformJson platform : platforms) {
				database.replaceOrThrow(Platform.TABLE_NAME, null,
						platform.toContentValues());
			}
			for (OperationScheduleJson operationSchedule : mergedOperationSchedules) {
				ContentValues values = operationSchedule.toContentValues();
				// 乗り降り時刻を更新
				Long arrivedAt = null;
				Long departedAt = null;
				for (OperationRecordJson operationRecord : operationRecords) {
					if (!operationSchedule.id
							.equals(operationRecord.operationScheduleId)) {
						continue;
					}
					if (operationRecord.arrivedAt != null) {
						Long nextArrivedAt = operationRecord.arrivedAt
								.getMillis();
						if (arrivedAt == null || arrivedAt > nextArrivedAt) {
							arrivedAt = nextArrivedAt;
						}
					}
					if (operationRecord.departedAt != null) {
						Long nextDepartedAt = operationRecord.departedAt
								.getMillis();
						if (departedAt == null || departedAt < nextDepartedAt) {
							departedAt = nextDepartedAt;
						}
					}
				}
				values.put(OperationSchedule.Columns.ARRIVED_AT, arrivedAt);
				values.put(OperationSchedule.Columns.DEPARTED_AT, departedAt);
				database.replaceOrThrow(OperationSchedule.TABLE_NAME, null,
						values);
			}
			for (ReservationJson reservation : reservations) {
				database.replaceOrThrow(Reservation.TABLE_NAME, null,
						reservation.toContentValues());
			}
			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
		}
		for (Long scheduleVehicleNotificationId : scheduleVehidleNotificationIds) {
			contentResolver.notifyChange(ContentUris.withAppendedId(
					VehicleNotification.CONTENT.URI,
					scheduleVehicleNotificationId), null);
		}
		contentResolver.notifyChange(VehicleNotification.CONTENT.URI, null);
		for (Uri uri : new Uri[]{User.CONTENT.URI, Reservation.CONTENT.URI,
				OperationSchedule.CONTENT.URI, OperationRecord.CONTENT.URI,
				PassengerRecord.CONTENT.URI, Platform.CONTENT.URI}) {
			contentResolver.notifyChange(uri, null);
		}
	}
	@Override
	protected void runSession(URI baseUri, String authenticationToken) {
		if (isDirty()) {
			Log.i(TAG, "dirty, retry");
			submitRetry();
			return;
		}
		final List<Long> oldScheduleVehidleNotificationIds = getScheduleVehidleNotificationIds();
		if (scheduleVehicleNotificationRequired
				&& oldScheduleVehidleNotificationIds.isEmpty()) {
			Log.i(TAG, "required VehicleNotifications not found");
			return;
		}
		doHttpGet(baseUri, "operation_schedules", authenticationToken,
				new Callback() {
					@Override
					public void onSuccess(HttpResponse response, byte[] entity) {
						save(new String(entity, Charsets.UTF_8),
								oldScheduleVehidleNotificationIds);
					}

					@Override
					public void onFailure(HttpResponse response, byte[] entity) {
						if (response.getStatusLine().getStatusCode() % 100 == 5) {
							Log.i(TAG, "status 5xx, retry");
							submitRetry();
						} else {
							Log.e(TAG, "onFailure: " + response.getStatusLine()
									+ " entity=" + dumpEntity(entity));
						}
					}

					@Override
					public void onException(IOException e) {
						submitRetry();
					}
				});
	}

	private List<Long> getScheduleVehidleNotificationIds() {
		List<Long> ids = Lists.newLinkedList();
		String where = VehicleNotification.Columns.NOTIFICATION_KIND + " = "
				+ VehicleNotification.NotificationKind.SCHEDULE + " AND "
				+ VehicleNotification.Columns.SCHEDULE_DOWNLOADED + " = 0 ";
		Cursor cursor = database.query(VehicleNotification.TABLE_NAME, null,
				where, null, null, null, null);
		try {
			if (cursor.moveToFirst()) {
				do {
					ids.add(cursor.getLong(cursor
							.getColumnIndexOrThrow(VehicleNotification.Columns._ID)));
				} while (cursor.moveToNext());
			}
		} finally {
			cursor.close();
		}
		return ids;
	}

	private Boolean isDirty() {
		Cursor orCursor = database.query(OperationRecord.TABLE_NAME, null,
				OperationRecord.Columns.LOCAL_VERSION + " > "
						+ OperationRecord.Columns.SERVER_VERSION, null, null,
				null, null);
		try {
			// TODO:count
			if (orCursor.getCount() > 0) {
				Log.i(TAG, "modified OperationRecord found");
				return true;
			}
		} finally {
			orCursor.close();
		}

		Cursor prCursor = database.query(PassengerRecord.TABLE_NAME, null,
				PassengerRecord.Columns.LOCAL_VERSION + " > "
						+ PassengerRecord.Columns.SERVER_VERSION, null, null,
				null, null);
		try {
			// TODO:count
			if (prCursor.getCount() > 0) {
				Log.i(TAG, "modified PassengerRecord found");
				return true;
			}
		} finally {
			prCursor.close();
		}
		Log.i(TAG, "clean");
		return false;
	}

	public void save(String entity, List<Long> oldScheduleVehidleNotificationIds) {
		if (isDirty()) {
			Log.i(TAG, "dirty, retry");
			submitRetry();
			return;
		}
		List<Long> newScheduleVehidleNotificationIds = getScheduleVehidleNotificationIds();
		if (!oldScheduleVehidleNotificationIds
				.equals(newScheduleVehidleNotificationIds)) {
			Log.i(TAG, "required VehicleNotification doesn't match, retry");
			submitRetry();
			return;
		}
		List<OperationScheduleJson> operationScheduleJsons;
		try {
			operationScheduleJsons = JSON.readValue(entity,
					new TypeReference<List<OperationScheduleJson>>() {
					});
		} catch (IOException e) {
			Log.e(TAG, "IOException while parsing entity: " + entity, e);
			return;
		}
		insert(operationScheduleJsons, oldScheduleVehidleNotificationIds);
	}
}
