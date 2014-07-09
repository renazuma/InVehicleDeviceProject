package com.kogasoftware.odt.invehicledevice.contentprovider.task;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.joda.time.DateTimeZone;
import org.json.JSONException;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.amazonaws.org.apache.http.client.utils.URIBuilder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Charsets;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.contentprovider.json.OperationRecordJson;
import com.kogasoftware.odt.invehicledevice.contentprovider.json.OperationScheduleJson;
import com.kogasoftware.odt.invehicledevice.contentprovider.json.PassengerRecordJson;
import com.kogasoftware.odt.invehicledevice.contentprovider.json.PlatformJson;
import com.kogasoftware.odt.invehicledevice.contentprovider.json.ReservationJson;
import com.kogasoftware.odt.invehicledevice.contentprovider.json.UserJson;
import com.kogasoftware.odt.invehicledevice.contentprovider.model.VehicleNotification;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.OperationRecords;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.OperationSchedules;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.PassengerRecords;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.Platforms;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.Reservations;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.Users;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.VehicleNotifications;

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
		List<ContentValues> userValuesList = Lists.newLinkedList();
		List<ContentValues> reservationValuesList = Lists.newLinkedList();
		List<ContentValues> operationScheduleValuesList = Lists.newLinkedList();
		List<ContentValues> passengerRecordValuesList = Lists.newLinkedList();
		List<ContentValues> operationRecordValuesList = Lists.newLinkedList();
		List<ContentValues> platformValuesList = Lists.newLinkedList();

		for (OperationScheduleJson operationSchedule : operationSchedules) {
			ContentValues operationScheduleValues = new ContentValues();
			operationScheduleValues.put(OperationSchedules.Columns._ID,
					operationSchedule.id);

			if (operationSchedule.arrivalEstimate != null) {
				operationScheduleValues.put(
						OperationSchedules.Columns.ARRIVAL_ESTIMATE,
						operationSchedule.arrivalEstimate.getMillis());
			}

			if (operationSchedule.departureEstimate != null) {
				operationScheduleValues.put(
						OperationSchedules.Columns.DEPARTURE_ESTIMATE,
						operationSchedule.departureEstimate.getMillis());
			}

			ContentValues operationRecordValues = new ContentValues();
			OperationRecordJson operationRecord = operationSchedule.operationRecord;
			operationRecordValues.put(OperationRecords.Columns._ID,
					operationRecord.id);
			operationRecordValues.put(
					OperationRecords.Columns.OPERATION_SCHEDULE_ID,
					operationRecord.operationScheduleId);
			if (operationRecord.arrivedAt != null) {
				operationScheduleValues.put(
						OperationRecords.Columns.ARRIVED_AT,
						operationRecord.arrivedAt.getMillis());
			}
			if (operationRecord.departedAt != null) {
				operationScheduleValues.put(
						OperationRecords.Columns.DEPARTED_AT,
						operationRecord.departedAt.getMillis());
			}
			operationRecordValuesList.add(operationRecordValues);

			operationScheduleValues.put(OperationSchedules.Columns.PLATFORM_ID,
					operationSchedule.platform.id);
			operationScheduleValues.put(
					OperationSchedules.Columns.OPERATION_DATE,
					operationSchedule.operationDate.toDateTimeAtStartOfDay(
							DateTimeZone.UTC).getMillis());
			operationScheduleValuesList.add(operationScheduleValues);

			PlatformJson platform = operationSchedule.platform;
			ContentValues platformValues = new ContentValues();
			platformValues.put(Platforms.Columns._ID, platform.id);
			platformValues.put(Platforms.Columns.NAME, platform.name);
			platformValues.put(Platforms.Columns.NAME_RUBY,
					Strings.nullToEmpty(platform.nameRuby));
			platformValues.put(Platforms.Columns.ADDRESS,
					Strings.nullToEmpty(platform.address));
			platformValues.put(Platforms.Columns.MEMO,
					Strings.nullToEmpty(platform.memo));
			platformValues.put(Platforms.Columns.LATITUDE,
					platform.latitude.toPlainString());
			platformValues.put(Platforms.Columns.LONGITUDE,
					platform.longitude.toPlainString());
			platformValuesList.add(platformValues);

			ReservationJson reservation = operationSchedule.departureReservation;
			if (reservation != null) {
				ContentValues reservationValues = new ContentValues();
				reservationValues.put(Reservations.Columns._ID, reservation.id);
				reservationValues.put(Reservations.Columns.USER_ID,
						reservation.userId);
				reservationValues.put(Reservations.Columns.MEMO,
						Strings.nullToEmpty(reservation.memo));
				reservationValues.put(Reservations.Columns.PASSENGER_COUNT,
						Objects.firstNonNull(reservation.passengerCount, 1));
				reservationValues.put(
						Reservations.Columns.DEPARTURE_SCHEDULE_ID,
						reservation.departureScheduleId);
				reservationValues.put(Reservations.Columns.ARRIVAL_SCHEDULE_ID,
						reservation.arrivalScheduleId);
				reservationValuesList.add(reservationValues);

				for (UserJson user : reservation.fellowUsers) {
					ContentValues userValues = new ContentValues();
					userValues.put(Users.Columns._ID, user.id);
					userValues.put(Users.Columns.FIRST_NAME, user.firstName);
					userValues.put(Users.Columns.LAST_NAME, user.lastName);
					userValues.put(Users.Columns.MEMO,
							Strings.nullToEmpty(user.memo));
					userValues.put(Users.Columns.HANDICAPPED, user.handicapped);
					userValues.put(Users.Columns.NEEDED_CARE, user.neededCare);
					userValues.put(Users.Columns.WHEELCHAIR, user.wheelchair);
					userValuesList.add(userValues);
				}

				for (PassengerRecordJson passengerRecord : reservation.passengerRecords) {
					ContentValues passengerRecordValues = new ContentValues();
					passengerRecordValues.put(PassengerRecords.Columns._ID,
							passengerRecord.id);
					passengerRecordValues.put(
							PassengerRecords.Columns.RESERVATION_ID,
							passengerRecord.reservationId);
					passengerRecordValues.put(PassengerRecords.Columns.USER_ID,
							passengerRecord.userId);
					passengerRecordValuesList.add(passengerRecordValues);
				}
			}
		}

		Map<Long, Long> toMergedId = new HashMap<Long, Long>();
		Map<Long, ContentValues> toMergedOperationSchedule = new HashMap<Long, ContentValues>();
		LinkedList<ContentValues> mergedOperationScheduleValuesList = Lists
				.newLinkedList();

		if (!operationScheduleValuesList.isEmpty()) {
			Boolean first = true;
			ContentValues lastValues = null;
			Long lastId = null;
			Long lastPlatformId = null;
			Long lastDepartureEstimate = null;
			Long lastArrivalEstimate = null;
			for (ContentValues values : operationScheduleValuesList) {
				Long id = values.getAsLong(OperationSchedules.Columns._ID);
				Long platformId = values
						.getAsLong(OperationSchedules.Columns.PLATFORM_ID);
				Long arrivalEstimate = values
						.getAsLong(OperationSchedules.Columns.ARRIVAL_ESTIMATE);
				Long departureEstimate = values
						.getAsLong(OperationSchedules.Columns.DEPARTURE_ESTIMATE);
				if (first || !platformId.equals(lastPlatformId)) {
					lastValues = values;
					lastId = id;
					lastPlatformId = platformId;
					lastArrivalEstimate = arrivalEstimate;
					lastDepartureEstimate = departureEstimate;
					mergedOperationScheduleValuesList.add(values);
					first = false;
				} else {
					if (lastArrivalEstimate > arrivalEstimate) {
						lastArrivalEstimate = arrivalEstimate;
						lastValues.put(
								OperationSchedules.Columns.ARRIVAL_ESTIMATE,
								arrivalEstimate);
					}
					if (lastDepartureEstimate < departureEstimate) {
						lastDepartureEstimate = departureEstimate;
						lastValues.put(
								OperationSchedules.Columns.DEPARTURE_ESTIMATE,
								departureEstimate);
					}
				}
				toMergedId.put(id, lastId);
				toMergedOperationSchedule.put(id, lastValues);
			}
		}

		for (ContentValues values : reservationValuesList) {
			Long departureId = values
					.getAsLong(Reservations.Columns.DEPARTURE_SCHEDULE_ID);
			values.put(Reservations.Columns.DEPARTURE_SCHEDULE_ID,
					toMergedId.get(departureId));
			Long arrivalId = values
					.getAsLong(Reservations.Columns.ARRIVAL_SCHEDULE_ID);
			values.put(Reservations.Columns.ARRIVAL_SCHEDULE_ID,
					toMergedId.get(arrivalId));
		}

		for (ContentValues values : operationRecordValuesList) {
			Long operationScheduleId = values
					.getAsLong(OperationRecords.Columns.OPERATION_SCHEDULE_ID);
			values.put(OperationRecords.Columns.OPERATION_SCHEDULE_ID,
					toMergedId.get(operationScheduleId));
			Long arrivedAt = values
					.getAsLong(OperationRecords.Columns.ARRIVED_AT);
			Long departedAt = values
					.getAsLong(OperationRecords.Columns.DEPARTED_AT);
			ContentValues osValues = toMergedOperationSchedule
					.get(operationScheduleId);
			if (arrivedAt == null) {
				osValues.putNull(OperationSchedules.Columns.ARRIVED_AT);
			} else if (!osValues
					.containsKey(OperationSchedules.Columns.ARRIVED_AT)) {
				osValues.put(OperationSchedules.Columns.ARRIVED_AT, arrivedAt);
			}
			if (departedAt == null) {
				osValues.putNull(OperationSchedules.Columns.DEPARTED_AT);
			} else if (!osValues
					.containsKey(OperationSchedules.Columns.DEPARTED_AT)) {
				osValues.put(OperationSchedules.Columns.DEPARTED_AT, departedAt);
			}
		}

		try {
			database.beginTransaction();
			for (Long vehicleNotificationId : scheduleVehidleNotificationIds) {
				ContentValues values = new ContentValues();
				values.put(VehicleNotifications.Columns.SCHEDULE_DOWNLOADED, 1);
				String where = VehicleNotifications.Columns._ID + " = ?";
				String[] whereArgs = new String[]{vehicleNotificationId
						.toString()};
				database.update(VehicleNotifications.TABLE_NAME, values, where,
						whereArgs);
			}
			for (String table : new String[]{Users.TABLE_NAME,
					Reservations.TABLE_NAME, OperationSchedules.TABLE_NAME,
					OperationRecords.TABLE_NAME, PassengerRecords.TABLE_NAME,
					Platforms.TABLE_NAME}) {
				database.delete(table, null, null);
			}
			for (ContentValues values : userValuesList) {
				database.replaceOrThrow(Users.TABLE_NAME, null, values);
			}
			for (ContentValues values : passengerRecordValuesList) {
				database.replaceOrThrow(PassengerRecords.TABLE_NAME, null,
						values);
			}
			for (ContentValues values : operationRecordValuesList) {
				database.replaceOrThrow(OperationRecords.TABLE_NAME, null,
						values);
			}
			for (ContentValues values : platformValuesList) {
				database.replaceOrThrow(Platforms.TABLE_NAME, null, values);
			}
			for (ContentValues values : mergedOperationScheduleValuesList) {
				database.replaceOrThrow(OperationSchedules.TABLE_NAME, null,
						values);
			}
			for (ContentValues values : reservationValuesList) {
				database.replaceOrThrow(Reservations.TABLE_NAME, null, values);
			}
			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
		}
		for (Long scheduleVehicleNotificationId : scheduleVehidleNotificationIds) {
			contentResolver.notifyChange(ContentUris.withAppendedId(
					VehicleNotifications.CONTENT.URI,
					scheduleVehicleNotificationId), null);
		}
		contentResolver.notifyChange(VehicleNotifications.CONTENT.URI, null);
		Cursor c = database.query(VehicleNotifications.TABLE_NAME, null, null,
				null, null, null, null);
		try {
			DatabaseUtils.dumpCursor(c);
		} finally {
			c.close();
		}
		for (Uri uri : new Uri[]{Users.CONTENT.URI, Reservations.CONTENT.URI,
				OperationSchedules.CONTENT.URI, OperationRecords.CONTENT.URI,
				PassengerRecords.CONTENT.URI, Platforms.CONTENT.URI}) {
			contentResolver.notifyChange(uri, null);
		}
	}

	@Override
	protected void runSession(URI baseUri, String authenticaitonToken)
			throws IOException, JSONException, URISyntaxException {
		if (isDirty()) {
			submitRetry();
			return;
		}
		List<Long> oldScheduleVehidleNotificationIds = getScheduleVehidleNotificationIds();
		if (scheduleVehicleNotificationRequired
				&& oldScheduleVehidleNotificationIds.isEmpty()) {
			return;
		}
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet();
		request.addHeader("Content-Type", "application/json");
		request.addHeader("Accept", "application/json");
		URIBuilder uriBuilder = new URIBuilder(baseUri);
		uriBuilder.setPath("/in_vehicle_devices/operation_schedules");
		uriBuilder.addParameter(AUTHENTICATION_TOKEN_KEY, authenticaitonToken);
		request.setURI(uriBuilder.build());
		HttpResponse response;
		Boolean networkError = true;
		try {
			response = client.execute(request);
			networkError = false;
		} finally {
			if (networkError) {
				submitRetry();
			}
		}
		int statusCode = response.getStatusLine().getStatusCode();
		byte[] responseEntity = new byte[]{};
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			responseEntity = EntityUtils.toByteArray(entity);
		}
		if (statusCode / 100 == 4 || statusCode / 100 == 5) {
			throw new IOException("code=" + statusCode);
		}
		if (isDirty()) {
			submitRetry();
			return;
		}
		List<Long> newScheduleVehidleNotificationIds = getScheduleVehidleNotificationIds();
		if (!oldScheduleVehidleNotificationIds
				.equals(newScheduleVehidleNotificationIds)) {
			submitRetry();
			return;
		}
		List<OperationScheduleJson> operationScheduleJsons = JSON.readValue(
				new String(responseEntity, Charsets.UTF_8),
				new TypeReference<List<OperationScheduleJson>>() {
				});
		insert(operationScheduleJsons, oldScheduleVehidleNotificationIds);
	}

	private List<Long> getScheduleVehidleNotificationIds() {
		List<Long> ids = Lists.newLinkedList();
		String where = VehicleNotifications.Columns.NOTIFICATION_KIND + " = "
				+ VehicleNotification.NotificationKind.SCHEDULE + " AND "
				+ VehicleNotifications.Columns.SCHEDULE_DOWNLOADED + " = 0 ";
		Cursor cursor = database.query(VehicleNotifications.TABLE_NAME, null,
				where, null, null, null, null);
		try {
			if (cursor.moveToFirst()) {
				do {
					ids.add(cursor.getLong(cursor
							.getColumnIndexOrThrow(VehicleNotifications.Columns._ID)));
				} while (cursor.moveToNext());
			}
		} finally {
			cursor.close();
		}
		return ids;
	}

	private Boolean isDirty() {
		Cursor orCursor = database.query(OperationRecords.TABLE_NAME, null,
				OperationRecords.Columns.LOCAL_VERSION + " > "
						+ OperationRecords.Columns.SERVER_VERSION, null, null,
				null, null);
		try {
			// TODO:count
			if (orCursor.getCount() > 0) {
				return true;
			}
		} finally {
			orCursor.close();
		}

		Cursor prCursor = database.query(PassengerRecords.TABLE_NAME, null,
				PassengerRecords.Columns.LOCAL_VERSION + " > "
						+ PassengerRecords.Columns.SERVER_VERSION, null, null,
				null, null);
		try {
			// TODO:count
			if (prCursor.getCount() > 0) {
				return true;
			}
		} finally {
			prCursor.close();
		}
		return false;
	}
}
