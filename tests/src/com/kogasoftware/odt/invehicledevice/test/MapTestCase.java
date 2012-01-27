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

	public void test起動時は非表示() {
		assertEquals(View.GONE, solo.getView(R.id.map_overlay).getVisibility());
	}

	public void test地図ボタンを押したら表示() {
		test起動時は非表示();
		solo.clickOnView(solo.getView(R.id.map_button));
		assertEquals(View.VISIBLE, solo.getView(R.id.map_overlay)
				.getVisibility());
	}

	public void test閉じるボタンを押したら消える() {
		test地図ボタンを押したら表示();
		solo.clickOnView(solo.getView(R.id.map_hide_button));
		assertEquals(View.GONE, solo.getView(R.id.map_overlay).getVisibility());
	}

	public void test一回閉じてからもう地図ボタンを押したら表示() {
		test閉じるボタンを押したら消える();
		solo.clickOnView(solo.getView(R.id.map_button));
		assertEquals(View.VISIBLE, solo.getView(R.id.map_overlay)
				.getVisibility());
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
	}
}