package com.kogasoftware.odt.invehicledevice.test;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.TextView;

import com.jayway.android.robotium.solo.Solo;
import com.kogasoftware.odt.invehicledevice.InVehicleDeviceActivity;
import com.kogasoftware.odt.invehicledevice.R;

public class DrivingTestCase extends
		ActivityInstrumentationTestCase2<InVehicleDeviceActivity> {

	private Solo solo;

	public DrivingTestCase() {
		super("com.kogasoftware.odt.invehicledevice",
				InVehicleDeviceActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	public void test起動時は走行中表示() {
		TextView v = (TextView) solo.getView(R.id.status_text_view);
		assertEquals("走行中", v.getText()); // TODO 画像ファイル名assertに書き換わる予定
	}

	public void test起動時は出発ダイアログは非表示() {
		assertEquals(View.GONE, solo.getView(R.id.check_start_layout)
				.getVisibility());
	}

	public void test到着しましたボタンを押すと停車中表示() {
		test起動時は走行中表示();

		TextView v = (TextView) solo.getView(R.id.status_text_view);
		solo.clickOnButton("到着しました");
		assertEquals("停車中", v.getText()); // TODO 画像ファイル名assertに書き換わる予定
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
	}
}