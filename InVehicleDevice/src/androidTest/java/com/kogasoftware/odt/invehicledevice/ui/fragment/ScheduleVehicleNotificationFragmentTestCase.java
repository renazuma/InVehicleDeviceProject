package com.kogasoftware.odt.invehicledevice.ui.fragment;

import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.test.ActivityInstrumentationTestCase2;

import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.contentprovider.DatabaseHelper;
import com.kogasoftware.odt.invehicledevice.contentprovider.json.PlatformJson;
import com.kogasoftware.odt.invehicledevice.contentprovider.json.UserJson;
import com.kogasoftware.odt.invehicledevice.contentprovider.json.VehicleNotificationJson;
import com.kogasoftware.odt.invehicledevice.contentprovider.model.VehicleNotification;
import com.kogasoftware.odt.invehicledevice.contentprovider.model.VehicleNotification.Response;
import com.kogasoftware.odt.invehicledevice.contentprovider.task.GetVehicleNotificationsTask;
import com.kogasoftware.odt.invehicledevice.mockserver.MockServer;
import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;
import com.kogasoftware.odt.invehicledevice.utils.TestUtils;
import com.robotium.solo.Condition;
import com.robotium.solo.Solo;

public class ScheduleVehicleNotificationFragmentTestCase
		extends
			ActivityInstrumentationTestCase2<InVehicleDeviceActivity> {
	MockServer server;
	Solo solo;
	SQLiteDatabase database;

	public ScheduleVehicleNotificationFragmentTestCase() {
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

	public void test() throws InterruptedException {
		solo = new Solo(getInstrumentation(), getActivity());
		solo.clickOnText(solo.getString(R.string.today_operation_schedule));
		assertTrue(solo.searchText("乗客も見る"));

		List<UserJson> users1 = Lists.newArrayList(server.addUser("マイクロ 次郎"));
		List<UserJson> users2 = Lists.newArrayList(server.addUser("まつもと ゆきひろ"),
				server.addUser("はしもと ゆきなり"));
		PlatformJson p1 = server.addPlatform("南浦和");
		PlatformJson p2 = server.addPlatform("東川口");
		PlatformJson p3 = server.addPlatform("東府中");
		server.addOperationSchedule(p1, p2, users1, "09:00:00", "09:00:02", 50);
		server.addOperationSchedule(p2, p3, users2, "10:00:00", "10:00:02", 50);
		server.reservations.get(0).memo = "よやくメモ";
		assertFalse(solo.searchText("南浦和"));

		final VehicleNotificationJson vn = server.addVehicleNotification(
				"変更されました", "へんこうされました",
				VehicleNotification.NotificationKind.SCHEDULE);
		assertNull(vn.response);
		assertNull(vn.readAt);
		assertTrue(solo.waitForCondition(new Condition() {
			@Override
			public boolean isSatisfied() {
				return solo.searchText("変更されました", true);
			}
		}, InVehicleDeviceActivity.VEHICLE_NOTIFICATION_ALERT_DELAY_MILLIS
				+ GetVehicleNotificationsTask.INTERVAL_MILLIS * 3 / 2));

		solo.clickOnButton(solo.getString(R.string.it_closes));
		assertTrue(solo.searchText("南浦和"));
		assertTrue(solo.waitForCondition(new Condition() {
			@Override
			public boolean isSatisfied() {
				return Response.YES.equals(vn.response) && vn.readAt != null;
			}
		}, 8000));
	}
}
