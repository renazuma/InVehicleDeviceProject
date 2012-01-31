package com.kogasoftware.odt.invehicledevice.test;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.TextView;

import com.jayway.android.robotium.solo.Solo;
import com.kogasoftware.odt.invehicledevice.InVehicleDeviceActivity;
import com.kogasoftware.odt.invehicledevice.R;

public class WaitingTestCase extends
		ActivityInstrumentationTestCase2<InVehicleDeviceActivity> {

	private Solo solo;

	public WaitingTestCase() {
		super("com.kogasoftware.odt.invehicledevice",
				InVehicleDeviceActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());

		// デフォルトで停車中にする
		solo.clickOnButton("到着");
	}

	public void testメモボタンを押すとメモ画面が表示() {
		assertEquals(View.GONE, solo.getView(R.id.memo_overlay).getVisibility());
		solo.clickOnButton("メモ");
		assertEquals(View.VISIBLE, solo.getView(R.id.memo_overlay)
				.getVisibility());
	}

	public void testメモ画面で閉じるボタンを押すとメモ画面が非表示() {
		testメモボタンを押すとメモ画面が表示();
		solo.clickOnView(solo.getView(R.id.memo_close_button));
		assertEquals(View.GONE, solo.getView(R.id.memo_overlay).getVisibility());
	}

	public void test復路ボタンを押すと復路画面が表示() {
		assertEquals(View.GONE, solo.getView(R.id.return_path_overlay)
				.getVisibility());
		solo.clickOnButton("復路");
		assertEquals(View.VISIBLE, solo.getView(R.id.return_path_overlay)
				.getVisibility());
	}

	public void test復路画面で閉じるボタンを押すと復路画面が非表示() {
		test復路ボタンを押すと復路画面が表示();
		solo.clickOnView(solo.getView(R.id.return_path_close_button));
		assertEquals(View.GONE, solo.getView(R.id.return_path_overlay)
				.getVisibility());
	}

	public void test出発ボタンを押すと出発ダイアログ表示() {
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

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
	}
}