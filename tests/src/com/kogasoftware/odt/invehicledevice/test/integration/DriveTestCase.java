package com.kogasoftware.odt.invehicledevice.test.integration;

import jp.tomorrowkey.android.vtextviewer.VTextView;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.TextView;

import com.jayway.android.robotium.solo.Solo;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;
import com.kogasoftware.odt.invehicledevice.test.util.datasource.DummyDataSource;
import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;

public class DriveTestCase extends
		ActivityInstrumentationTestCase2<InVehicleDeviceActivity> {

	private Solo solo;

	public DriveTestCase() {
		super("com.kogasoftware.odt.invehicledevice.ui.activity",
				InVehicleDeviceActivity.class);
		TestUtil.setDataSource(new DummyDataSource());
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		TestUtil.clearStatus();

		solo = new Solo(getInstrumentation(), getActivity());
		assertTrue(TestUtil.waitForStartUi(getActivity()));
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
	}

	public void test01_起動時は走行中表示() {

		assertTrue(solo.searchText("走行中")); // TODO 画像ファイル名assertに書き換わる予定
	}

	public void test02_起動時は出発ダイアログは非表示() {
		assertEquals(View.GONE, solo.getView(R.id.departure_check_modal_view)
				.getVisibility());
	}

	public void test03_到着しましたボタンを押すと停車中表示() {
		test01_起動時は走行中表示();

		solo.clickOnView(solo.getView(R.id.change_phase_button));

		solo.clickOnView(solo.getView(R.id.arrival_button));
		
		assertTrue(solo.searchText("停車中")); // TODO 画像ファイル名assertに書き換わる予定

	}

	public void test04_停車中から出発しますボタンを押すと出発確認画面表示() {

		solo.clickOnView(solo.getView(R.id.change_phase_button));
		
		solo.clickOnView(solo.getView(R.id.arrival_button));
		
		assertTrue(solo.searchText("停車中"));

		solo.clickOnView(solo.getView(R.id.change_phase_button));
		
		TestUtil.willShow(solo, R.id.departure_check_modal_view);
	}

	public void test05_出発確認画面でやめるボタンを押すと停車中画面表示() {
		test04_停車中から出発しますボタンを押すと出発確認画面表示();

		solo.clickOnView(solo.getView(R.id.departure_check_close_button));
		TestUtil.willShow(solo, R.id.platform_phase_view);
		assertTrue(solo.searchText("停車中", true));
		assertFalse(solo.searchText("走行中", true));
	}

	public void test06_出発確認画面で出発するボタンを押すと運転中画面表示() {
		test04_停車中から出発しますボタンを押すと出発確認画面表示();

		solo.clickOnView(solo.getView(R.id.departure_button));

		TestUtil.willShow(solo, R.id.drive_phase_view);
		assertFalse(solo.searchText("停車中", true));
		assertTrue(solo.searchText("走行中", true));

	}

	public void test07_乗降場の表示が遷移する() {

		test01_起動時は走行中表示();

		VTextView next1 = (VTextView) solo.getView(R.id.platform_name_1_beyond_text_view);
		VTextView next2 = (VTextView) solo.getView(R.id.platform_name_2_beyond_text_view);
		VTextView next3 = (VTextView) solo.getView(R.id.platform_name_3_beyond_text_view);

		assertTrue(solo.searchText("乗降場A", true));
		assertEquals("乗降場A", next1.getText());
		assertEquals("乗降場B", next2.getText());
		assertEquals("乗降場C", next3.getText());
		
		test06_出発確認画面で出発するボタンを押すと運転中画面表示();

		assertFalse(solo.searchText("乗降場A", true));
		assertTrue(solo.searchText("乗降場B", true));
		assertEquals("乗降場B", next1.getText());
		assertEquals("乗降場C", next2.getText());
		assertEquals("", next3.getText());

	}

	public void test08_最終乗降場についた時の挙動() {
		for (Integer i = 0; i < 2; ++i) {
			test06_出発確認画面で出発するボタンを押すと運転中画面表示();

			if (solo.searchText("乗降場C")) {
				break;
			}
		}

		solo.clickOnView(solo.getView(R.id.change_phase_button));
		solo.clickOnView(solo.getView(R.id.arrival_button));
		solo.clickOnView(solo.getView(R.id.change_phase_button));
		TestUtil.willShow(solo, R.id.departure_check_modal_view);

		solo.clickOnView(solo.getView(R.id.departure_button));

		TestUtil.willShow(solo, R.id.finish_phase_view);
		TextView v = (TextView) solo.getView(R.id.phase_text_view);
		assertEquals("", v.getText());
	}
}
