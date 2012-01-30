package com.kogasoftware.odt.invehicledevice.test;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;

import com.jayway.android.robotium.solo.Solo;
import com.kogasoftware.odt.invehicledevice.InVehicleDeviceActivity;
import com.kogasoftware.odt.invehicledevice.R;

public class ConfigTestCase extends
		ActivityInstrumentationTestCase2<InVehicleDeviceActivity> {

	private Solo solo;

	public ConfigTestCase() {
		super("com.kogasoftware.odt.invehicledevice",
				InVehicleDeviceActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	public void test起動時は非表示() {
		assertEquals(View.GONE, solo.getView(R.id.config_overlay)
				.getVisibility());
	}

	public void test運行管理ボタンを押したら表示() {
		test起動時は非表示();
		solo.clickOnView(solo.getView(R.id.config_button));
		assertEquals(View.VISIBLE, solo.getView(R.id.config_overlay)
				.getVisibility());
	}

	public void test閉じるボタンを押したら消える() {
		test運行管理ボタンを押したら表示();
		solo.clickOnView(solo.getView(R.id.config_hide_button));
		assertEquals(View.GONE, solo.getView(R.id.config_overlay)
				.getVisibility());
	}

	public void test一回閉じてからもう運行管理ボタンを押したら表示() {
		test閉じるボタンを押したら消える();
		solo.clickOnView(solo.getView(R.id.config_button));
		assertEquals(View.VISIBLE, solo.getView(R.id.config_overlay)
				.getVisibility());
	}

	public void test中止ボタンを押すと中止確認画面が表示() {
		test運行管理ボタンを押したら表示();
		assertEquals(View.GONE, solo.getView(R.id.stop_check_overlay)
				.getVisibility());
		solo.clickOnView(solo.getView(R.id.stop_check_button));
		assertEquals(View.VISIBLE, solo.getView(R.id.stop_check_overlay)
				.getVisibility());
	}

	public void test中止確認画面で中止ボタンを押すと中止画面が表示() {
		test中止ボタンを押すと中止確認画面が表示();
		assertEquals(View.GONE, solo.getView(R.id.stop_overlay).getVisibility());
		solo.clickOnView(solo.getView(R.id.stop_button));
		assertEquals(View.VISIBLE, solo.getView(R.id.stop_overlay)
				.getVisibility());
	}

	public void test中止確認画面で中止しないボタンを押すと中止確認が消える() {
		test中止ボタンを押すと中止確認画面が表示();
		solo.clickOnView(solo.getView(R.id.stop_cancel_button));
		assertEquals(View.GONE, solo.getView(R.id.stop_check_overlay)
				.getVisibility());
	}

	public void test停止ボタンを押すと停止画面が表示() {
		test運行管理ボタンを押したら表示();
		assertEquals(View.GONE, solo.getView(R.id.pause_overlay)
				.getVisibility());
		solo.clickOnView(solo.getView(R.id.pause_button));
		assertEquals(View.VISIBLE, solo.getView(R.id.pause_overlay)
				.getVisibility());
	}

	public void test停止画面で運行開始ボタンを押すと停止画面と設定画面が非表示() {
		test停止ボタンを押すと停止画面が表示();
		solo.clickOnView(solo.getView(R.id.pause_cancel_button));
		assertEquals(View.GONE, solo.getView(R.id.config_overlay)
				.getVisibility());
		assertEquals(View.GONE, solo.getView(R.id.pause_overlay)
				.getVisibility());
	}

	public void test左にフリックすると非表示() {
		test運行管理ボタンを押したら表示();

	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
	}
}