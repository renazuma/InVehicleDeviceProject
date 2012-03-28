package com.kogasoftware.odt.invehicledevice.test;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.TextView;

import com.jayway.android.robotium.solo.Solo;
import com.kogasoftware.odt.invehicledevice.InVehicleDeviceActivity;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.datasource.DataSourceFactory;
import com.kogasoftware.odt.invehicledevice.test.MockDataSourceTest;

public class WaitingTestCase extends
ActivityInstrumentationTestCase2<InVehicleDeviceActivity> {

	private Solo solo;

	public void dataset(Integer i) {

		DataSourceFactory.newInstance();
		MockDataSourceTest mdst = new MockDataSourceTest();

		mdst.setReservation(i);
		DataSourceFactory.setInstance(mdst);

	}

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

	public void test00データ初期設定() {
		dataset(6);
	}

	public void test01メモボタンを押すとメモ画面が表示() {
		assertEquals(View.GONE, solo.getView(R.id.memo_modal).getVisibility());
		solo.clickOnView(solo.getView(R.id.memo_button));
		assertEquals(View.VISIBLE, solo.getView(R.id.memo_modal)
				.getVisibility());
		assertTrue(solo.searchText("テストメモ1"));
	}

	public void test02メモ画面で閉じるボタンを押すとメモ画面が非表示() {
		test01メモボタンを押すとメモ画面が表示();
		solo.clickOnView(solo.getView(R.id.memo_close_button));
		assertEquals(View.GONE, solo.getView(R.id.memo_modal).getVisibility());
	}

	public void test03復路ボタンを押すと復路画面が表示() {
		assertEquals(View.GONE, solo.getView(R.id.return_path_modal)
				.getVisibility());
		solo.clickOnButton("復路");
		assertEquals(View.VISIBLE, solo.getView(R.id.return_path_modal)
				.getVisibility());
	}

	public void test04復路画面で戻るボタンを押すと復路画面が非表示() {
		test03復路ボタンを押すと復路画面が表示();
		solo.clickOnView(solo.getView(R.id.return_path_close_button));
		assertEquals(View.GONE, solo.getView(R.id.return_path_modal)
				.getVisibility());
	}

	public void test05出発ボタンを押すと出発ダイアログ表示() {
		solo.clickOnButton("出発する");
		assertEquals(View.VISIBLE, solo.getView(R.id.start_check_modal)
				.getVisibility());
	}

	public void test06出発ダイアログはい選択で走行中() {
		test05出発ボタンを押すと出発ダイアログ表示();
		solo.clickOnView(solo.getView(R.id.start_button));
		TextView v = (TextView) solo.getView(R.id.status_text_view);
		assertEquals("走行中", v.getText());
	}

	public void test07出発ダイアログいいえ選択で停車中に戻る() {
		test05出発ボタンを押すと出発ダイアログ表示();
		solo.clickOnView(solo.getView(R.id.start_cancel_button));
		TextView v = (TextView) solo.getView(R.id.status_text_view);
		assertEquals("停車中", v.getText());
	}

	public void test08復路画面で予約候補を検索を押すと予約候補表示() {

		test03復路ボタンを押すと復路画面が表示();

		solo.clickOnButton("予約候補を検索");
		assertEquals(View.VISIBLE, solo.getView(R.id.return_path_modal)
				.getVisibility());
		assertEquals(View.VISIBLE, solo.getView(R.id.return_path_button)
				.getVisibility());

	}

	public void test09復路画面で予約を押すと待機中画面へ戻る() {

		test08復路画面で予約候補を検索を押すと予約候補表示();

		solo.clickOnButton("予約する");
		assertEquals(View.VISIBLE, solo.getView(R.id.waiting_layout)
				.getVisibility());

	}

	public void test10下スクロール() {

		solo.clickOnButton("下へ移動");

		assertFalse(solo.searchText("テストa", 0,false));
		assertTrue(solo.searchText("テストf", 0,false));

	}

	public void test11上スクロール() {

		test10下スクロール();

		solo.clickOnButton("上へ移動");

		assertTrue(solo.searchText("テストa", 0,false));
		assertFalse(solo.searchText("テストf", 0,false));

	}

	public void test12未払ボタンを押すと支払済に変更() {

		solo.clickOnToggleButton("未払");
		assertTrue(solo.searchToggleButton("支払済"));

	}

	public void test13支払済ボタンを押すと未払に変更() {

		test12未払ボタンを押すと支払済に変更();

		solo.clickOnToggleButton("支払済");
		assertTrue(solo.searchToggleButton("未払"));

	}

	public void test14人数を1名から2名に変更() {

		solo.clickOnText("1名");

		solo.clickOnText("2名");

		assertTrue(solo.searchText("2名"));

	}

	public void test15人数を2名から3名に変更() {

		test14人数を1名から2名に変更();

		solo.clickOnText("2名");

		solo.clickOnText("3名");

		assertTrue(solo.searchText("3名"));

	}

	public void test16復路画面の予約条件時間変更() {

		test03復路ボタンを押すと復路画面が表示();

		solo.clickOnText("9");

		solo.clickOnText("14");

		assertTrue(solo.searchText("14"));

	}

	public void test17復路画面の予約条件分変更() {

		test03復路ボタンを押すと復路画面が表示();

		solo.clickOnText("0",2);

		solo.clickOnText("51");

		assertTrue(solo.searchText("51"));

	}

	public void test18復路画面の予約条件乗車変更() {

		test03復路ボタンを押すと復路画面が表示();

		solo.clickOnText("乗車");

		solo.clickOnText("降車");

		assertTrue(solo.searchText("降車"));

	}

	public void test19データ初期設定1件() {
		dataset(1);
	}

	public void test20件数が少ないため上へ移動ボタンが存在しない() {

		assertFalse(solo.searchButton("上へ移動"));

	}

	public void test21件数が少ないため下へ移動ボタンが存在しない() {

		assertFalse(solo.searchButton("下へ移動"));

	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
	}
}