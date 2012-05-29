package com.kogasoftware.odt.invehicledevice.test.integration;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess;
import com.kogasoftware.odt.invehicledevice.logic.datasource.DataSourceFactory;
import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;
import com.kogasoftware.odt.invehicledevice.test.util.datasource.ScheduleChangedTestDataSource;
import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;

public class ScheduleChangedTestCase extends
		ActivityInstrumentationTestCase2<InVehicleDeviceActivity> {

	private Solo solo;

	public ScheduleChangedTestCase() {
		super("com.kogasoftware.odt.invehicledevice.ui.activity",
				InVehicleDeviceActivity.class);
		DataSourceFactory.setInstance(new ScheduleChangedTestDataSource());
		StatusAccess.clearSavedFile();
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
		assertTrue(TestUtil.waitForStartUi(getActivity()));
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
	}

	public void test01_テスト() throws Exception {
		assertTrue(solo.searchText("乗降場A"));

		solo.clickOnButton("到着しました");
		solo.clickOnButton("到着する");
		getInstrumentation().waitForIdleSync();
		solo.clickOnButton("出発する");
		getInstrumentation().waitForIdleSync();
		solo.clickOnView(solo.getButton("出発する", true));
		getInstrumentation().waitForIdleSync();

		assertFalse(solo.searchText("乗降場A"));
		assertTrue(solo.searchText("乗降場B"));
		
		Thread.sleep(20 * 1000); // 通知を待つ
		solo.clickOnButton("戻る");
		assertFalse(solo.searchText("乗降場B", true));
		assertTrue(solo.searchText("乗降場C"));

		solo.clickOnButton("到着しました");
		getInstrumentation().waitForIdleSync();
		solo.clickOnView(solo.getView(R.id.change_phase_button));
		getInstrumentation().waitForIdleSync();
		solo.clickOnView(solo.getView(R.id.start_button));

		assertFalse(solo.searchText("乗降場C"));
		assertTrue(solo.searchText("乗降場B", true));
	}
}
