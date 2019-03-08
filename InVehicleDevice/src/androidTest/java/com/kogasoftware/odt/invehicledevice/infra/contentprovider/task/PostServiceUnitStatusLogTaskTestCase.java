package com.kogasoftware.odt.invehicledevice.infra.contentprovider.task;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;

import android.content.ContentValues;

import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.ServiceUnitStatusLog;

public class PostServiceUnitStatusLogTaskTestCase
		extends
			SynchronizationTaskTestCase {
	@Override
	public void setUp() throws Exception {
		super.setUp();
		signIn();
	}

	public void testRun() throws Exception {
		Long now = 12345678L;
		DateTimeUtils.setCurrentMillisFixed(now);

		Long t1 = now - InsertServiceUnitStatusLogTask.INTERVAL_MILLIS / 2;
		Long t2 = now - (InsertServiceUnitStatusLogTask.INTERVAL_MILLIS + 100);
		Long t3 = now;

		ContentValues values1 = new ContentValues();
		values1.put(ServiceUnitStatusLog.Columns._ID, 1);
		values1.put(ServiceUnitStatusLog.Columns.CREATED_AT, t1);
		database.insertOrThrow(ServiceUnitStatusLog.TABLE_NAME, null, values1);

		ContentValues values2 = new ContentValues();
		values2.put(ServiceUnitStatusLog.Columns._ID, 2);
		values2.put(ServiceUnitStatusLog.Columns.CREATED_AT, t2);
		database.insertOrThrow(ServiceUnitStatusLog.TABLE_NAME, null, values2);

		ContentValues values3 = new ContentValues();
		values3.put(ServiceUnitStatusLog.Columns._ID, 3);
		values3.put(ServiceUnitStatusLog.Columns.CREATED_AT, t3);
		database.insertOrThrow(ServiceUnitStatusLog.TABLE_NAME, null, values3);

		Runnable task = new PostServiceUnitStatusLogTask(mContext, database,
				executorService);
		task.run();
		assertEquals(1, server.serviceUnitStatusLogs.size());
		assertEquals(2, server.serviceUnitStatusLogs.get(0).id.intValue());
		task.run();
		assertEquals(1, server.serviceUnitStatusLogs.size());
		assertEquals(2, server.serviceUnitStatusLogs.get(0).id.intValue());

		DateTimeUtils.setCurrentMillisFixed(DateTime.now().getMillis()
				+ InsertServiceUnitStatusLogTask.INTERVAL_MILLIS);
		task.run();
		assertEquals(2, server.serviceUnitStatusLogs.size());
		assertEquals(2, server.serviceUnitStatusLogs.get(0).id.intValue());
		assertEquals(1, server.serviceUnitStatusLogs.get(1).id.intValue());
	}
}
