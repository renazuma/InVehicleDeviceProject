package com.kogasoftware.odt.invehicledevice.ui.fragment;

import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.ActivityInstrumentationTestCase2;

import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.contentprovider.DatabaseHelper;
import com.kogasoftware.odt.invehicledevice.contentprovider.json.OperationRecordJson;
import com.kogasoftware.odt.invehicledevice.contentprovider.json.PassengerRecordJson;
import com.kogasoftware.odt.invehicledevice.contentprovider.json.PlatformJson;
import com.kogasoftware.odt.invehicledevice.contentprovider.json.UserJson;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.PassengerRecords;
import com.kogasoftware.odt.invehicledevice.mockserver.MockServer;
import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;
import com.kogasoftware.odt.invehicledevice.utils.TestUtils;
import com.robotium.solo.Condition;
import com.robotium.solo.Solo;

public class PassengerRecordErrorFragmentTestCase
		extends
			ActivityInstrumentationTestCase2<InVehicleDeviceActivity> {

	MockServer server;
	Solo solo;
	SQLiteDatabase database;

	public PassengerRecordErrorFragmentTestCase() {
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

	public void testGetOnError() throws InterruptedException {
		List<UserJson> users1 = Lists.newArrayList(server.addUser("マイクロ 次郎"));
		List<UserJson> users2 = Lists.newArrayList(server.addUser("まつもと ゆきひろ"),
				server.addUser("はしもと ゆきなり"));
		PlatformJson p1 = server.addPlatform("南浦和");
		PlatformJson p2 = server.addPlatform("東川口");
		PlatformJson p3 = server.addPlatform("東府中");
		server.addOperationSchedule(p1, p2, users1, "09:00:00", "09:00:02", 50);
		server.addOperationSchedule(p2, p3, users2, "10:00:00", "10:00:02", 50);
		server.reservations.get(0).memo = "よやくメモ";

		final OperationRecordJson or1 = server.operationRecords.get(0);
		final OperationRecordJson or2 = server.operationRecords.get(1);
		final OperationRecordJson or3 = server.operationRecords.get(2);
		final PassengerRecordJson pr1 = server.passengerRecords.get(0);
		final PassengerRecordJson pr2 = server.passengerRecords.get(1);
		final PassengerRecordJson pr3 = server.passengerRecords.get(2);

		solo = new Solo(getInstrumentation(), getActivity());

		assertTrue(solo.searchText("運行中", true));
		assertTrue(solo.searchText("南浦和", true));
		Thread.sleep(3000);
		solo.clickOnButton(solo.getString(R.string.it_arrives_button_text));
		Thread.sleep(3000);
		solo.clickOnButton(solo.getString(R.string.it_arrives));
		Thread.sleep(3000);

		assertTrue(solo.searchText("乗車中", true));
		Thread.sleep(3000);
		solo.clickOnButton("確認\nする");
		Thread.sleep(3000);
		solo.clickOnText("未乗車でよい", 1);
		Thread.sleep(3000);
		assertTrue(solo.waitForCondition(new Condition() {
			@Override
			public boolean isSatisfied() {
				Cursor cursor = database
						.query(PassengerRecords.TABLE_NAME,
								new String[]{PassengerRecords.Columns.IGNORE_GET_ON_MISS},
								PassengerRecords.Columns._ID + " = ?",
								new String[]{pr1.id.toString()}, null, null,
								null);
				try {
					if (cursor.moveToFirst()) {
						return !cursor.isNull(0) && cursor.getInt(0) != 0;
					}
				} finally {
					cursor.close();
				}
				return false;
			}
		}, 20 * 1000));
	}
}
