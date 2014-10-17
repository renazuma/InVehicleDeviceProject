package com.kogasoftware.odt.invehicledevice.contentprovider.table;

import android.provider.BaseColumns;

/**
 * 乗降場テーブル
 */
public class Platform {
	public static final int TABLE_CODE = 8;
	public static final String TABLE_NAME = "platforms";
	public static final Content CONTENT = new Content(TABLE_CODE, TABLE_NAME);

	public static class Columns implements BaseColumns {
		public static final String ADDRESS = "address";
		public static final String MEMO = "memo";
		public static final String NAME = "name";
		public static final String NAME_RUBY = "name_ruby";
		public static final String LATITUDE = "latitude";
		public static final String LONGITUDE = "longitude";
	}
}
