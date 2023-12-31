package com.kogasoftware.odt.invehicledevice.scenariotest;

import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.test.ActivityInstrumentationTestCase2;

import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.DatabaseHelper;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.json.PassengerRecordJson;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.json.PlatformJson;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.json.UserJson;
import com.kogasoftware.odt.invehicledevice.mockserver.MockServer;
import com.kogasoftware.odt.invehicledevice.view.activity.InVehicleDeviceActivity;
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.SoloUtils;
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.TestUtils;
import com.robotium.solo.Condition;
import com.robotium.solo.Solo;

public class OrderedOperationTestCase
		extends
			ActivityInstrumentationTestCase2<InVehicleDeviceActivity> {
	public OrderedOperationTestCase() {
		super(InVehicleDeviceActivity.class);
	}

	MockServer server;
	Solo solo;
	SQLiteDatabase database;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		Context tc = getInstrumentation().getTargetContext();
		DatabaseHelper databaseHelper = new DatabaseHelper(tc);
		database = databaseHelper.getWritableDatabase();
		TestUtils.clear(database);

		server = new MockServer(12346);
		server.start();
		server.signIn(tc.getContentResolver());
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
		List<UserJson> users1 = Lists.newArrayList(server.addUser("マイクロ 次郎"));
		List<UserJson> users2 = Lists.newArrayList(server.addUser("まつもと ゆきひろ"),
				server.addUser("はしもと ゆきなり"));
		PlatformJson p1 = server.addPlatform("南浦和");
		PlatformJson p2 = server.addPlatform("東川口");
		PlatformJson p3 = server.addPlatform("東府中");
		server.addOperationSchedule(p1, p2, users1, "09:00:00", "09:00:02", 50);
		server.addOperationSchedule(p2, p3, users2, "10:00:00", "10:00:02", 50);
		server.reservations.get(0).memo = "よやくメモ";

		final PassengerRecordJson pr1 = server.passengerRecords.get(0);
		final PassengerRecordJson pr2 = server.passengerRecords.get(1);
		final PassengerRecordJson pr3 = server.passengerRecords.get(2);

		solo = new Solo(getInstrumentation(), SoloUtils.LONGER_TIMEOUT,
				getActivity());
		assertTrue(solo.waitForText("運行中", 1, 180 * 1000, false, true)); // 初回起動時なので長く待つ
		assertTrue(solo.searchText("南浦和", true));
		Thread.sleep(3000);
		solo.clickOnButton(solo.getString(R.string.it_arrives_button_text));
		Thread.sleep(3000);
		solo.clickOnButton(solo.getString(R.string.it_arrives));
		Thread.sleep(3000);

		assertTrue(solo.searchText("乗車中", true));
		solo.clickOnText("マイクロ 次郎");
		assertTrue(solo.waitForCondition(new Condition() {
			@Override
			public boolean isSatisfied() {
				return pr1.getOnTime != null;
			}
		}, 5000));
		Thread.sleep(3000);
		solo.clickOnButton("確認\nする");
		Thread.sleep(5000);
		solo.clickOnButton("出発する");

		assertTrue(solo.searchText("運行中", true));
		assertTrue(solo.searchText("東川口", true));
		solo.clickOnButton(solo.getString(R.string.it_arrives_button_text));
		solo.clickOnButton(solo.getString(R.string.it_arrives));
		solo.clickOnText("マイクロ 次郎");
		assertTrue(solo.waitForCondition(new Condition() {
			@Override
			public boolean isSatisfied() {
				return pr1.getOffTime != null;
			}
		}, 5000));

		Thread.sleep(3000);
		solo.clickOnButton("確認\nする");
		solo.clickOnText("まつもと ゆきひろ");
		solo.clickOnText("はしもと ゆきなり");
		solo.clickOnButton("確認\nする");
		Thread.sleep(5000);
		solo.clickOnButton("出発する");
		assertTrue(solo.waitForCondition(new Condition() {
			@Override
			public boolean isSatisfied() {
				return pr2.getOnTime != null && pr3.getOnTime != null;
			}
		}, 5000));

		assertTrue(solo.searchText("運行中", true));
		assertTrue(solo.searchText("東府中", true));
		solo.clickOnButton(solo.getString(R.string.it_arrives_button_text));
		solo.clickOnButton(solo.getString(R.string.it_arrives));
		solo.clickOnText("まつもと ゆきひろ");
		solo.clickOnText("はしもと ゆきなり");
		assertTrue(solo.waitForCondition(new Condition() {
			@Override
			public boolean isSatisfied() {
				return pr2.getOffTime != null && pr3.getOffTime != null;
			}
		}, 5000));

		Thread.sleep(3000);
		solo.clickOnButton("確認\nする");
		Thread.sleep(5000);
		solo.clickOnButton("確定する");
		assertTrue(solo.searchText("運行終了", true));
	}
}
