package com.kogasoftware.odt.invehicledevice.view.fragment;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.test.ActivityInstrumentationTestCase2;

import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.DatabaseHelper;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.json.OperationRecordJson;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.json.OperationScheduleJson;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.json.PassengerRecordJson;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.json.PlatformJson;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.json.UserJson;
import com.kogasoftware.odt.invehicledevice.mockserver.MockServer;
import com.kogasoftware.odt.invehicledevice.view.activity.InVehicleDeviceActivity;
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.TestUtils;
import com.robotium.solo.Condition;
import com.robotium.solo.Solo;

import org.joda.time.DateTime;

import java.util.List;

public class OperationListFragmentTestCase
		extends
			ActivityInstrumentationTestCase2<InVehicleDeviceActivity> {
	MockServer server;
	Solo solo;
	SQLiteDatabase database;

	public OperationListFragmentTestCase() {
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
		solo = new Solo(getInstrumentation(), getActivity());

		OperationRecordJson or = server.operationRecords.get(0);
		PassengerRecordJson pr = server.passengerRecords.get(0);

		assertNull(or.arrivedAt);
		assertNull(or.departedAt);
		solo.clickOnText(solo.getString(R.string.today_operation_schedule));
		solo.clickOnText("南浦和", 2); // 裏の画面にも「南浦和」があるため
		Thread.sleep(3000);
		assertNotNull(or.arrivedAt);
		assertNotNull(or.departedAt);

		solo.clickOnText("南浦和");
		Thread.sleep(3000);
		assertNull(or.arrivedAt);
		assertNull(or.departedAt);

		solo.clickOnText("乗客も見る");
		assertNull(pr.getOnTime);

		solo.clickOnText("マイクロ 次郎", 1);
		Thread.sleep(3000);
		assertNotNull(pr.getOnTime);
		assertNull(pr.getOffTime);

		solo.clickOnText("マイクロ 次郎", 1);
		Thread.sleep(3000);
		assertNull(pr.getOnTime);
		assertNull(pr.getOffTime);

		// getOnTimeも同時に記録される
		solo.clickOnText("マイクロ 次郎", 2);
		Thread.sleep(3000);
		assertNotNull(pr.getOnTime);
		assertNotNull(pr.getOffTime);

		solo.clickOnText("マイクロ 次郎", 2);
		Thread.sleep(3000);
		assertNotNull(pr.getOnTime);
		assertNull(pr.getOffTime);

		solo.clickOnText("マイクロ 次郎", 2);
		Thread.sleep(3000);
		assertNotNull(pr.getOnTime);
		assertNotNull(pr.getOffTime);

		// getOnTimeも同時にキャンセルされる
		solo.clickOnText("マイクロ 次郎", 0);
		Thread.sleep(3000);
		assertNull(pr.getOnTime);
		assertNull(pr.getOffTime);
	}

	public void testAlreadyDeparted() throws InterruptedException {
		List<UserJson> users1 = Lists.newArrayList(server.addUser("マイクロ 次郎"));
		List<UserJson> users2 = Lists.newArrayList(server.addUser("まつもと ゆきひろ"),
				server.addUser("はしもと ゆきなり"));
		PlatformJson p1 = server.addPlatform("南浦和");
		PlatformJson p2 = server.addPlatform("東川口");
		PlatformJson p3 = server.addPlatform("東府中");
		server.addOperationSchedule(p1, p2, users1, "09:00:00", "09:00:02", 50);
		OperationScheduleJson os3 = server.addOperationSchedule(p2, p3, users2,
				"10:00:00", "10:00:02", 50).second;
		final OperationRecordJson or3 = os3.operationRecord;
		or3.arrivedAt = DateTime.now();
		or3.departedAt = DateTime.now();
		server.reservations.get(0).memo = "よやくメモ";
		solo = new Solo(getInstrumentation(), getActivity());
		solo.clickOnText(solo.getString(R.string.today_operation_schedule));
		solo.clickOnText("東府中");
		assertTrue(solo.waitForCondition(new Condition() {
			@Override
			public boolean isSatisfied() {
				return or3.arrivedAt == null && or3.departedAt == null;
			}
		}, 5000));
	}

	public void testAlreadyGetOn() throws InterruptedException {
		List<UserJson> users1 = Lists.newArrayList(server.addUser("マイクロ 次郎"));
		List<UserJson> users2 = Lists.newArrayList(server.addUser("まつもと ゆきひろ"),
				server.addUser("はしもと ゆきなり"));
		PlatformJson p1 = server.addPlatform("南浦和");
		PlatformJson p2 = server.addPlatform("東川口");
		PlatformJson p3 = server.addPlatform("東府中");
		server.addOperationSchedule(p1, p2, users1, "09:00:00", "09:00:02", 50);
		server.addOperationSchedule(p2, p3, users2, "10:00:00", "10:00:02", 50);
		final PassengerRecordJson pr1 = server.passengerRecords.get(0);
		pr1.getOnTime = DateTime.now();

		solo = new Solo(getInstrumentation(), getActivity());
		solo.clickOnText(solo.getString(R.string.today_operation_schedule));
		solo.clickOnText("乗客も見る");
		solo.clickOnText("マイクロ 次郎");
		assertTrue(solo.waitForCondition(new Condition() {
			@Override
			public boolean isSatisfied() {
				return pr1.getOnTime == null;
			}
		}, 5000));
	}
}
