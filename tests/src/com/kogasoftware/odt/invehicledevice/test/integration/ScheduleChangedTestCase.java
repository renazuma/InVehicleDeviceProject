package com.kogasoftware.odt.invehicledevice.test.integration;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.datasource.DataSourceFactory;
import com.kogasoftware.odt.invehicledevice.test.util.datasource.ScheduleChangedTestDataSource;
import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;

public class ScheduleChangedTestCase extends
		ActivityInstrumentationTestCase2<InVehicleDeviceActivity> {

	private Solo solo;

	public ScheduleChangedTestCase() {
		super("com.kogasoftware.odt.invehicledevice.ui.activity",
				InVehicleDeviceActivity.class);
		DataSourceFactory.setInstance(new ScheduleChangedTestDataSource());
		CommonLogic.clearStatusFile();
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
		sync();
	}

	private void sync() throws InterruptedException {
		getInstrumentation().waitForIdleSync();
		Thread.sleep(500);
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
	}

	public void test01_テスト起動() throws Exception {
		assertTrue(solo.searchText("乗降場A"));

		solo.clickOnButton("到着しました");
		sync();
		solo.clickOnView(solo.getView(R.id.change_phase_button));
		sync();
		solo.clickOnView(solo.getView(R.id.start_button));
		sync();

		assertFalse(solo.searchText("乗降場A"));
		assertTrue(solo.searchText("乗降場B"));
		Thread.sleep(50 * 1000); // 通知を待つ

		assertFalse(solo.searchText("乗降場B"));
		assertTrue(solo.searchText("乗降場C"));

		solo.clickOnButton("到着しました");
		sync();
		solo.clickOnView(solo.getView(R.id.change_phase_button));
		sync();
		solo.clickOnView(solo.getView(R.id.start_button));
		sync();

		assertFalse(solo.searchText("乗降場C"));
		assertTrue(solo.searchText("乗降場B"));
	}
}
