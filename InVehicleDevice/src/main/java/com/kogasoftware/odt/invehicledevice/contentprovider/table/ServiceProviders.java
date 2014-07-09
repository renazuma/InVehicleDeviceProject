package com.kogasoftware.odt.invehicledevice.contentprovider.table;

import android.provider.BaseColumns;

public class ServiceProviders {
	public static final int TABLE_CODE = 10;
	public static final String TABLE_NAME = "service_providers";
	public static final Content CONTENT = new Content(TABLE_CODE, TABLE_NAME);

	public static class Columns implements BaseColumns {
		public static final String NAME = "name";
		public static final String LOG_ACCESS_KEY_ID_AWS = "log_access_key_id_aws";
		public static final String LOG_SECRET_ACCESS_KEY_AWS = "log_secret_access_key_aws";
	}
}
