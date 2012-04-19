package com.kogasoftware.odt.invehicledevice.test;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.Button;

import com.jayway.android.robotium.solo.Solo;
import com.kogasoftware.odt.invehicledevice.InVehicleDeviceActivity;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.datasource.DataSourceFactory;
import com.kogasoftware.odt.invehicledevice.datasource.DummyDataSource;
import com.kogasoftware.odt.invehicledevice.logic.Logic;
import com.kogasoftware.odt.invehicledevice.test.MockDataSourceTest;

public class ScheduleTestCase extends
		ActivityInstrumentationTestCase2<InVehicleDeviceActivity> {

	private Solo solo;

	public void dataset(Integer i) {

		DataSourceFactory.newInstance();
		MockDataSourceTest mdst = new MockDataSourceTest();

		mdst.setOperationSchedules(i);
		DataSourceFactory.setInstance(mdst);

	}

	public ScheduleTestCase() {
		super("com.kogasoftware.odt.invehicledevice",
				InVehicleDeviceActivity.class);
		DataSourceFactory.setInstance(new DummyDataSource());
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		Logic.clearStatusFile();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	public void test00_データ初期設定6件() {
		dataset(6);
	}

	public void test01_起動時は非表示() {
		assertEquals("本日の運行予定",
				((Button) solo.getView(R.id.schedule_button)).getText());
		assertEquals(View.GONE, solo.getView(R.id.schedule_modal_view)
				.getVisibility());
	}

	public void test02_予定ボタンを押したら表示() {
		test01_起動時は非表示();
		solo.clickOnView(solo.getView(R.id.schedule_button));
		assertEquals(View.VISIBLE, solo.getView(R.id.schedule_modal_view)
				.getVisibility());
	}

	public void test03_戻るボタンを押したら消える() {
		test02_予定ボタンを押したら表示();
		solo.clickOnButton("戻る");
		
		assertFalse(solo.searchText("コガソフトウェア前"));
		assertFalse(solo.searchText("上野御徒町駅前"));
		//assertEquals(View.GONE, solo.getView(R.id.schedule_modal_view).getVisibility());
		
		
	}

	
	public void test04_一回閉じてからもう一度予定ボタンを押したら表示() {
		test03_戻るボタンを押したら消える();
		solo.clickOnView(solo.getView(R.id.schedule_button));
		assertEquals(View.VISIBLE, solo.getView(R.id.schedule_modal_view)
				.getVisibility());
	}

	public void test05_予定を表示してから下スクロール() {

		test02_予定ボタンを押したら表示();

		assertTrue(solo.searchText("コガソフト", 0,false));

		solo.clickOnView(solo.getView(R.id.schedule_button));
		assertEquals(View.VISIBLE, solo.getView(R.id.schedule_modal_view)
				.getVisibility());

		solo.clickOnButton("下へ移動");

		assertFalse(solo.searchText("コガソフト", 0,false));

	}
/*
	public void test06_予定を表示してから上スクロール() {

		test05_予定を表示してから下スクロール();

		solo.clickOnView(solo.getView(R.id.schedule_button));
		assertEquals(View.VISIBLE, solo.getView(R.id.schedule_modal_view)
				.getVisibility());

		solo.clickOnButton("上へ移動");

		assertTrue(solo.searchText("コガソフト", 0,false));

	}

	public void test07_データ初期設定1件() {
		dataset(1);
	}

	public void test08_件数が少ないため上へ移動ボタンが存在しない() {
		test02_予定ボタンを押したら表示();

		solo.clickOnView(solo.getView(R.id.schedule_button));
		assertEquals(View.VISIBLE, solo.getView(R.id.schedule_modal_view)
				.getVisibility());

		assertFalse(solo.searchButton("上へ移動"));

	}

	public void test09_件数が少ないため下へ移動ボタンが存在しない() {
		test02_予定ボタンを押したら表示();

		solo.clickOnView(solo.getView(R.id.schedule_button));
		assertEquals(View.VISIBLE, solo.getView(R.id.schedule_modal_view)
				.getVisibility());

		assertFalse(solo.searchButton("下へ移動"));

	}
*/

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
	}
}