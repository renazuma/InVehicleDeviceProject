package com.kogasoftware.odt.invehicledevice.contentprovider.table;

import android.provider.BaseColumns;

public class OperationSchedules {
	public static final int TABLE_CODE = 3;
	public static final String TABLE_NAME = "operation_schedules";
	public static final Content CONTENT = new Content(TABLE_CODE, TABLE_NAME);

	public static class Columns implements BaseColumns {
		public static final String ARRIVAL_ESTIMATE = "arrival_estimate";
		public static final String DEPARTURE_ESTIMATE = "departure_estimate";
		public static final String PLATFORM_ID = "platform_id";
		public static final String ARRIVED_AT = "arrived_at";
		public static final String DEPARTED_AT = "departed_at";
		public static final String COMPLETE_GET_OFF = "complete_get_off";
		public static final String OPERATION_DATE = "operation_date";
	}
}
