package com.kogasoftware.odt.invehicledevice.test.integration;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.TextView;

import com.jayway.android.robotium.solo.Solo;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;
import com.kogasoftware.odt.invehicledevice.test.util.datasource.MockDataSource;
import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;
import com.kogasoftware.odt.invehicledevice.ui.modalview.DepartureCheckModalView;
import com.kogasoftware.odt.invehicledevice.ui.modalview.MemoModalView;

public class PlatformTestCase extends
		ActivityInstrumentationTestCase2<InVehicleDeviceActivity> {

	private Solo solo;

	public PlatformTestCase() {
		super("com.kogasoftware.odt.invehicledevice.ui.activity",
				InVehicleDeviceActivity.class);
	}

	public void dataset(Integer i) {

		MockDataSource mds = new MockDataSource();

		mds.setReservation(i);
		mds.setReservationCandidate(6, 1, 1, 1);

		TestUtil.setDataSource(mds);
		TestUtil.setDate("2012-01-01T09:00:00+09:00");
	}

	@Override
	public void setUp() throws Exception {

		super.setUp();

		dataset(6);

		TestUtil.clearStatus();

		solo = new Solo(getInstrumentation(), getActivity());
		assertTrue(TestUtil.waitForStartUI(getActivity()));

		// デフォルトで停車中にする
		if (solo.searchButton("到着しました", true)) {
			solo.clickOnButton("到着しました");
			solo.clickOnButton("到着する");
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
		assertEquals(View.VISIBLE, solo.getView(MemoModalView.class, 0)
				.getVisibility());
		assertTrue(solo.searchText("テストメモ1"));
	}

	public void test02_メモ画面で戻るボタンを押すとメモ画面が非表示() {
		test01_メモボタンを押すとメモ画面が表示();
		solo.clickOnView(solo.getView(R.id.memo_close_button));
		getInstrumentation().waitForIdleSync();
		assertEquals(View.GONE, solo.getView(MemoModalView.class, 0)
				.getVisibility());
	}

	public void test04_出発ボタンを押すと出発ダイアログ表示() {
		solo.clickOnButton("出発する");
		assertEquals(View.VISIBLE, solo
				.getView(DepartureCheckModalView.class, 0).getVisibility());
	}

	public void test05_出発ダイアログはい選択で走行中() {
		test04_出発ボタンを押すと出発ダイアログ表示();
		solo.clickOnView(solo.getView(R.id.departure_button));
		getInstrumentation().waitForIdleSync();
		TextView v = (TextView) solo.getView(R.id.phase_text_view);
		assertEquals("走行中", v.getText());
	}

	public void test06_出発ダイアログいいえ選択で停車中に戻る() {
		test04_出発ボタンを押すと出発ダイアログ表示();
		solo.clickOnView(solo.getView(R.id.departure_check_close_button));
		TextView v = (TextView) solo.getView(R.id.phase_text_view);
		assertEquals("停車中", v.getText());
	}

	public void test09_下スクロール() {

		assertTrue(solo.searchText("名字a", 0, false));
		assertTrue(solo.searchText("1011", 0, false));

		solo.clickOnButton("下へ移動");
		solo.clickOnButton("下へ移動");

		assertFalse(solo.searchText("名字a", 0, false));
		assertFalse(solo.searchText("1011", 0, false));

	}

	public void test10_上スクロール() {

		test09_下スクロール();

		solo.clickOnButton("上へ移動");
		solo.clickOnButton("上へ移動");

		assertTrue(solo.searchText("名字a", 0, false));
		assertTrue(solo.searchText("1011", 0, false));

	}

	public void xtest11_未払ボタンを押すと支払済に変更() { // TODO: 支払UIは瀬戸内では利用しない

		solo.clickOnToggleButton("未払");
		assertTrue(solo.searchToggleButton("支払済"));

	}

	public void xtest12_支払済ボタンを押すと未払に変更() { // TODO: 支払UIは瀬戸内では利用しない

		xtest11_未払ボタンを押すと支払済に変更();

		solo.clickOnToggleButton("支払済");
		assertTrue(solo.searchToggleButton("未払"));

	}

	public void test13_人数が正しく表示されている() {

		assertTrue(solo.searchText("1名"));
		assertTrue(solo.searchText("2名"));

	}

	public void Xtest13_人数を1名から2名に変更() { // TODO: 瀬戸内では人数変更を行わない

		solo.clickOnText("1名");

		solo.clickOnText("2名");

		assertTrue(solo.searchText("2名"));

	}

	public void Xtest14_人数を2名から3名に変更() { // TODO: 瀬戸内では人数変更を行わない

		Xtest13_人数を1名から2名に変更();

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

	public void xtest18_乗客の新規追加ボタンが存在する() { // TODO: 予約の新規追加UIは瀬戸内では利用しない

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

	public void test22_過去の乗降場で未乗車の乗客を表示オンオフ() {

		assertTrue(solo.searchText("過去の乗降場で未乗車の乗客を表示"));

		solo.clickOnText("過去の乗降場で未乗車の乗客を表示");

	}

	public void test21_未来の乗客を表示オンオフ() {

		assertTrue(solo.searchText("未来の乗客を表示"));

		solo.clickOnText("未来の乗客を表示");

	}
}