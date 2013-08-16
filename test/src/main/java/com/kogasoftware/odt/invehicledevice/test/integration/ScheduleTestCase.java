package com.kogasoftware.odt.invehicledevice.test.integration;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;
import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;
import com.kogasoftware.odt.invehicledevice.test.util.apiclient.DummyApiClient;
import com.kogasoftware.odt.invehicledevice.test.util.apiclient.MockApiClient;
import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;

public class ScheduleTestCase extends
		ActivityInstrumentationTestCase2<InVehicleDeviceActivity> {

	private Solo solo;

	@SuppressWarnings("deprecation")
	public ScheduleTestCase() {
		super("com.kogasoftware.odt.invehicledevice.ui.activity",
				InVehicleDeviceActivity.class);
		TestUtil.setApiClient(new DummyApiClient());
	}

	public void dataset(Integer i) {

		MockApiClient mds = new MockApiClient();

		mds.setOperationSchedules(i);
		TestUtil.setApiClient(mds);

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

	public void xtest00_データ初期設定6件() {
		dataset(6);
	}

	public void xtest01_起動時は非表示() {
//		assertEquals("運行\n予定",
//				((Button) solo.getView(R.id.schedule_button)).getText());
//		assertFalse(solo.waitForView(ScheduleModalView.class, 0, 500));
	}

	public void xtest02_予定ボタンを押したら表示() {
		xtest01_起動時は非表示();
//		solo.clickOnView(solo.getView(R.id.schedule_button));
		getInstrumentation().waitForIdleSync();
//		assertTrue(solo.waitForView(ScheduleModalView.class));
		assertTrue(solo.searchText("テスト上野動物園前"));
		assertTrue(solo.searchText("テスト湯島天神前"));
	}

	public void xtest03_戻るボタンを押したら消える() {
		xtest02_予定ボタンを押したら表示();
		solo.clickOnButton("戻る");

		getInstrumentation().waitForIdleSync();
//		assertFalse(solo.waitForView(ScheduleModalView.class, 0, 500));
		
		assertFalse(solo.searchText("テスト上野動物園前", true));
		assertFalse(solo.searchText("テスト湯島天神前", true));
	}

	public void xtest04_一回閉じてからもう一度予定ボタンを押したら表示() {
		xtest03_戻るボタンを押したら消える();
//		solo.clickOnView(solo.getView(R.id.schedule_button));
//		assertTrue(solo.waitForView(ScheduleModalView.class));
	}

	public void xtest05_予定を表示してから下スクロール() {

		xtest02_予定ボタンを押したら表示();

		assertTrue(solo.searchText("コガソフト", 0, false));

//		solo.clickOnView(solo.getView(R.id.schedule_button));
//		assertTrue(solo.waitForView(ScheduleModalView.class));

		solo.clickOnButton("下へ移動");
		solo.clickOnButton("下へ移動");

		assertFalse(solo.searchText("コガソフト", 0, false));

	}

	public void xtest06_予定を表示してから上スクロール() {

		xtest05_予定を表示してから下スクロール();

//		solo.clickOnView(solo.getView(R.id.schedule_button));
//		assertTrue(solo.waitForView(ScheduleModalView.class));

		solo.clickOnButton("上へ移動");

		assertTrue(solo.searchText("コガソフト", 0, false));

	}

	public void xtest07_データ初期設定1件() {
		dataset(1);
	}

	public void xtest08_件数が少ないため上へ移動ボタンが存在しない() {
		xtest02_予定ボタンを押したら表示();

//		solo.clickOnView(solo.getView(R.id.schedule_button));
//		assertEquals(View.VISIBLE, solo.getView(ScheduleModalView.class, 0)
//				.getVisibility());

		assertFalse(solo.searchButton("上へ移動"));
	}

	public void xtest09_件数が少ないため下へ移動ボタンが存在しない() {
		xtest02_予定ボタンを押したら表示();

//		solo.clickOnView(solo.getView(R.id.schedule_button));
//		assertEquals(View.VISIBLE, solo.getView(ScheduleModalView.class, 0)
//				.getVisibility());

		assertFalse(solo.searchButton("下へ移動"));

	}

}