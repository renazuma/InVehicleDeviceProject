package com.kogasoftware.odt.invehicledevice.contentprovider.json;

import org.joda.time.DateTime;

import android.content.ContentValues;

import com.kogasoftware.odt.invehicledevice.contentprovider.table.VehicleNotification;

public class VehicleNotificationJson {
	public Long id;
	public String body;
	public String bodyRuby;
	public Long notificationKind;
	public Long response;
	public DateTime readAt;
	public ContentValues toContentValues() {
		ContentValues values = new ContentValues();
		values.put(VehicleNotification.Columns._ID, id);
		values.put(VehicleNotification.Columns.BODY, body);
		if (bodyRuby != null) {
			values.put(VehicleNotification.Columns.BODY_RUBY, bodyRuby);
		}
		values.put(VehicleNotification.Columns.NOTIFICATION_KIND,
				notificationKind);
		if (response != null) {
			values.put(VehicleNotification.Columns.RESPONSE, response);
		}
		if (readAt != null) {
			values.put(VehicleNotification.Columns.READ_AT, readAt.getMillis());
		}
		return values;
	}
}
