package com.kogasoftware.odt.invehicledevice.contentprovider.task;

import java.util.List;

import org.joda.time.DateTime;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.contentprovider.json.PassengerRecordJson;
import com.kogasoftware.odt.invehicledevice.contentprovider.json.UserJson;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.PassengerRecord;

public class PatchPassengerRecordTaskTestCase
		extends
			SynchronizationTaskTestCase {
	@Override
	public void setUp() throws Exception {
		super.setUp();
		signIn();
	}

	public void testRun() {
		List<UserJson> users = Lists.newArrayList(server.addUser("マイクロ 太郎"),
				server.addUser("マイクロ 次郎"));
		server.addOperationSchedule(server.addPlatform("御徒町"),
				server.addPlatform("秋葉原"), users, "10:00:00", "10:00:02", 20);
		PassengerRecordJson pr1 = server.passengerRecords.get(0);
		assertNull(pr1.getOnTime);
		assertNull(pr1.getOffTime);

		PassengerRecordJson pr2 = server.passengerRecords.get(1);
		assertNull(pr2.getOnTime);
		assertNull(pr2.getOffTime);

		DateTime now = DateTime.now();

		ContentValues values1 = new ContentValues();
		values1.put(PassengerRecord.Columns._ID, pr1.id);
		values1.put(PassengerRecord.Columns.GET_ON_TIME, now.getMillis());
		values1.put(PassengerRecord.Columns.GET_OFF_TIME, now.getMillis());
		values1.put(PassengerRecord.Columns.RESERVATION_ID, pr1.reservationId);
		values1.put(PassengerRecord.Columns.USER_ID, pr1.userId);
		values1.put(PassengerRecord.Columns.PASSENGER_COUNT, 1);
		values1.put(PassengerRecord.Columns.SERVER_VERSION, 5);
		values1.put(PassengerRecord.Columns.LOCAL_VERSION, 8);
		database.insertOrThrow(PassengerRecord.TABLE_NAME, null, values1);

		ContentValues values2 = new ContentValues();
		values2.put(PassengerRecord.Columns._ID, pr2.id);
		values2.put(PassengerRecord.Columns.GET_ON_TIME, now.getMillis());
		values2.put(PassengerRecord.Columns.GET_OFF_TIME, now.getMillis());
		values2.put(PassengerRecord.Columns.RESERVATION_ID, pr2.reservationId);
		values2.put(PassengerRecord.Columns.USER_ID, pr2.userId);
		values2.put(PassengerRecord.Columns.PASSENGER_COUNT, 1);
		values2.put(PassengerRecord.Columns.SERVER_VERSION, 2);
		values2.put(PassengerRecord.Columns.LOCAL_VERSION, 2);
		database.insertOrThrow(PassengerRecord.TABLE_NAME, null, values2);

		Runnable task = new PatchPassengerRecordTask(mContext, database,
				executorService);
		task.run();

		assertEquals(now, pr1.getOnTime);
		assertEquals(now, pr1.getOffTime);
		assertNull(pr2.getOnTime);
		assertNull(pr2.getOffTime);

		Cursor c = database.query(PassengerRecord.TABLE_NAME, null, null,
				null, null, null, PassengerRecord.Columns._ID, null);
		try {
			assertTrue(c.moveToFirst());
			assertEquals(
					8,
					c.getLong(c
							.getColumnIndexOrThrow(PassengerRecord.Columns.SERVER_VERSION)));
			assertTrue(c.moveToNext());
			assertEquals(
					2,
					c.getLong(c
							.getColumnIndexOrThrow(PassengerRecord.Columns.SERVER_VERSION)));
		} finally {
			c.close();
		}
	}
}
