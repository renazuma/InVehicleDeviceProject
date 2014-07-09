package com.kogasoftware.odt.invehicledevice.contentprovider.table;

import android.provider.BaseColumns;

public class InVehicleDevices {
	public static final int TABLE_CODE = 1;
	public static final String TABLE_NAME = "in_vehicle_devices";
	public static final Content CONTENT = new Content(TABLE_CODE, TABLE_NAME);

	public static class Columns implements BaseColumns {
		public static final String LOGIN = "login";
		public static final String PASSWORD = "password";
		public static final String URL = "url";
		public static final String AUTHENTICATION_TOKEN = "authentication_token";
	}
}
