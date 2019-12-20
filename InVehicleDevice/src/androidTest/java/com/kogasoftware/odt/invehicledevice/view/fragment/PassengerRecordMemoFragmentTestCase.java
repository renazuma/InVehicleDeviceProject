package com.kogasoftware.odt.invehicledevice.view.fragment;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.test.ActivityInstrumentationTestCase2;

import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.DatabaseHelper;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.json.PlatformJson;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.json.UserJson;
import com.kogasoftware.odt.invehicledevice.mockserver.MockServer;
import com.kogasoftware.odt.invehicledevice.view.activity.InVehicleDeviceActivity;
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.TestUtils;
import com.robotium.solo.Solo;

import java.util.List;

public class PassengerRecordMemoFragmentTestCase
		extends
			ActivityInstrumentationTestCase2<InVehicleDeviceActivity> {
	MockServer server;
	Solo solo;
	SQLiteDatabase database;

	public PassengerRecordMemoFragmentTestCase() {
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
		UserJson user1 = server.addUser("マイクロ 次郎");
		user1.memo = "ゆーざーメモ";
		user1.neededCare = true;
		user1.licenseReturned = true;
		List<UserJson> users1 = Lists.newArrayList(user1);
		List<UserJson> users2 = Lists.newArrayList(server.addUser("まつもと ゆきひろ"),
				server.addUser("はしもと ゆきなり"));
		PlatformJson p1 = server.addPlatform("南浦和");
		PlatformJson p2 = server.addPlatform("東川口");
		PlatformJson p3 = server.addPlatform("東府中");
		server.addOperationSchedule(p1, p2, users1, "09:00:00", "09:02:00", 50);
		server.addOperationSchedule(p2, p3, users2, "10:00:00", "10:02:00", 50);
		server.reservations.get(0).memo = "よやくメモ";
		solo = new Solo(getInstrumentation(), getActivity());
		solo.clickOnText(solo.getString(R.string.today_operation_schedule));
		solo.clickOnButton("乗客も見る");
		solo.clickOnButton("メモ");
		assertTrue(solo.searchText("ゆーざーメモ", true));
		assertTrue(solo.searchText("要介護", true));
		assertTrue(solo.searchText("免許返納", true));
		assertTrue(solo.searchText("よやくメモ", true));
		solo.clickOnButton("戻る");
		assertFalse(solo.searchText("よやくメモ", true));
	}
}
