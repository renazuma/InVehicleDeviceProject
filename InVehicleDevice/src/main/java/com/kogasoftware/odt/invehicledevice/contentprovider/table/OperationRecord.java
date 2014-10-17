package com.kogasoftware.odt.invehicledevice.contentprovider.table;

import android.provider.BaseColumns;

/**
 * 運行実績テーブル
 */
public class OperationRecord {
	public static final int TABLE_CODE = 5;
	public static final String TABLE_NAME = "operation_records";
	public static final Content CONTENT = new Content(TABLE_CODE, TABLE_NAME);

	public static class Columns implements BaseColumns {
		public static final String OPERATION_SCHEDULE_ID = "operation_schedule_id";
		public static final String ARRIVED_AT = "arrived_at";
		public static final String DEPARTED_AT = "departed_at";
		public static final String LOCAL_VERSION = "local_version";
		public static final String SERVER_VERSION = "server_version";
	}
}
