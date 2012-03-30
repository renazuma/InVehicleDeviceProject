package com.kogasoftware.odt.invehicledevice.test;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jayway.android.robotium.solo.Solo;
import com.kogasoftware.odt.invehicledevice.InVehicleDeviceActivity;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.datasource.DataSourceFactory;
import com.kogasoftware.odt.invehicledevice.test.MockDataSourceTest;

public class ReturnPathTestCase extends
ActivityInstrumentationTestCase2<InVehicleDeviceActivity> {

	private Solo solo;

	public void dataset(Integer i) {

		DataSourceFactory.newInstance();
		MockDataSourceTest mdst = new MockDataSourceTest();

		mdst.setReservation(6);
		mdst.setReservationCandidate(i,1,1,1);

		DataSourceFactory.setInstance(mdst);

	}

	public ReturnPathTestCase() {
		super("com.kogasoftware.odt.invehicledevice",
				InVehicleDeviceActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());

		// デフォルトで復路画面にする
		solo.clickOnButton("到着しました");
		solo.clickOnButton("復路");

	}

	public void test00_データ初期設定() {
		dataset(6);
	}

	public void test01_復路画面で戻るボタンを押すと復路画面が非表示() {

		solo.clickOnView(solo.getView(R.id.return_path_close_button));
		assertEquals(View.GONE, solo.getView(R.id.return_path_modal)
				.getVisibility());
	}

	public void test02_予約候補表示前は予約ボタン非表示() {

		assertFalse(solo.searchButton("予約する",true));

	}

	public void test03_予約候補表示後で予約選択前は予約ボタン使用不可() {

		solo.clickOnButton("予約候補を検索");

		Button reserveButton = (Button) getActivity().findViewById(R.id.return_path_button);

		assertTrue(reserveButton.isEnabled());

	}

	public void test02_復路画面で予約候補を検索を押すと予約候補表示() {

		assertFalse(solo.searchText("乗車時刻"));

		assertEquals(View.VISIBLE, solo.getView(R.id.return_path_modal)
				.getVisibility());

		solo.clickOnButton("予約候補を検索");

		assertTrue(solo.searchText("乗車時刻"));

		assertEquals(View.VISIBLE, solo.getView(R.id.return_path_button)
				.getVisibility());

	}


	public void test03_復路画面で予約を押すと待機中画面へ戻る() {

		test02_復路画面で予約候補を検索を押すと予約候補表示();

		solo.clickOnButton("予約する");
		assertEquals(View.VISIBLE, solo.getView(R.id.waiting_layout)
				.getVisibility());

	}

	public void test04_下スクロール() {

		test02_復路画面で予約候補を検索を押すと予約候補表示();

		assertTrue(solo.searchText("予約名字a", 0,false));

		solo.clickOnButton("下へ移動");

		assertFalse(solo.searchText("名字a", 0,false));

	}

	public void test05_上スクロール() {

		test04_下スクロール();

		solo.clickOnButton("上へ移動");

		assertTrue(solo.searchText("名字a", 0,false));

	}


	/*
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
*/

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
	}
}