package com.kogasoftware.odt.invehicledevice.test;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.TextView;

import com.jayway.android.robotium.solo.Solo;
import com.kogasoftware.odt.invehicledevice.InVehicleDeviceActivity;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.datasource.DataSourceFactory;

public class DrivingTestCase extends
		ActivityInstrumentationTestCase2<InVehicleDeviceActivity> {

	private Solo solo;

	public void dataset() {

		DataSourceFactory.newInstance();

		MockDataSourceTest mdst = new MockDataSourceTest();
		DataSourceFactory.setInstance(mdst);

	}

	public DrivingTestCase() {
		super("com.kogasoftware.odt.invehicledevice",
				InVehicleDeviceActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		dataset();
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	public void test01起動時は走行中表示() {
		TextView v = (TextView) solo.getView(R.id.status_text_view);
		assertEquals("走行中", v.getText()); // TODO 画像ファイル名assertに書き換わる予定

	}

	public void test02起動時は出発ダイアログは非表示() {
		assertEquals(View.GONE, solo.getView(R.id.check_start_layout)
				.getVisibility());
	}

	public void test03到着しましたボタンを押すと停車中表示() {
		test01起動時は走行中表示();

		TextView v = (TextView) solo.getView(R.id.status_text_view);
		solo.clickOnButton("到着しました");
		assertEquals("停車中", v.getText()); // TODO 画像ファイル名assertに書き換わる予定
	}

	public void test04停車中から出発しますボタンを押すと出発確認画面表示() {
		test03到着しましたボタンを押すと停車中表示();

		solo.clickOnButton("出発する");
		assertEquals(View.VISIBLE, solo.getView(R.id.check_start_layout)
				.getVisibility());
	}

	public void test05出発確認画面でやめるボタンを押すと待機中画面表示() {
		test04停車中から出発しますボタンを押すと出発確認画面表示();

		solo.clickOnButton("やめる");
		assertEquals(View.VISIBLE, solo.getView(R.id.waiting_layout)
				.getVisibility());
	}

	public void test06出発確認画面で出発するボタンを押すと運転中画面表示() {
		test04停車中から出発しますボタンを押すと出発確認画面表示();

		solo.clickOnButton("出発する");
		assertEquals(View.VISIBLE, solo.getView(R.id.driving_layout)
				.getVisibility());
		TextView v = (TextView) solo.getView(R.id.status_text_view);
		assertEquals("走行中", v.getText()); // TODO 画像ファイル名assertに書き換わる予定

	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
	}
}