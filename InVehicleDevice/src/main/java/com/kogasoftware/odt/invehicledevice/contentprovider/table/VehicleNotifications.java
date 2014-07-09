package com.kogasoftware.odt.invehicledevice.contentprovider.table;

import android.provider.BaseColumns;

public class VehicleNotifications {
	public static final int TABLE_CODE = 2;
	public static final String TABLE_NAME = "vehicle_notifications";
	public static final Content CONTENT = new Content(TABLE_CODE, TABLE_NAME);

	public static class Columns implements BaseColumns {
		public static final String BODY = "body";
		public static final String BODY_RUBY = "body_ruby";
		public static final String NOTIFICATION_KIND = "notification_kind";
		public static final String READ_AT = "read_at";
		public static final String RESPONSE = "response";
		public static final String SCHEDULE_DOWNLOADED = "schedule_downloaded";
	}
}
