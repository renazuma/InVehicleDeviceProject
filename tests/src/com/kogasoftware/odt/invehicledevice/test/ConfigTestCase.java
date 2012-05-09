package com.kogasoftware.odt.invehicledevice.test;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;

import com.jayway.android.robotium.solo.Solo;
import com.kogasoftware.odt.invehicledevice.InVehicleDeviceActivity;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.datasource.DataSourceFactory;
import com.kogasoftware.odt.invehicledevice.datasource.DummyDataSource;
import com.kogasoftware.odt.invehicledevice.logic.Logic;

public class ConfigTestCase extends
		ActivityInstrumentationTestCase2<InVehicleDeviceActivity> {

	private Solo solo;

	public void dataset() {

	}

	public ConfigTestCase() {
		super("com.kogasoftware.odt.invehicledevice",
				InVehicleDeviceActivity.class);
		DataSourceFactory.setInstance(new DummyDataSource());
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		Logic.clearStatusFile();
		solo = new Solo(getInstrumentation(), getActivity());
	}


	public void test01_起動時は非表示() {
		assertEquals(View.GONE, solo.getView(R.id.config_modal)
				.getVisibility());
	}

	public void test02_運行管理ボタンを押したら表示() {
		test01_起動時は非表示();

		solo.clickOnButton("運行管理");
		getInstrumentation().waitForIdleSync();

		assertEquals(View.VISIBLE, solo.getView(R.id.config_modal)
				.getVisibility());
	}

	public void test03_戻るボタンを押したら消える() {

		test01_起動時は非表示();

		solo.clickOnButton("運行管理");
		getInstrumentation().waitForIdleSync();

		assertEquals(View.VISIBLE, solo.getView(R.id.config_modal)
				.getVisibility());

		solo.clickOnView(solo.getView(R.id.config_hide_button));

		getInstrumentation().waitForIdleSync();

		assertEquals(View.GONE, solo.getView(R.id.config_modal)
				.getVisibility());
	}

	public void test04_一回閉じてからもう一度運行管理ボタンを押したら表示() {
		test03_戻るボタンを押したら消える();
		solo.clickOnView(solo.getView(R.id.config_button));

		getInstrumentation().waitForIdleSync();

		assertEquals(View.VISIBLE, solo.getView(R.id.config_modal)
				.getVisibility());
	}

	public void test05_中止ボタンを押すと中止確認画面が表示() {
		test02_運行管理ボタンを押したら表示();
		assertEquals(View.GONE, solo.getView(R.id.stop_check_modal)
				.getVisibility());
		solo.clickOnView(solo.getView(R.id.stop_check_button));

		getInstrumentation().waitForIdleSync();

		assertEquals(View.VISIBLE, solo.getView(R.id.stop_check_modal)
				.getVisibility());
	}

	public void test06_中止確認画面で中止ボタンを押すと中止画面が表示() {
		test05_中止ボタンを押すと中止確認画面が表示();
		assertEquals(View.GONE, solo.getView(R.id.stop_modal).getVisibility());
		solo.clickOnView(solo.getView(R.id.stop_button));

		getInstrumentation().waitForIdleSync();

		assertEquals(View.VISIBLE, solo.getView(R.id.stop_modal)
				.getVisibility());
	}

	public void test07_中止確認画面で戻るボタンを押すと中止確認が消える() {
		test05_中止ボタンを押すと中止確認画面が表示();

		solo.clickOnButton("戻る");

		getInstrumentation().waitForIdleSync();

		assertEquals(View.GONE, solo.getView(R.id.stop_check_modal)
				.getVisibility());

	}

	public void test08_一時停止ボタンを押すと一時停止画面が表示() {

		test02_運行管理ボタンを押したら表示();
		assertEquals(View.GONE, solo.getView(R.id.pause_modal)
				.getVisibility());
		solo.clickOnView(solo.getView(R.id.pause_button));

		getInstrumentation().waitForIdleSync();

		assertEquals(View.VISIBLE, solo.getView(R.id.pause_modal)
				.getVisibility());
	}

	public void test09_一時停止画面で運行再開ボタンを押すと一時停止画面と設定画面が非表示() {

		test02_運行管理ボタンを押したら表示();

		solo.clickOnView(solo.getView(R.id.pause_button));

		getInstrumentation().waitForIdleSync();

		assertEquals(View.VISIBLE, solo.getView(R.id.pause_modal)
				.getVisibility());

		solo.clickOnButton("運行を再開する");

		getInstrumentation().waitForIdleSync();

		assertEquals(View.GONE, solo.getView(R.id.config_modal)
				.getVisibility());

		assertEquals(View.GONE, solo.getView(R.id.pause_modal)
				.getVisibility());
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
	}
}