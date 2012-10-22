package com.kogasoftware.odt.invehicledevice.test.integration;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.apiclient.DataSourceFactory;
import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;
import com.kogasoftware.odt.invehicledevice.test.util.apiclient.DummyDataSource;
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
		TestUtil.clearStatus();
		solo = new Solo(getInstrumentation(), getActivity());
		assertTrue(TestUtil.waitForStartUI(getActivity()));
		Thread.sleep(5000);
	}

	@Override
	public void tearDown() throws Exception {
		TestUtil.disableAutoStart(getInstrumentation().getContext());
		solo.finishOpenedActivities();
		super.tearDown();
	}

	public void test01_起動時は非表示() {
		assertFalse(solo.waitForView(NavigationModalView.class, 0, 500));
	}

	public void test02_地図ボタンを押したら表示() {
		test01_起動時は非表示();
		solo.clickOnView(solo.getView(R.id.map_button));
		assertTrue(solo.waitForView(NavigationModalView.class));
	}

	public void test03_戻るボタンを押したら消える() {
		test02_地図ボタンを押したら表示();
		solo.clickOnButton("戻る");
		getInstrumentation().waitForIdleSync();
		assertFalse(solo.waitForView(NavigationModalView.class));
	}

	public void test04_一回閉じてからもう地図ボタンを押したら表示() {
		test03_戻るボタンを押したら消える();
		solo.clickOnView(solo.getView(R.id.map_button));
		assertTrue(solo.waitForView(NavigationModalView.class));
	}

	public void test05_拡大ボタンを押下で地図が拡大する() {
		test02_地図ボタンを押したら表示();
		solo.clickOnButton("拡大");
	}

	public void test06_縮小ボタンを押下で地図が縮小する() {
		test02_地図ボタンを押したら表示();
		solo.clickOnButton("縮小");
	}
}

