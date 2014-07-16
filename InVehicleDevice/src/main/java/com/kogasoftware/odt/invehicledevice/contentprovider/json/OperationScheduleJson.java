package com.kogasoftware.odt.invehicledevice.contentprovider.json;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;

import android.content.ContentValues;

import com.kogasoftware.odt.invehicledevice.contentprovider.table.OperationSchedules;

public class OperationScheduleJson {
	public Long id;
	public PlatformJson platform;
	public ReservationJson departureReservation;
	public ReservationJson arrivalReservation;
	public DateTime departureEstimate;
	public DateTime arrivalEstimate;
	public OperationRecordJson operationRecord;
	public LocalDate operationDate;

	public ContentValues toContentValues() {
		ContentValues operationScheduleValues = new ContentValues();
		operationScheduleValues.put(OperationSchedules.Columns._ID, id);

		if (arrivalEstimate != null) {
			operationScheduleValues.put(
					OperationSchedules.Columns.ARRIVAL_ESTIMATE,
					arrivalEstimate.getMillis());
		}

		if (departureEstimate != null) {
			operationScheduleValues.put(
					OperationSchedules.Columns.DEPARTURE_ESTIMATE,
					departureEstimate.getMillis());
		}
		operationScheduleValues.put(OperationSchedules.Columns.PLATFORM_ID,
				platform.id);
		operationScheduleValues.put(OperationSchedules.Columns.OPERATION_DATE,
				operationDate.toDateTimeAtStartOfDay(DateTimeZone.UTC)
						.getMillis());
		return operationScheduleValues;
	}
}
