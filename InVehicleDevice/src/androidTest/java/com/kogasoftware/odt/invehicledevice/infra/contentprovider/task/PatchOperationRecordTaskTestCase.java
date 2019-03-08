package com.kogasoftware.odt.invehicledevice.infra.contentprovider.task;

import java.util.List;

import org.joda.time.DateTime;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.json.OperationRecordJson;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.json.UserJson;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.OperationRecord;

public class PatchOperationRecordTaskTestCase
		extends
			SynchronizationTaskTestCase {
	@Override
	public void setUp() throws Exception {
		super.setUp();
		signIn();
	}

	public void testRun() {
		List<UserJson> users = Lists.newArrayList(server.addUser("マイクロ 太郎"));
		server.addOperationSchedule(server.addPlatform("御徒町"),
				server.addPlatform("秋葉原"), users, "10:00:00", "10:00:02", 20);
		OperationRecordJson or1 = server.operationRecords.get(0);
		assertNull(or1.arrivedAt);
		assertNull(or1.departedAt);

		OperationRecordJson or2 = server.operationRecords.get(1);
		assertNull(or2.arrivedAt);
		assertNull(or2.departedAt);

		DateTime now = DateTime.now();

		ContentValues values1 = new ContentValues();
		values1.put(OperationRecord.Columns._ID, or1.id);
		values1.put(OperationRecord.Columns.OPERATION_SCHEDULE_ID,
				or1.operationScheduleId);
		values1.put(OperationRecord.Columns.ARRIVED_AT, now.getMillis());
		values1.put(OperationRecord.Columns.DEPARTED_AT, now.getMillis());
		values1.put(OperationRecord.Columns.SERVER_VERSION, 3);
		values1.put(OperationRecord.Columns.LOCAL_VERSION, 3);
		database.insertOrThrow(OperationRecord.TABLE_NAME, null, values1);

		ContentValues values2 = new ContentValues();
		values2.put(OperationRecord.Columns._ID, or2.id);
		values2.put(OperationRecord.Columns.OPERATION_SCHEDULE_ID,
				or2.operationScheduleId);
		values2.put(OperationRecord.Columns.ARRIVED_AT, now.getMillis());
		values2.put(OperationRecord.Columns.DEPARTED_AT, now.getMillis());
		values2.put(OperationRecord.Columns.SERVER_VERSION, 1);
		values2.put(OperationRecord.Columns.LOCAL_VERSION, 2);
		database.insertOrThrow(OperationRecord.TABLE_NAME, null, values2);

		Runnable task = new PatchOperationRecordTask(mContext, database,
				executorService);
		task.run();

		assertNull(or1.arrivedAt);
		assertNull(or1.departedAt);
		assertEquals(now, or2.arrivedAt);
		assertEquals(now, or2.departedAt);

		Cursor c = database.query(OperationRecord.TABLE_NAME, null, null,
				null, null, null, OperationRecord.Columns._ID, null);
		try {
			assertTrue(c.moveToFirst());
			assertEquals(
					3,
					c.getLong(c
							.getColumnIndexOrThrow(OperationRecord.Columns.SERVER_VERSION)));
			assertTrue(c.moveToNext());
			assertEquals(
					2,
					c.getLong(c
							.getColumnIndexOrThrow(OperationRecord.Columns.SERVER_VERSION)));
		} finally {
			c.close();
		}
	}
}
