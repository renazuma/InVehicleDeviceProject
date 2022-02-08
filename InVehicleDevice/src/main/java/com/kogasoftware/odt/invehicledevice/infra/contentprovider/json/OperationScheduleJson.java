package com.kogasoftware.odt.invehicledevice.infra.contentprovider.json;

import android.content.ContentValues;

import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.OperationSchedule;

import org.joda.time.DateTime;

/**
 * 運行スケジュールのJSON（OperationScheduleという名前だが、実態はスケジュールおよび関連全データのセット）
 */
public class OperationScheduleJson {
    public Long id;
    public PlatformJson platform;
    public ReservationJson departureReservation;
    public ReservationJson arrivalReservation;
    public DateTime departureEstimate;
    public DateTime arrivalEstimate;
    public OperationRecordJson operationRecord;

    public ContentValues toContentValues() {
        ContentValues operationScheduleValues = new ContentValues();
        operationScheduleValues.put(OperationSchedule.Columns._ID, id);

        if (arrivalEstimate != null) {
            operationScheduleValues.put(
                    OperationSchedule.Columns.ARRIVAL_ESTIMATE, arrivalEstimate.getMillis());
        }

        if (departureEstimate != null) {
            operationScheduleValues.put(
                    OperationSchedule.Columns.DEPARTURE_ESTIMATE, departureEstimate.getMillis());
        }

        operationScheduleValues.put(OperationSchedule.Columns.PLATFORM_ID, platform.id);

        return operationScheduleValues;
    }
}
