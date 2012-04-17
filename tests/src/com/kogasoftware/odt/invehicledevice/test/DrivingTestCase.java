package com.kogasoftware.odt.invehicledevice.test;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.TextView;

import com.jayway.android.robotium.solo.Solo;
import com.kogasoftware.odt.invehicledevice.InVehicleDeviceActivity;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.datasource.DataSourceFactory;
import com.kogasoftware.odt.invehicledevice.datasource.DummyDataSource;
import com.kogasoftware.odt.invehicledevice.logic.Logic;

public class DrivingTestCase extends
ActivityInstrumentationTestCase2<InVehicleDeviceActivity> {

	private Solo solo;

	public DrivingTestCase() {
		super("com.kogasoftware.odt.invehicledevice",
				InVehicleDeviceActivity.class);
		DataSourceFactory.setInstance(new DummyDataSource());
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		Logic.clearStatusFile();
		
		solo = new Solo(getInstrumentation(), getActivity());
		
		if (solo.searchText("停車中")) {
			
			solo.clickOnButton("出発する");
			getInstrumentation().waitForIdleSync();
			solo.clickOnButton("出発する");
			getInstrumentation().waitForIdleSync();
			
		}
		
	}

	public void test01_起動時は走行中表示() {
		
		assertTrue(solo.searchText("走行中")); // TODO 画像ファイル名assertに書き換わる予定

	}

	public void test02_起動時は出発ダイアログは非表示() {
		assertEquals(View.GONE, solo.getView(R.id.start_check_modal)
				.getVisibility());
	}

	public void test03_到着しましたボタンを押すと停車中表示() {
		test01_起動時は走行中表示();

		solo.clickOnButton("到着しました");

		getInstrumentation().waitForIdleSync();

		assertTrue(solo.searchText("停車中")); // TODO 画像ファイル名assertに書き換わる予定

	}

	public void test04_停車中から出発しますボタンを押すと出発確認画面表示() {
		
		solo.clickOnButton("到着しました");

		getInstrumentation().waitForIdleSync();

		assertTrue(solo.searchText("停車中")); // TODO 画像ファイル名assertに書き換わる予定

		solo.clickOnButton("出発する");

		getInstrumentation().waitForIdleSync();

		assertEquals(View.VISIBLE, solo.getView(R.id.start_check_modal)
				.getVisibility());
		
	}

	public void test05_出発確認画面でやめるボタンを押すと停車中画面表示() {
		test04_停車中から出発しますボタンを押すと出発確認画面表示();

		solo.clickOnView(solo.getView(R.id.stop_cancel_button));
		assertEquals(View.VISIBLE, solo.getView(R.id.waiting_layout)
				.getVisibility());

		getInstrumentation().waitForIdleSync();

		TextView v = (TextView) solo.getView(R.id.phase_text_view);
		assertEquals("停車中", v.getText()); // TODO 画像ファイル名assertに書き換わる予定

	}

	public void test06_出発確認画面で出発するボタンを押すと運転中画面表示() {
		test04_停車中から出発しますボタンを押すと出発確認画面表示();

		solo.clickOnView(solo.getView(R.id.start_button));
		assertEquals(View.VISIBLE, solo.getView(R.id.driving_layout)
				.getVisibility());

		getInstrumentation().waitForIdleSync();

		TextView v = (TextView) solo.getView(R.id.phase_text_view);
		assertEquals("走行中", v.getText()); // TODO 画像ファイル名assertに書き換わる予定

	}

	public void test07_乗降場の表示が遷移する() {

		test01_起動時は走行中表示();

		assertTrue("博物館前",solo.searchText("博物館前"));

		test04_停車中から出発しますボタンを押すと出発確認画面表示();

		assertTrue("御徒町駅前",solo.searchText("御徒町駅前"));

	}

	public void test08_最終乗降場についた時の挙動() {

		// TODO 機能が確定していないので確定後実装する

		test04_停車中から出発しますボタンを押すと出発確認画面表示();

		solo.clickOnView(solo.getView(R.id.start_button));
		assertEquals(View.VISIBLE, solo.getView(R.id.driving_layout)
				.getVisibility());

		getInstrumentation().waitForIdleSync();

		TextView v = (TextView) solo.getView(R.id.phase_text_view);
		assertEquals("走行中", v.getText()); // TODO 画像ファイル名assertに書き換わる予定

	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
	}
}