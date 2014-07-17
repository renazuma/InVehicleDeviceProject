package com.kogasoftware.odt.invehicledevice.contentprovider.json;

import org.joda.time.DateTime;

import android.content.ContentValues;

import com.kogasoftware.odt.invehicledevice.contentprovider.table.VehicleNotifications;

public class VehicleNotificationJson {
	public Long id;
	public String body;
	public String bodyRuby;
	public Long notificationKind;
	public Long response;
	public DateTime readAt;
	public ContentValues toContentValues() {
		ContentValues values = new ContentValues();
		values.put(VehicleNotifications.Columns._ID, id);
		values.put(VehicleNotifications.Columns.BODY, body);
		if (bodyRuby != null) {
			values.put(VehicleNotifications.Columns.BODY_RUBY, bodyRuby);
		}
		values.put(VehicleNotifications.Columns.NOTIFICATION_KIND,
				notificationKind);
		if (response != null) {
			values.put(VehicleNotifications.Columns.RESPONSE, response);
		}
		if (readAt != null) {
			values.put(VehicleNotifications.Columns.READ_AT, readAt.getMillis());
		}
		return values;
	}
}
