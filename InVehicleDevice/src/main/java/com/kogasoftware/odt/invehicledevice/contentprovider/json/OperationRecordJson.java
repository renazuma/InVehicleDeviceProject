package com.kogasoftware.odt.invehicledevice.contentprovider.json;

import org.joda.time.DateTime;

import android.content.ContentValues;

import com.kogasoftware.odt.invehicledevice.contentprovider.table.OperationRecords;

public class OperationRecordJson {
	public Long id;
	public DateTime departedAt;
	public DateTime arrivedAt;
	public Long operationScheduleId;
	public ContentValues toContentValues() {
		ContentValues values = new ContentValues();
		values.put(OperationRecords.Columns._ID, id);
		values.put(OperationRecords.Columns.OPERATION_SCHEDULE_ID,
				operationScheduleId);
		if (arrivedAt != null) {
			values.put(OperationRecords.Columns.ARRIVED_AT,
					arrivedAt.getMillis());
		}
		if (departedAt != null) {
			values.put(OperationRecords.Columns.DEPARTED_AT,
					departedAt.getMillis());
		}
		return values;
	}
}
