package com.kogasoftware.odt.invehicledevice.test.integration;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.Button;

import com.jayway.android.robotium.solo.Solo;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;
import com.kogasoftware.odt.invehicledevice.test.util.datasource.DummyDataSource;
import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;
import com.kogasoftware.odt.invehicledevice.ui.modalview.ScheduleModalView;

public class ScheduleTestCase extends
		ActivityInstrumentationTestCase2<InVehicleDeviceActivity> {

	private Solo solo;

	public ScheduleTestCase() {
		super("com.kogasoftware.odt.invehicledevice.ui.activity",
				InVehicleDeviceActivity.class);
		TestUtil.setDataSource(new DummyDataSource());
	}

	public void dataset(Integer i) {

		MockDataSource mds = new MockDataSource();

		mds.setOperationSchedules(i);
		TestUtil.setDataSource(mds);

	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		TestUtil.clearStatus();
		solo = new Solo(getInstrumentation(), getActivity());
		assertTrue(TestUtil.waitForStartUI(getActivity()));
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
		assertEquals("運行\n予定",
				((Button) solo.getView(R.id.schedule_button)).getText());
		assertFalse(solo.waitForView(ScheduleModalView.class, 0, 500));
	}

	public void test02_予定ボタンを押したら表示() {
		test01_起動時は非表示();
		solo.clickOnView(solo.getView(R.id.schedule_button));
		getInstrumentation().waitForIdleSync();
		assertTrue(solo.waitForView(ScheduleModalView.class));
		assertTrue(solo.searchText("テスト上野動物園前"));
		assertTrue(solo.searchText("テスト湯島天神前"));
	}

	public void test03_戻るボタンを押したら消える() {
		test02_予定ボタンを押したら表示();
		solo.clickOnButton("戻る");

		getInstrumentation().waitForIdleSync();
		assertFalse(solo.waitForView(ScheduleModalView.class, 0, 500));
		
		assertFalse(solo.searchText("テスト上野動物園前", true));
		assertFalse(solo.searchText("テスト湯島天神前", true));
	}

	public void test04_一回閉じてからもう一度予定ボタンを押したら表示() {
		test03_戻るボタンを押したら消える();
		solo.clickOnView(solo.getView(R.id.schedule_button));
		assertTrue(solo.waitForView(ScheduleModalView.class));
	}

	public void test05_予定を表示してから下スクロール() {

		test02_予定ボタンを押したら表示();

		assertTrue(solo.searchText("コガソフト", 0, false));

		solo.clickOnView(solo.getView(R.id.schedule_button));
		assertTrue(solo.waitForView(ScheduleModalView.class));

		solo.clickOnButton("下へ移動");
		solo.clickOnButton("下へ移動");

		assertFalse(solo.searchText("コガソフト", 0, false));

	}

	public void test06_予定を表示してから上スクロール() {

		test05_予定を表示してから下スクロール();

		solo.clickOnView(solo.getView(R.id.schedule_button));
		assertTrue(solo.waitForView(ScheduleModalView.class));

		solo.clickOnButton("上へ移動");

		assertTrue(solo.searchText("コガソフト", 0, false));

	}

	public void test07_データ初期設定1件() {
		dataset(1);
	}

	public void test08_件数が少ないため上へ移動ボタンが存在しない() {
		test02_予定ボタンを押したら表示();

		solo.clickOnView(solo.getView(R.id.schedule_button));
		assertEquals(View.VISIBLE, solo.getView(ScheduleModalView.class, 0)
				.getVisibility());

		assertFalse(solo.searchButton("上へ移動"));
	}

	public void test09_件数が少ないため下へ移動ボタンが存在しない() {
		test02_予定ボタンを押したら表示();

		solo.clickOnView(solo.getView(R.id.schedule_button));
		assertEquals(View.VISIBLE, solo.getView(ScheduleModalView.class, 0)
				.getVisibility());

		assertFalse(solo.searchButton("下へ移動"));

	}

}