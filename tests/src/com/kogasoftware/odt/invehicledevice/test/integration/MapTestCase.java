package com.kogasoftware.odt.invehicledevice.test.integration;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;

import com.jayway.android.robotium.solo.Solo;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess;
import com.kogasoftware.odt.invehicledevice.logic.datasource.DataSourceFactory;
import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;
import com.kogasoftware.odt.invehicledevice.test.util.datasource.DummyDataSource;
import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;

public class MapTestCase extends
		ActivityInstrumentationTestCase2<InVehicleDeviceActivity> {

	private Solo solo;

	public MapTestCase() {
		super("com.kogasoftware.odt.invehicledevice.ui.activity",
				InVehicleDeviceActivity.class);
		DataSourceFactory.setInstance(new DummyDataSource());
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		StatusAccess.clearSavedFile();
		solo = new Solo(getInstrumentation(), getActivity());
		assertTrue(TestUtil.waitForStartUi(getActivity()));
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
	}

	public void test01_起動時は非表示() {
		assertEquals(View.GONE, solo.getView(R.id.navigation_modal_view)
				.getVisibility());
	}

	public void test02_地図ボタンを押したら表示() {
		test01_起動時は非表示();
		solo.clickOnView(solo.getView(R.id.map_button));
		assertEquals(View.VISIBLE, solo.getView(R.id.navigation_modal_view)
				.getVisibility());
	}

	public void test03_戻るボタンを押したら消える() {
		test02_地図ボタンを押したら表示();
		solo.clickOnButton("戻る");
		getInstrumentation().waitForIdleSync();
		assertEquals(View.GONE, solo.getView(R.id.navigation_modal_view)
				.getVisibility());
	}

	public void test04_一回閉じてからもう地図ボタンを押したら表示() {
		test03_戻るボタンを押したら消える();
		solo.clickOnView(solo.getView(R.id.map_button));
		assertEquals(View.VISIBLE, solo.getView(R.id.navigation_modal_view)
				.getVisibility());
	}

	public void test05_拡大ボタンを押下で地図が拡大する() {
		test02_地図ボタンを押したら表示();
		solo.clickOnButton("拡大");
		assertEquals(View.VISIBLE, solo.getView(R.id.navigation_modal_view)
				.getVisibility());
	}

	public void test06_縮小ボタンを押下で地図が縮小する() {
		test02_地図ボタンを押したら表示();
		solo.clickOnButton("縮小");
		assertEquals(View.VISIBLE, solo.getView(R.id.navigation_modal_view)
				.getVisibility());
	}
}