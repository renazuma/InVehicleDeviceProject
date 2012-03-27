package com.kogasoftware.odt.invehicledevice.test;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.Button;

import com.jayway.android.robotium.solo.Solo;
import com.kogasoftware.odt.invehicledevice.InVehicleDeviceActivity;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.datasource.DataSourceFactory;
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
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	public void test01起動時は非表示() {
		assertEquals("本日の運行予定",
				((Button) solo.getView(R.id.schedule_button)).getText());
		assertEquals(View.GONE, solo.getView(R.id.schedule_modal)
				.getVisibility());
	}

	public void test02予定ボタンを押したら表示() {
		dataset(1);
		test01起動時は非表示();
		solo.clickOnView(solo.getView(R.id.schedule_button));
		assertEquals(View.VISIBLE, solo.getView(R.id.schedule_modal)
				.getVisibility());
	}

	public void test03戻るボタンを押したら消える() {
		test02予定ボタンを押したら表示();
		solo.clickOnButton("戻る");
		assertEquals(View.GONE, solo.getView(R.id.schedule_modal).getVisibility());
	}

	public void test04一回閉じてからもう一度予定ボタンを押したら表示() {
		test03戻るボタンを押したら消える();
		solo.clickOnView(solo.getView(R.id.schedule_button));
		assertEquals(View.VISIBLE, solo.getView(R.id.schedule_modal)
				.getVisibility());
	}

	public void test05予定を表示してから上スクロール() {
		test02予定ボタンを押したら表示();
		// TODO 実装されたら修正
		solo.clickOnView(solo.getView(R.id.schedule_button));
		assertEquals(View.VISIBLE, solo.getView(R.id.schedule_modal)
				.getVisibility());
	}

	public void test06予定を表示してから下スクロール() {

		test02予定ボタンを押したら表示();
		// TODO 実装されたら修正
		solo.clickOnView(solo.getView(R.id.schedule_button));
		assertEquals(View.VISIBLE, solo.getView(R.id.schedule_modal)
				.getVisibility());
	}

	public void test07二回目の予定ボタン押下で表示() {
		test02予定ボタンを押したら表示();
		solo.clickOnView(solo.getView(R.id.config_button));
		test02予定ボタンを押したら表示();
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
	}
}