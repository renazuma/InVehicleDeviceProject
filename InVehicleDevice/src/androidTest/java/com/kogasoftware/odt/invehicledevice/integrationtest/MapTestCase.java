package com.kogasoftware.odt.invehicledevice.integrationtest;

import android.test.ActivityInstrumentationTestCase2;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.apiclient.InVehicleDeviceApiClientFactory;
import com.kogasoftware.odt.invehicledevice.testutil.TestUtil;
import com.kogasoftware.odt.invehicledevice.testutil.apiclient.DummyApiClient;
import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;
import com.robotium.solo.Solo;

public class MapTestCase extends
		ActivityInstrumentationTestCase2<InVehicleDeviceActivity> {

	private Solo solo;

	@SuppressWarnings("deprecation")
	public MapTestCase() {
		super("com.kogasoftware.odt.invehicledevice.ui.activity",
				InVehicleDeviceActivity.class);
		InVehicleDeviceApiClientFactory.setInstance(new DummyApiClient());
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		TestUtil.clearLocalStorage(getInstrumentation());
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

	public void xtest01_起動時は非表示() {
		// assertFalse(solo.waitForView(NavigationModalView.class, 0, 500));
	}

	public void xtest02_地図ボタンを押したら表示() {
		xtest01_起動時は非表示();
		solo.clickOnView(solo.getView(R.id.map_button));
		// assertTrue(solo.waitForView(NavigationModalView.class));
	}

	public void xtest03_戻るボタンを押したら消える() {
		xtest02_地図ボタンを押したら表示();
		solo.clickOnButton("戻る");
		getInstrumentation().waitForIdleSync();
		// assertFalse(solo.waitForView(NavigationModalView.class));
	}

	public void xtest04_一回閉じてからもう地図ボタンを押したら表示() {
		xtest03_戻るボタンを押したら消える();
		solo.clickOnView(solo.getView(R.id.map_button));
		// assertTrue(solo.waitForView(NavigationModalView.class));
	}

	public void xtest05_拡大ボタンを押下で地図が拡大する() {
		xtest02_地図ボタンを押したら表示();
		solo.clickOnButton("拡大");
	}

	public void xtest06_縮小ボタンを押下で地図が縮小する() {
		xtest02_地図ボタンを押したら表示();
		solo.clickOnButton("縮小");
	}
}

