package com.kogasoftware.odt.invehicledevice.contentprovider.task;

import android.database.Cursor;

import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.contentprovider.json.PlatformJson;
import com.kogasoftware.odt.invehicledevice.contentprovider.json.UserJson;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.OperationSchedules;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.Platforms;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.Users;
import com.kogasoftware.odt.invehicledevice.contentprovider.task.GetOperationSchedulesTask;

public class GetOperationSchedulesTaskTestCase
		extends
			SynchronizationTaskTestCase {
	@Override
	public void setUp() throws Exception {
		super.setUp();
		signIn();
	}

	public void testRun() {
		UserJson u = server.addUser("マイクロ 太郎");
		PlatformJson p1 = server.addPlatform("御徒町");
		PlatformJson p2 = server.addPlatform("秋葉原");
		PlatformJson p3 = server.addPlatform("神田");
		server.addOperationSchedule(p1, p2, Lists.newArrayList(u), "10:00:00",
				"10:00:02", 20);
		server.addOperationSchedule(p1, p3, Lists.newArrayList(u), "09:50:00",
				"10:00:02", 30);
		Runnable task = new GetOperationSchedulesTask(mContext, database,
				executorService);
		task.run();

		Cursor osCursor = database.query(OperationSchedules.TABLE_NAME, null,
				null, null, null, null, null);
		try {
			assertEquals(3, osCursor.getCount()); // 御徒町が1件マージされて3件になる
		} finally {
			osCursor.close();
		}

		Cursor pCursor = database.query(Platforms.TABLE_NAME, null, null, null,
				null, null, Platforms.Columns._ID);
		try {
			assertEquals(3, pCursor.getCount());
			pCursor.moveToFirst();
			assertEquals(p1.name, pCursor.getString(pCursor
					.getColumnIndexOrThrow(Platforms.Columns.NAME)));
			pCursor.moveToNext();
			assertEquals(p2.name, pCursor.getString(pCursor
					.getColumnIndexOrThrow(Platforms.Columns.NAME)));
			pCursor.moveToNext();
			assertEquals(p3.name, pCursor.getString(pCursor
					.getColumnIndexOrThrow(Platforms.Columns.NAME)));

		} finally {
			pCursor.close();
		}

		Cursor uCursor = database.query(Users.TABLE_NAME, null, null, null,
				null, null, null);
		try {
			assertEquals(1, uCursor.getCount());
		} finally {
			uCursor.close();
		}
	}
}
