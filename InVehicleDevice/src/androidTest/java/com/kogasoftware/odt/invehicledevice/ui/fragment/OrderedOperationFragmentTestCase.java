package com.kogasoftware.odt.invehicledevice.ui.fragment;

import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.test.ActivityInstrumentationTestCase2;

import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.contentprovider.DatabaseHelper;
import com.kogasoftware.odt.invehicledevice.contentprovider.json.PlatformJson;
import com.kogasoftware.odt.invehicledevice.contentprovider.json.UserJson;
import com.kogasoftware.odt.invehicledevice.mockserver.MockServer;
import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;
import com.kogasoftware.odt.invehicledevice.utils.TestUtils;
import com.robotium.solo.Solo;

public class OrderedOperationFragmentTestCase
		extends
			ActivityInstrumentationTestCase2<InVehicleDeviceActivity> {
	MockServer server;
	Solo solo;
	SQLiteDatabase database;

	public OrderedOperationFragmentTestCase() {
		super(InVehicleDeviceActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		Context tc = getInstrumentation().getTargetContext();
		DatabaseHelper databaseHelper = new DatabaseHelper(tc);
		database = databaseHelper.getWritableDatabase();
		TestUtils.clear(database);

		server = new MockServer(12346);
		server.start();
		server.signIn(database);
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		try {
			solo.finishOpenedActivities();
			server.interrupt();
			server.join(5000);
			assertFalse(server.isAlive());
			database.close();
		} finally {
			super.tearDown();
		}
	}

	public void test() throws InterruptedException {
		List<UserJson> users1 = Lists.newArrayList(server.addUser("マイクロ 次郎"));
		PlatformJson p1 = server.addPlatform("南浦和");
		PlatformJson p2 = server.addPlatform("東川口");
		server.addOperationSchedule(p1, p2, users1, "09:00:00", "09:00:02", 50);
	}
}
