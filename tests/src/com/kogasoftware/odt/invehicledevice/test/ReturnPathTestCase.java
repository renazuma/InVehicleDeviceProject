package com.kogasoftware.odt.invehicledevice.test;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.Button;

import com.jayway.android.robotium.solo.Solo;
import com.kogasoftware.odt.invehicledevice.InVehicleDeviceActivity;
import com.kogasoftware.odt.invehicledevice.InVehicleDeviceLogic;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.datasource.DataSourceFactory;

public class ReturnPathTestCase extends
ActivityInstrumentationTestCase2<InVehicleDeviceActivity> {

	private Solo solo;

	public void dataset(Integer iCount,Integer userId,
			Integer departurePlatformId, Integer arrivalPlatformId) {

		DataSourceFactory.newInstance();
		MockDataSourceTest mdst = new MockDataSourceTest();

		mdst.setReservation(6);
		mdst.setReservationCandidate(iCount,userId,departurePlatformId,arrivalPlatformId);

		DataSourceFactory.setInstance(mdst);

	}

	public ReturnPathTestCase() {
		super("com.kogasoftware.odt.invehicledevice",
				InVehicleDeviceActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		InVehicleDeviceLogic.clearStatusFile();
		solo = new Solo(getInstrumentation(), getActivity());

		// デフォルトで復路画面にする
		solo.clickOnButton("到着しました");
		solo.clickOnButton("復路");
		System.out.println("セットアップ");

	}

	public void test00_データ初期設定() {
		// TODO userId,departurePlatformId,arrivalPlatformId部分は後で茂木さんの実装が出来たら実装する
		dataset(6,1,1,1);

	}

	public void test01_復路画面で戻るボタンを押すと復路画面が非表示() {

		assertEquals(View.VISIBLE, solo.getView(R.id.return_path_modal)
				.getVisibility());

		solo.clickOnButton("戻る");

		assertEquals(View.GONE, solo.getView(R.id.return_path_modal)
				.getVisibility());
	}

	public void test02_予約候補表示前は予約ボタン非表示() {

		assertFalse(solo.searchButton("予約する",true));

	}

	public void test03_復路画面で予約候補を検索を押すと予約候補表示() {

		assertFalse(solo.searchText("乗車時刻"));

		solo.clickOnButton("予約候補を検索");
		assertEquals(View.VISIBLE, solo.getView(R.id.return_path_modal)
				.getVisibility());

		assertTrue(solo.searchText("乗車時刻"));
		assertTrue(solo.searchText("13時"));
		assertTrue(solo.searchText("34分"));

	}

	public void test04_予約候補表示後で予約選択前は予約ボタン使用不可() {

		test03_復路画面で予約候補を検索を押すと予約候補表示();

		Button reserveButton = (Button) getActivity().findViewById(R.id.return_path_button);

		assertFalse(reserveButton.isEnabled());

	}

	public void test05_復路画面で予約候補をタッチすると予約ボタンが使用可能になる() {

		test03_復路画面で予約候補を検索を押すと予約候補表示();

		solo.clickOnText("15時");

		Button reserveButton = (Button) getActivity().findViewById(R.id.return_path_button);

		assertTrue(reserveButton.isEnabled());


	}

	public void test06_復路画面で予約を押すと待機中画面へ戻る() {

		test03_復路画面で予約候補を検索を押すと予約候補表示();

		solo.clickOnText("15時");

		solo.clickOnButton("予約する");
		assertEquals(View.VISIBLE, solo.getView(R.id.waiting_layout)
				.getVisibility());

	}

	public void test07_復路画面の予約条件時間変更() {

		solo.clickOnText("9");

		solo.clickOnText("14");

		assertTrue(solo.searchText("14"));

	}

	public void test08_復路画面の予約条件分変更() {

		solo.clickOnText("0",2);

		solo.clickOnText("51");

		assertTrue(solo.searchText("51"));

	}

	public void test09_復路画面の予約条件乗車変更() {

		solo.clickOnText("乗車");

		solo.clickOnText("降車");

		assertTrue(solo.searchText("降車"));

	}

	public void test10_下スクロール() {

		// TODO
		assertTrue(solo.searchText("13時", 0,false));

		solo.clickOnButton("下へ移動");

		assertFalse(solo.searchText("13時", 0,false));

	}

	public void test11_上スクロール() {

		test10_下スクロール();

		solo.clickOnButton("上へ移動");

		assertTrue(solo.searchText("13時", 0,false));

	}

	public void test12_データ初期設定1件() {

		// TODO userId,departurePlatformId,arrivalPlatformId部分は後で茂木さんの実装が出来たら実装する
		dataset(1,1,1,1);
	}

	public void test13_件数が少ないため上へ移動ボタンが存在しない() {

		assertFalse(solo.searchButton("上へ移動"));

	}

	public void test14_件数が少ないため下へ移動ボタンが存在しない() {

		assertFalse(solo.searchButton("下へ移動"));

	}

	public void test15_データ初期設定() {
		// TODO userId,departurePlatformId,arrivalPlatformId部分は後で茂木さんの実装が出来たら実装する
		dataset(6,1,1,1);
	}

	public void test16_条件により予約候補表示が変更される() {

		// TODO 茂木さんが実装した記述する
		assertFalse(solo.searchText("乗車時刻"));

		solo.clickOnButton("予約候補を検索");
		assertEquals(View.VISIBLE, solo.getView(R.id.return_path_modal)
				.getVisibility());

		assertTrue(solo.searchText("乗車時刻"));
		assertTrue(solo.searchText("13時"));
		assertTrue(solo.searchText("34分"));

	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
		System.out.println("ダウン");

	}
}