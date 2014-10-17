package com.kogasoftware.odt.invehicledevice.contentprovider.json;

import java.io.IOException;
import java.util.List;

import org.joda.time.DateTime;

import android.content.ContentValues;

import com.kogasoftware.odt.invehicledevice.contentprovider.table.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.utils.ContentValuesUtils;

/**
 * 乗車実績のJSON
 */
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
		values.put(PassengerRecord.Columns._ID, id);
		values.put(PassengerRecord.Columns.RESERVATION_ID, reservationId);
		values.put(PassengerRecord.Columns.USER_ID, userId);
		ContentValuesUtils.putDateTime(values,
				PassengerRecord.Columns.GET_ON_TIME, getOnTime);
		ContentValuesUtils.putDateTime(values,
				PassengerRecord.Columns.GET_OFF_TIME, getOffTime);
		Boolean representative = userId.equals(reservation.userId);
		values.put(PassengerRecord.Columns.REPRESENTATIVE, representative
				? 1
				: 0);
		Integer passengerCount = 1;
		if (representative && reservation.fellowUsers.size() > 2) {
			passengerCount = reservation.passengerCount
					- reservation.fellowUsers.size() + 1;
		}
		values.put(PassengerRecord.Columns.PASSENGER_COUNT, passengerCount);
		return values;
	}
}
