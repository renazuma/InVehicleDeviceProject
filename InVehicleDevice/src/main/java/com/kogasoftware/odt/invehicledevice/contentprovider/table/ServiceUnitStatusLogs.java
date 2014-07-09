package com.kogasoftware.odt.invehicledevice.contentprovider.table;

import android.provider.BaseColumns;

public class ServiceUnitStatusLogs {
	public static final int TABLE_CODE = 7;
	public static final String TABLE_NAME = "service_unit_status_logs";
	public static final Content CONTENT = new Content(TABLE_CODE, TABLE_NAME);

	public static class Columns implements BaseColumns {
		public static final String LATITUDE = "latitude";
		public static final String LONGITUDE = "longitude";
		public static final String ORIENTATION = "orientation";
		public static final String TEMPERATURE = "temperature";
		public static final String SIGNAL_STRENGTH = "signal_strength";
		public static final String CREATED_AT = "created_at";
	}
}
