package com.kogasoftware.odt.invehicledevice.contentprovider.json;

import org.joda.time.DateTime;

import android.content.ContentValues;

import com.kogasoftware.odt.invehicledevice.contentprovider.table.OperationRecord;

public class OperationRecordJson {
	public Long id;
	public DateTime departedAt;
	public DateTime arrivedAt;
	public Long operationScheduleId;
	public ContentValues toContentValues() {
		ContentValues values = new ContentValues();
		values.put(OperationRecord.Columns._ID, id);
		values.put(OperationRecord.Columns.OPERATION_SCHEDULE_ID,
				operationScheduleId);
		if (arrivedAt != null) {
			values.put(OperationRecord.Columns.ARRIVED_AT,
					arrivedAt.getMillis());
		}
		if (departedAt != null) {
			values.put(OperationRecord.Columns.DEPARTED_AT,
					departedAt.getMillis());
		}
		return values;
	}
}
