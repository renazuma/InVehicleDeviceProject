package com.kogasoftware.odt.invehicledevice.contentprovider.task;

import android.database.Cursor;

import com.kogasoftware.odt.invehicledevice.contentprovider.model.VehicleNotification.NotificationKind;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.VehicleNotifications;
import com.kogasoftware.odt.invehicledevice.contentprovider.task.GetVehicleNotificationsTask;

public class GetVehicleNotificationsTaskTestCase extends
		SynchronizationTaskTestCase {
	@Override
	public void setUp() throws Exception {
		super.setUp();
		signIn();
	}

	public void testRun() {
		server.addVehicleNotification("Hello", "はろー", NotificationKind.NORMAL);
		server.addVehicleNotification("World", "わーるど", NotificationKind.NORMAL);
		GetVehicleNotificationsTask task = new GetVehicleNotificationsTask(
				mContext, database, executorService);
		task.run();
		Cursor cursor = database.query(VehicleNotifications.TABLE_NAME, null,
				null, null, null, null, null);
		try {
			assertTrue(cursor.moveToFirst());
			assertEquals("Hello",
					cursor.getString(cursor.getColumnIndexOrThrow("body")));
			assertTrue(cursor.moveToNext());
			assertEquals("World",
					cursor.getString(cursor.getColumnIndexOrThrow("body")));
			assertFalse(cursor.moveToNext());
		} finally {
			cursor.close();
		}
	}
}
