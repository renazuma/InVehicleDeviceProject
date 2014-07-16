package com.kogasoftware.odt.invehicledevice.contentprovider.json;

import org.joda.time.DateTime;

import android.content.ContentValues;

import com.kogasoftware.odt.invehicledevice.contentprovider.table.PassengerRecords;

public class PassengerRecordJson {
	public Long id;
	public Long reservationId;
	public DateTime getOnTime;
	public DateTime getOffTime;
	public Long userId;

	public ContentValues toContentValues() {
		ContentValues values = new ContentValues();
		values.put(PassengerRecords.Columns._ID, id);
		values.put(PassengerRecords.Columns.RESERVATION_ID, reservationId);
		values.put(PassengerRecords.Columns.USER_ID, userId);
		return values;
	}
}
