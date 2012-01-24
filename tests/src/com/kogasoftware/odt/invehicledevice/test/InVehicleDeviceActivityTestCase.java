package com.kogasoftware.odt.invehicledevice.test;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.TextView;

import com.jayway.android.robotium.solo.Solo;
import com.kogasoftware.odt.invehicledevice.InVehicleDeviceActivity;
import com.kogasoftware.odt.invehicledevice.R;

public class InVehicleDeviceActivityTestCase extends
		ActivityInstrumentationTestCase2<InVehicleDeviceActivity> {

	private Solo solo;

	public InVehicleDeviceActivityTestCase() {
		super("com.kogasoftware.odt.invehicledevice",
				InVehicleDeviceActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
	}

	public void test起動時は走行中表示() {
		TextView v = (TextView) solo.getView(R.id.status_text_view);
		assertEquals("走行中", v.getText()); // TODO 画像ファイル名assertに書き換わる予定
	}

	public void test起動時は出発ダイアログは非表示() {
		assertEquals(View.GONE, solo.getView(R.id.check_start_layout)
				.getVisibility());
	}

	public void test出発ボタンを押すと出発ダイアログ表示() {
		test起動時は出発ダイアログは非表示();
		test到着しましたボタンを押すと停車中表示();
		solo.clickOnButton("出発します");
		assertEquals(View.VISIBLE, solo.getView(R.id.check_start_layout)
				.getVisibility());
	}

	public void test出発ダイアログはい選択で走行中() {
		test出発ボタンを押すと出発ダイアログ表示();
		solo.clickOnView(solo.getView(R.id.start_button));
		TextView v = (TextView) solo.getView(R.id.status_text_view);
		assertEquals("走行中", v.getText());
	}

	public void test出発ダイアログいいえ選択() {
		test出発ボタンを押すと出発ダイアログ表示();
		solo.clickOnView(solo.getView(R.id.start_cancel_button));
		TextView v = (TextView) solo.getView(R.id.status_text_view);
		assertEquals("停車中", v.getText());
	}

	public void test到着しましたボタンを押すと停車中表示() {
		test起動時は走行中表示();

		TextView v = (TextView) solo.getView(R.id.status_text_view);
		solo.clickOnButton("到着しました");
		assertEquals("停車中", v.getText()); // TODO 画像ファイル名assertに書き換わる予定
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
	}
}