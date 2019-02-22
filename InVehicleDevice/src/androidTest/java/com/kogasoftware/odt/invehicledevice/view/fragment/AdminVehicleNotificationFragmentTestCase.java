package com.kogasoftware.odt.invehicledevice.view.fragment;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.test.ActivityInstrumentationTestCase2;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.model.contentprovider.DatabaseHelper;
import com.kogasoftware.odt.invehicledevice.model.contentprovider.json.VehicleNotificationJson;
import com.kogasoftware.odt.invehicledevice.model.contentprovider.table.VehicleNotification;
import com.kogasoftware.odt.invehicledevice.model.contentprovider.table.VehicleNotification.Response;
import com.kogasoftware.odt.invehicledevice.model.contentprovider.task.GetVehicleNotificationsTask;
import com.kogasoftware.odt.invehicledevice.mockserver.MockServer;
import com.kogasoftware.odt.invehicledevice.view.activity.InVehicleDeviceActivity;
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.TestUtils;
import com.robotium.solo.Condition;
import com.robotium.solo.Solo;

public class AdminVehicleNotificationFragmentTestCase
		extends
			ActivityInstrumentationTestCase2<InVehicleDeviceActivity> {
	MockServer server;
	Solo solo;
	SQLiteDatabase database;

	public AdminVehicleNotificationFragmentTestCase() {
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
	}

	@Override
	public void tearDown() throws Exception {
		try {
			TestUtils.dispose(solo, server, database);
		} finally {
			super.tearDown();
		}
	}

	public void testNormalVehicleNotification() throws InterruptedException {
		solo = new Solo(getInstrumentation(), getActivity());
		final VehicleNotificationJson vn = server.addVehicleNotification("ハロー",
				"はろー", VehicleNotification.NotificationKind.NORMAL);
		assertNull(vn.response);
		assertNull(vn.readAt);
		assertTrue(solo.waitForCondition(new Condition() {
			@Override
			public boolean isSatisfied() {
				return solo.searchText("ハロー", true);
			}
		}, GetVehicleNotificationsTask.INTERVAL_MILLIS * 3 / 2));
		solo.clickOnButton(solo.getString(R.string.yes));
		assertTrue(solo.waitForCondition(new Condition() {
			@Override
			public boolean isSatisfied() {
				return Response.YES.equals(vn.response) && vn.readAt != null;
			}
		}, 5000));
	}
}
