package com.kogasoftware.odt.invehicledevice.infra.contentprovider.json;

import java.util.List;

import android.content.ContentValues;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.Reservation;

/**
 * 予約のJSON
 */
public class ReservationJson {
	public Long id;
	public Long userId;
	public Long arrivalScheduleId;
	public Long departureScheduleId;
	public List<UserJson> fellowUsers = Lists.newLinkedList();
	public List<PassengerRecordJson> passengerRecords = Lists.newLinkedList();
	public String memo;
	public Integer passengerCount;
	public ContentValues toContentValues() {
		ContentValues reservationValues = new ContentValues();
		reservationValues.put(Reservation.Columns._ID, id);
		reservationValues.put(Reservation.Columns.USER_ID, userId);
		reservationValues.put(Reservation.Columns.MEMO,
				Strings.nullToEmpty(memo));
		reservationValues.put(Reservation.Columns.DEPARTURE_SCHEDULE_ID,
				departureScheduleId);
		reservationValues.put(Reservation.Columns.ARRIVAL_SCHEDULE_ID,
				arrivalScheduleId);
		return reservationValues;
	}
}
