package com.kogasoftware.odt.invehicledevice.contentprovider.json;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;

import android.content.ContentValues;

import com.kogasoftware.odt.invehicledevice.contentprovider.table.OperationSchedule;

/**
 * 運行スケジュールのJSON
 */
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
		operationScheduleValues.put(OperationSchedule.Columns._ID, id);

		if (arrivalEstimate != null) {
			operationScheduleValues.put(
					OperationSchedule.Columns.ARRIVAL_ESTIMATE,
					arrivalEstimate.getMillis());
		}

		if (departureEstimate != null) {
			operationScheduleValues.put(
					OperationSchedule.Columns.DEPARTURE_ESTIMATE,
					departureEstimate.getMillis());
		}
		operationScheduleValues.put(OperationSchedule.Columns.PLATFORM_ID,
				platform.id);
		operationScheduleValues.put(OperationSchedule.Columns.OPERATION_DATE,
				operationDate.toDateTimeAtStartOfDay(DateTimeZone.UTC)
						.getMillis());
		return operationScheduleValues;
	}
}
