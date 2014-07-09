package com.kogasoftware.odt.invehicledevice.contentprovider.task;

import org.joda.time.DateTime;

import android.content.ContentValues;
import android.database.Cursor;

import com.kogasoftware.odt.invehicledevice.contentprovider.json.VehicleNotificationJson;
import com.kogasoftware.odt.invehicledevice.contentprovider.model.VehicleNotification.NotificationKind;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.VehicleNotifications;
import com.kogasoftware.odt.invehicledevice.contentprovider.task.PatchVehicleNotificationTask;

public class PatchVehicleNotificationTaskTestCase extends
		SynchronizationTaskTestCase {
	@Override
	public void setUp() throws Exception {
		super.setUp();
		signIn();
	}

	public void testRun() {
		VehicleNotificationJson vn1 = server.addVehicleNotification("Hello",
				"はろー", NotificationKind.NORMAL);
		assertNull(vn1.response);
		assertNull(vn1.readAt);
		VehicleNotificationJson vn2 = server.addVehicleNotification("World",
				"わーるど", NotificationKind.NORMAL);
		assertNull(vn2.response);
		assertNull(vn2.readAt);
		DateTime now = DateTime.now();
		ContentValues values1 = new ContentValues();
		values1.put(VehicleNotifications.Columns._ID, vn1.id);
		values1.put(VehicleNotifications.Columns.BODY, vn1.body);
		values1.put(VehicleNotifications.Columns.BODY_RUBY, vn1.bodyRuby);
		values1.put(VehicleNotifications.Columns.NOTIFICATION_KIND,
				vn1.notificationKind);
		values1.put(VehicleNotifications.Columns.RESPONSE, 1);
		values1.put(VehicleNotifications.Columns.READ_AT, now.getMillis());
		database.insertOrThrow(VehicleNotifications.TABLE_NAME, null, values1);

		ContentValues values2 = new ContentValues();
		values2.put(VehicleNotifications.Columns._ID, vn2.id);
		values2.put(VehicleNotifications.Columns.BODY, vn2.body);
		values2.put(VehicleNotifications.Columns.BODY_RUBY, vn2.bodyRuby);
		values2.put(VehicleNotifications.Columns.NOTIFICATION_KIND,
				vn2.notificationKind);
		database.insertOrThrow(VehicleNotifications.TABLE_NAME, null, values2);

		Runnable task = new PatchVehicleNotificationTask(mContext, database,
				executorService);
		task.run();

		assertEquals(Long.valueOf(1), vn1.response);
		assertEquals(now, vn1.readAt);
		assertNull(vn2.response);
		assertNull(vn2.readAt);

		Cursor c = database.query(VehicleNotifications.TABLE_NAME, null, null,
				null, null, null, null);
		try {
			c.moveToFirst();
			assertEquals(1, c.getCount());
			assertEquals(vn2.id.longValue(), c.getLong(c
					.getColumnIndexOrThrow(VehicleNotifications.Columns._ID)));
		} finally {
			c.close();
		}
	}
}
