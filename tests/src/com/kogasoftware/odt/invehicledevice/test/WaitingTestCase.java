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
		solo.clickOnButton("到着しました");
	}

	public void test01メモボタンを押すとメモ画面が表示() {
		assertEquals(View.GONE, solo.getView(R.id.memo_overlay).getVisibility());
		solo.clickOnButton("メモ");
		assertEquals(View.VISIBLE, solo.getView(R.id.memo_overlay)
				.getVisibility());
	}

	public void test02メモ画面で閉じるボタンを押すとメモ画面が非表示() {
		test01メモボタンを押すとメモ画面が表示();
		solo.clickOnView(solo.getView(R.id.memo_close_button));
		assertEquals(View.GONE, solo.getView(R.id.memo_overlay).getVisibility());
	}

	public void test03復路ボタンを押すと復路画面が表示() {
		assertEquals(View.GONE, solo.getView(R.id.return_path_overlay)
				.getVisibility());
		solo.clickOnButton("復路");
		assertEquals(View.VISIBLE, solo.getView(R.id.return_path_overlay)
				.getVisibility());
	}

	public void test04復路画面で戻るボタンを押すと復路画面が非表示() {
		test03復路ボタンを押すと復路画面が表示();
		solo.clickOnView(solo.getView(R.id.return_path_close_button));
		assertEquals(View.GONE, solo.getView(R.id.return_path_overlay)
				.getVisibility());
	}

	public void test05出発ボタンを押すと出発ダイアログ表示() {
		solo.clickOnButton("出発する");
		assertEquals(View.VISIBLE, solo.getView(R.id.check_start_layout)
				.getVisibility());
	}

	public void test06出発ダイアログはい選択で走行中() {
		test05出発ボタンを押すと出発ダイアログ表示();
		solo.clickOnView(solo.getView(R.id.start_button));
		TextView v = (TextView) solo.getView(R.id.status_text_view);
		assertEquals("走行中", v.getText());
	}

	public void test07出発ダイアログいいえ選択() {
		test05出発ボタンを押すと出発ダイアログ表示();
		solo.clickOnView(solo.getView(R.id.start_cancel_button));
		TextView v = (TextView) solo.getView(R.id.status_text_view);
		assertEquals("停車中", v.getText());
	}

	public void test08復路画面で予約候補を検索を押すと予約候補表示() {

		test03復路ボタンを押すと復路画面が表示();

		assertEquals(View.GONE, solo.getView(R.id.return_path_overlay)
				.getVisibility());
		solo.clickOnButton("予約情報を検索");
		assertEquals(View.VISIBLE, solo.getView(R.id.return_path_overlay)
				.getVisibility());
		assertEquals(View.VISIBLE, solo.getView(R.id.return_path_button)
				.getVisibility());

	}

	public void test09復路画面で予約を押すと待機中画面へ戻る() {

		test08復路画面で予約候補を検索を押すと予約候補表示();

		assertEquals(View.GONE, solo.getView(R.id.return_path_overlay)
				.getVisibility());
		solo.clickOnButton("予約する");
		assertEquals(View.VISIBLE, solo.getView(R.id.waiting_layout)
				.getVisibility());

	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
	}
}