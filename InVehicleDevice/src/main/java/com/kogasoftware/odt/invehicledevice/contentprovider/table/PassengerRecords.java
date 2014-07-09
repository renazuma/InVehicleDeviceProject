package com.kogasoftware.odt.invehicledevice.contentprovider.table;

import android.provider.BaseColumns;

public class PassengerRecords {
	public static final int TABLE_CODE = 4;
	public static final String TABLE_NAME = "passenger_records";
	public static final Content CONTENT = new Content(TABLE_CODE, TABLE_NAME);

	public static class Columns implements BaseColumns {
		public static final String GET_ON_TIME = "get_on_time";
		public static final String GET_OFF_TIME = "get_off_time";
		public static final String PASSENGER_COUNT = "passenger_count";
		public static final String RESERVATION_ID = "reservation_id";
		public static final String USER_ID = "user_id";
		public static final String LOCAL_VERSION = "local_version";
		public static final String SERVER_VERSION = "server_version";
		public static final String IGNORE_GET_ON_MISS = "ignore_get_on_miss";
		public static final String IGNORE_GET_OFF_MISS = "ignore_get_off_miss";
	}
}
