package com.kogasoftware.odt.invehicledevice.test;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;

import com.jayway.android.robotium.solo.Solo;
import com.kogasoftware.odt.invehicledevice.InVehicleDeviceActivity;
import com.kogasoftware.odt.invehicledevice.R;

public class MapTestCase extends
		ActivityInstrumentationTestCase2<InVehicleDeviceActivity> {

	private Solo solo;

	public MapTestCase() {
		super("com.kogasoftware.odt.invehicledevice",
				InVehicleDeviceActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	public void test01起動時は非表示() {
		assertEquals(View.GONE, solo.getView(R.id.navigation_modal).getVisibility());
	}

	public void test02地図ボタンを押したら表示() {
		test01起動時は非表示();
		solo.clickOnView(solo.getView(R.id.map_button));
		assertEquals(View.VISIBLE, solo.getView(R.id.navigation_modal)
				.getVisibility());
	}

	public void test03戻るボタンを押したら消える() {
		test02地図ボタンを押したら表示();
		solo.clickOnButton("戻る");
		assertEquals(View.GONE, solo.getView(R.id.navigation_modal).getVisibility());
	}

	public void test04一回閉じてからもう地図ボタンを押したら表示() {
		test03戻るボタンを押したら消える();
		solo.clickOnView(solo.getView(R.id.map_button));
		assertEquals(View.VISIBLE, solo.getView(R.id.navigation_modal)
				.getVisibility());
	}

	public void test05拡大ボタンを押下で地図が拡大する() {
		test02地図ボタンを押したら表示();
		solo.clickOnButton("拡大");
		assertEquals(View.VISIBLE, solo.getView(R.id.navigation_modal)
				.getVisibility());
	}

	public void test06縮小ボタンを押下で地図が縮小する() {
		test02地図ボタンを押したら表示();
		solo.clickOnButton("縮小");
		assertEquals(View.VISIBLE, solo.getView(R.id.navigation_modal)
				.getVisibility());
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
	}
}