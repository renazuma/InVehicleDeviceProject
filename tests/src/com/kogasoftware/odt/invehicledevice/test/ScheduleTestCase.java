package com.kogasoftware.odt.invehicledevice.test;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.Button;

import com.jayway.android.robotium.solo.Solo;
import com.kogasoftware.odt.invehicledevice.InVehicleDeviceActivity;
import com.kogasoftware.odt.invehicledevice.R;

public class ScheduleTestCase extends
		ActivityInstrumentationTestCase2<InVehicleDeviceActivity> {

	private Solo solo;

	public ScheduleTestCase() {
		super("com.kogasoftware.odt.invehicledevice",
				InVehicleDeviceActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	public void test起動時は非表示() {
		assertEquals("予定",
				((Button) solo.getView(R.id.schedule_button)).getText());
		assertEquals(View.GONE, solo.getView(R.id.schedule_layout)
				.getVisibility());
	}

	public void test予定ボタンを押したら表示() {
		test起動時は非表示();
		solo.clickOnView(solo.getView(R.id.schedule_button));
		assertEquals("予定",
				((Button) solo.getView(R.id.schedule_button)).getText());
		assertEquals(View.VISIBLE, solo.getView(R.id.schedule_layout)
				.getVisibility());
	}

	public void test予定ボタンを2回押したら非表示() {
		test予定ボタンを押したら表示();
		solo.clickOnView(solo.getView(R.id.schedule_button));
		assertEquals("予定",
				((Button) solo.getView(R.id.schedule_button)).getText());
		assertEquals(View.GONE, solo.getView(R.id.schedule_layout)
				.getVisibility());
	}

	public void test予定ボタンを3回押したら表示() {
		test予定ボタンを2回押したら非表示();
		solo.clickOnView(solo.getView(R.id.schedule_button));

		assertEquals("予定",
				((Button) solo.getView(R.id.schedule_button)).getText());
		assertEquals(View.VISIBLE, solo.getView(R.id.schedule_layout)
				.getVisibility());
	}

	public void test到着しましたボタンを押したら非表示() {
		test予定ボタンを押したら表示();
		solo.clickOnView(solo.getView(R.id.change_status_button));
		assertEquals("予定",
				((Button) solo.getView(R.id.schedule_button)).getText());
		assertEquals(View.GONE, solo.getView(R.id.schedule_layout)
				.getVisibility());
	}

	public void test出発しますボタンを押したら非表示() {
		solo.clickOnView(solo.getView(R.id.change_status_button));
		test予定ボタンを押したら表示();
		solo.clickOnView(solo.getView(R.id.change_status_button));
		assertEquals("予定を隠す",
				((Button) solo.getView(R.id.schedule_button)).getText());
		assertEquals(View.VISIBLE, solo.getView(R.id.schedule_layout)
				.getVisibility());
		solo.clickOnView(solo.getView(R.id.start_button));
		assertEquals("予定を表示",
				((Button) solo.getView(R.id.schedule_button)).getText());
		assertEquals(View.GONE, solo.getView(R.id.schedule_layout)
				.getVisibility());
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
	}
}