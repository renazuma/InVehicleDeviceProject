package com.kogasoftware.odt.invehicledevice.contentprovider.json;

import java.io.IOException;
import java.util.List;

import org.joda.time.DateTime;

import android.content.ContentValues;

import com.kogasoftware.odt.invehicledevice.contentprovider.table.PassengerRecords;
import com.kogasoftware.odt.invehicledevice.utils.ContentValuesUtils;

public class PassengerRecordJson {
	public Long id;
	public Long reservationId;
	public DateTime getOnTime;
	public DateTime getOffTime;
	public Long userId;

	public ContentValues toContentValues(List<ReservationJson> reservations)
			throws IOException {
		for (ReservationJson reservation : reservations) {
			if (reservationId.equals(reservation.id)) {
				return toContentValues(reservation);
			}
		}
		throw new IOException("toContentValues reservation.id=" + reservationId
				+ " not found");
	}

	public ContentValues toContentValues(ReservationJson reservation) {
		ContentValues values = new ContentValues();
		values.put(PassengerRecords.Columns._ID, id);
		values.put(PassengerRecords.Columns.RESERVATION_ID, reservationId);
		values.put(PassengerRecords.Columns.USER_ID, userId);
		ContentValuesUtils.putDateTime(values,
				PassengerRecords.Columns.GET_ON_TIME, getOnTime);
		ContentValuesUtils.putDateTime(values,
				PassengerRecords.Columns.GET_OFF_TIME, getOffTime);
		Boolean representative = userId.equals(reservation.userId);
		values.put(PassengerRecords.Columns.REPRESENTATIVE, representative
				? 1
				: 0);
		Integer passengerCount = 1;
		if (representative && reservation.fellowUsers.size() > 2) {
			passengerCount = reservation.passengerCount
					- reservation.fellowUsers.size() + 1;
		}
		values.put(PassengerRecords.Columns.PASSENGER_COUNT, passengerCount);
		return values;
	}
}
