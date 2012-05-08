package com.kogasoftware.odt.invehicledevice.test.integration;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.Button;

import com.jayway.android.robotium.solo.Solo;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess;
import com.kogasoftware.odt.invehicledevice.logic.datasource.DataSourceFactory;
import com.kogasoftware.odt.invehicledevice.test.util.datasource.DummyDataSource;
import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;

public class ScheduleTestCase extends
		ActivityInstrumentationTestCase2<InVehicleDeviceActivity> {

	private Solo solo;

	public ScheduleTestCase() {
		super("com.kogasoftware.odt.invehicledevice.ui.activity",
				InVehicleDeviceActivity.class);
		DataSourceFactory.setInstance(new DummyDataSource());
	}

	public void dataset(Integer i) {

		MockDataSource mds = new MockDataSource();

		mds.setOperationSchedules(i);
		DataSourceFactory.setInstance(mds);

	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		StatusAccess.clearSavedFile();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
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
		getInstrumentation().waitForIdleSync();
		assertEquals(View.VISIBLE, solo.getView(R.id.schedule_modal_view)
				.getVisibility());
		assertTrue(solo.searchText("上野御徒町駅前"));
		assertTrue(solo.searchText("上野動物園前"));

	}

	public void test03_戻るボタンを押したら消える() {
		test02_予定ボタンを押したら表示();
		solo.clickOnButton("戻る");

		getInstrumentation().waitForIdleSync();
		assertEquals(View.GONE, solo.getView(R.id.schedule_modal_view)
				.getVisibility());
		
		assertFalse(solo.searchText("上野御徒町駅前", true));
		assertFalse(solo.searchText("上野動物園前", true));
	}

	public void test04_一回閉じてからもう一度予定ボタンを押したら表示() {
		test03_戻るボタンを押したら消える();
		solo.clickOnView(solo.getView(R.id.schedule_button));
		assertEquals(View.VISIBLE, solo.getView(R.id.schedule_modal_view)
				.getVisibility());
	}

	public void test05_予定を表示してから下スクロール() {

		test02_予定ボタンを押したら表示();

		assertTrue(solo.searchText("コガソフト", 0, false));

		solo.clickOnView(solo.getView(R.id.schedule_button));
		assertEquals(View.VISIBLE, solo.getView(R.id.schedule_modal_view)
				.getVisibility());

		solo.clickOnButton("下へ移動");
		solo.clickOnButton("下へ移動");

		assertFalse(solo.searchText("コガソフト", 0, false));

	}

	public void test06_予定を表示してから上スクロール() {

		test05_予定を表示してから下スクロール();

		solo.clickOnView(solo.getView(R.id.schedule_button));
		assertEquals(View.VISIBLE, solo.getView(R.id.schedule_modal_view)
				.getVisibility());

		solo.clickOnButton("上へ移動");

		assertTrue(solo.searchText("コガソフト", 0, false));

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
}