package com.kogasoftware.odt.invehicledevice.contentprovider.json;

import java.util.List;

import android.content.ContentValues;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.Reservations;

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
		reservationValues.put(Reservations.Columns._ID, id);
		reservationValues.put(Reservations.Columns.USER_ID, userId);
		reservationValues.put(Reservations.Columns.MEMO,
				Strings.nullToEmpty(memo));
		reservationValues.put(Reservations.Columns.DEPARTURE_SCHEDULE_ID,
				departureScheduleId);
		reservationValues.put(Reservations.Columns.ARRIVAL_SCHEDULE_ID,
				arrivalScheduleId);
		return reservationValues;
	}
}
