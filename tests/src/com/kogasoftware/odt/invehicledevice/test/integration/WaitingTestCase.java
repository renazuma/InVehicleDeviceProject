package com.kogasoftware.odt.invehicledevice.test.integration;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.TextView;

import com.jayway.android.robotium.solo.Solo;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.datasource.DataSourceFactory;
import com.kogasoftware.odt.invehicledevice.test.util.datasource.DummyDataSource;
import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;

public class WaitingTestCase extends
		ActivityInstrumentationTestCase2<InVehicleDeviceActivity> {

	private Solo solo;

	public WaitingTestCase() {
		super("com.kogasoftware.odt.invehicledevice.ui.activity",
				InVehicleDeviceActivity.class);
		DataSourceFactory.setInstance(new DummyDataSource());
	}

	public void dataset(Integer i) {

		MockDataSource mds = new MockDataSource();

		mds.setReservation(i);
		mds.setReservationCandidate(6, 1, 1, 1);

		DataSourceFactory.setInstance(mds);

	}

	@Override
	public void setUp() throws Exception {

		super.setUp();
		assertTrue(false); // TODO: 内部データを修正するまでこのテストはペンディング

		dataset(6);

		CommonLogic.clearStatusFile();

		solo = new Solo(getInstrumentation(), getActivity());

		// デフォルトで停車中にする
		if (solo.searchButton("到着しました")) {
			solo.clickOnButton("到着しました");
		}
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
	}

	public void test00_データ初期設定() {
		dataset(6);

	}

	public void test01_メモボタンを押すとメモ画面が表示() {

		solo.clickOnButton("メモ");
		assertEquals(View.VISIBLE, solo.getView(R.id.memo_modal_view)
				.getVisibility());
		assertTrue(solo.searchText("テストメモ1"));
	}

	public void test02_メモ画面で戻るボタンを押すとメモ画面が非表示() {
		test01_メモボタンを押すとメモ画面が表示();
		solo.clickOnView(solo.getView(R.id.memo_close_button));
		assertEquals(View.GONE, solo.getView(R.id.memo_modal_view)
				.getVisibility());
	}

	public void test03_復路ボタンを押すと復路画面が表示() {
		assertEquals(View.GONE, solo.getView(R.id.return_path_modal_view)
				.getVisibility());
		solo.clickOnButton("復路");
		assertEquals(View.VISIBLE, solo.getView(R.id.return_path_modal_view)
				.getVisibility());
	}

	public void test04_出発ボタンを押すと出発ダイアログ表示() {
		solo.clickOnButton("出発する");
		assertEquals(View.VISIBLE, solo.getView(R.id.start_check_modal_view)
				.getVisibility());
	}

	public void test05_出発ダイアログはい選択で走行中() {
		test04_出発ボタンを押すと出発ダイアログ表示();
		solo.clickOnView(solo.getView(R.id.start_button));
		TextView v = (TextView) solo.getView(R.id.phase_text_view);
		assertEquals("走行中", v.getText());
	}

	public void test06_出発ダイアログいいえ選択で停車中に戻る() {
		test04_出発ボタンを押すと出発ダイアログ表示();
		solo.clickOnView(solo.getView(R.id.start_check_close_button));
		TextView v = (TextView) solo.getView(R.id.phase_text_view);
		assertEquals("停車中", v.getText());
	}

	public void test07_復路画面で予約候補を検索を押すと予約候補表示() {

		test03_復路ボタンを押すと復路画面が表示();

		solo.clickOnButton("予約候補を検索");
		assertEquals(View.VISIBLE, solo.getView(R.id.return_path_modal_view)
				.getVisibility());
		assertEquals(View.VISIBLE, solo.getView(R.id.return_path_button)
				.getVisibility());

	}

	public void test08_復路画面で予約を押すと待機中画面へ戻る() {

		test07_復路画面で予約候補を検索を押すと予約候補表示();

		solo.clickOnButton("予約する");
		assertEquals(View.VISIBLE, solo.getView(R.id.platform_phase_view)
				.getVisibility());

	}

	public void test09_下スクロール() {

		assertTrue(solo.searchText("予約番号11", 0, false));

		solo.clickOnButton("下へ移動");

		assertFalse(solo.searchText("予約番号11", 0, false));

	}

	public void test10_上スクロール() {

		test09_下スクロール();

		solo.clickOnButton("上へ移動");

		assertTrue(solo.searchText("予約番号11", 0, false));

	}

	public void test11_未払ボタンを押すと支払済に変更() {

		solo.clickOnToggleButton("未払");
		assertTrue(solo.searchToggleButton("支払済"));

	}

	public void test12_支払済ボタンを押すと未払に変更() {

		test11_未払ボタンを押すと支払済に変更();

		solo.clickOnToggleButton("支払済");
		assertTrue(solo.searchToggleButton("未払"));

	}

	public void test13_人数を1名から2名に変更() {

		solo.clickOnText("1名");

		solo.clickOnText("2名");

		assertTrue(solo.searchText("2名"));

	}

	public void test14_人数を2名から3名に変更() {

		test13_人数を1名から2名に変更();

		solo.clickOnText("2名");

		solo.clickOnText("3名");

		assertTrue(solo.searchText("3名"));

	}

	public void test15_データ初期設定1件() {
		dataset(1);
	}

	public void test16_件数が少ないため上へ移動ボタンが存在しない() {

		assertFalse(solo.searchButton("上へ移動"));

	}

	public void test17_件数が少ないため下へ移動ボタンが存在しない() {

		assertFalse(solo.searchButton("下へ移動"));

	}

	public void test18_乗客の新規追加ボタンが存在する() {

		assertTrue(solo.searchText("予約の新規追加"));

	}

	public void test19_乗客表示の切り替えボタンが存在する() {

		assertTrue(solo.searchText("乗車中の乗客全員を表示"));
		assertTrue(solo.searchText("未来の乗客を表示"));
		assertTrue(solo.searchText("過去の乗降場で未乗車の乗客を表示"));

	}

	public void test20_乗車中の乗客全員を表示オンオフ() {

		assertTrue(solo.searchText("乗車中の乗客全員を表示"));

		solo.clickOnText("乗車中の乗客全員を表示");

	}
}