package com.kogasoftware.odt.invehicledevice.integrationtest;

import android.test.ActivityInstrumentationTestCase2;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.testutil.TestUtil;
import com.kogasoftware.odt.invehicledevice.testutil.apiclient.ScheduleChangedTestApiClient;
import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;
import com.robotium.solo.Solo;

public class ScheduleChangedTestCase extends
		ActivityInstrumentationTestCase2<InVehicleDeviceActivity> {

	private Solo solo;

	@SuppressWarnings("deprecation")
	public ScheduleChangedTestCase() {
		super("com.kogasoftware.odt.invehicledevice.ui.activity",
				InVehicleDeviceActivity.class);
		TestUtil.setApiClient(new ScheduleChangedTestApiClient());
		TestUtil.clearLocalStorage(getInstrumentation());
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
		assertTrue(TestUtil.waitForStartUI(getActivity()));
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
	}

	public void xtest01_テスト() throws Exception {

//		assertTrue(solo.searchText("乗降場A", true));

//		solo.clickOnButton("到着し");
//		solo.clickOnButton("到着する");
//		solo.clickOnButton("出発する");
// 		TestUtil.assertShow(solo, DepartureCheckModalView.class);
//		solo.clickOnView(solo.getButton("出発する", true));

//		assertFalse(solo.searchText("乗降場A", true));
//		assertTrue(solo.searchText("乗降場B", true));

		Thread.sleep(200 * 1000); // 通知を待つ

//		solo.clickOnButton("戻る");
	}

	public void xtest02_テスト() throws Exception {
		assertTrue(solo.searchText("乗降場A", true));

		solo.clickOnButton("到着しました");
		solo.clickOnButton("到着する");
		solo.clickOnButton("出発する");
		// TestUtil.assertShow(solo, DepartureCheckModalView.class);
		solo.clickOnView(solo.getButton("出発する", true));

		assertFalse(solo.searchText("乗降場A", true));
		assertTrue(solo.searchText("乗降場B", true));

		Thread.sleep(20 * 1000); // 通知を待つ
		solo.clickOnButton("戻る");
		assertFalse(solo.searchText("乗降場B", true));
		assertTrue(solo.searchText("乗降場C", true));

		solo.clickOnButton("到着しました");
		solo.clickOnView(solo.getView(R.id.change_phase_button));
		solo.clickOnView(solo.getView(R.id.departure_button));

		assertFalse(solo.searchText("乗降場C", true));
		assertTrue(solo.searchText("乗降場B", true));
	}
}
