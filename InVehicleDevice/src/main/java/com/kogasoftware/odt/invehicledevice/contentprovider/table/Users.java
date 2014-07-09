package com.kogasoftware.odt.invehicledevice.contentprovider.table;

import android.provider.BaseColumns;

public class Users {
	public static final int TABLE_CODE = 9;
	public static final String TABLE_NAME = "users";
	public static final Content CONTENT = new Content(TABLE_CODE, TABLE_NAME);

	public static class Columns implements BaseColumns {
		public static final String FIRST_NAME = "first_name";
		public static final String LAST_NAME = "last_name";
		public static final String MEMO = "memo";
		public static final String HANDICAPPED = "handicapped";
		public static final String NEEDED_CARE = "needed_care";
		public static final String WHEELCHAIR = "wheelchair";
	}
}
