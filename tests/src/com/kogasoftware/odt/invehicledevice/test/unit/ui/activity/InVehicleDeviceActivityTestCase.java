package com.kogasoftware.odt.invehicledevice.test.unit.ui.activity;

import android.test.ActivityInstrumentationTestCase2;

import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;

public class InVehicleDeviceActivityTestCase extends
		ActivityInstrumentationTestCase2<InVehicleDeviceActivity> {

	public InVehicleDeviceActivityTestCase() {
		super("com.kogasoftware.odt.invehicledevice",
				InVehicleDeviceActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void test起動時にいきなり落ちないかだけ確認() throws Exception {
		getActivity();
	}

	public void xtest2() {
		fail("stub! / 単体でのテストは難しいので、結合試験を利用する");
	}
}
