package com.kogasoftware.odt.invehicledevice.infra.contentprovider.task;

import android.database.Cursor;

import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.ServiceProvider;

public class GetServiceProviderTaskTestCase extends SynchronizationTaskTestCase {
	@Override
	public void setUp() throws Exception {
		super.setUp();
		signIn();
	}

	public void testRun() {
		server.serviceProviders.get(0).name = "ミュンヘン市";
		GetServiceProviderTask task = new GetServiceProviderTask(mContext,
				database, executorService);
		task.run();
		Cursor cursor = database.query(ServiceProvider.TABLE_NAME, null, null,
				null, null, null, null);
		try {
			assertEquals(1, cursor.getCount());
			cursor.moveToFirst();
			assertEquals("ミュンヘン市",
					cursor.getString(cursor.getColumnIndexOrThrow("name")));
		} finally {
			cursor.close();
		}
	}
}
