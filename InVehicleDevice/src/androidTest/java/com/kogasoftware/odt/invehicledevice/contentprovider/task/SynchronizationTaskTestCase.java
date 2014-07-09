package com.kogasoftware.odt.invehicledevice.contentprovider.task;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.kogasoftware.odt.invehicledevice.contentprovider.DatabaseHelper;
import com.kogasoftware.odt.invehicledevice.mockserver.MockServer;
import com.kogasoftware.odt.invehicledevice.utils.TestUtils;

public class SynchronizationTaskTestCase extends AndroidTestCase {
	DatabaseHelper databaseHelper;
	SQLiteDatabase database;
	MockServer server;
	ScheduledExecutorService executorService = Executors
			.newScheduledThreadPool(1);

	@Override
	public void setUp() throws Exception {
		super.setUp();
		databaseHelper = new DatabaseHelper(getContext());
		database = databaseHelper.getWritableDatabase();
		TestUtils.clean(database);

		server = new MockServer(12345);
		server.start();
	}

	@Override
	public void tearDown() throws Exception {
		TestUtils.close(server, database, databaseHelper, executorService);
		super.tearDown();
	}

	protected void signIn() {
		server.signIn(database);
	}
}
