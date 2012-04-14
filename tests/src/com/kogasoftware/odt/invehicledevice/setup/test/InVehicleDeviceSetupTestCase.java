package com.kogasoftware.odt.invehicledevice.setup.test;

import java.util.concurrent.CountDownLatch;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;
import com.kogasoftware.odt.invehicledevice.setup.InVehicleDeviceSetupActivity;

public class InVehicleDeviceSetupTestCase extends
		ActivityInstrumentationTestCase2<InVehicleDeviceSetupActivity> {

	private Solo solo;

	public InVehicleDeviceSetupTestCase() {
		super("com.kogasoftware.odt.invehicledevice.setup", InVehicleDeviceSetupActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
	}
	
	public void testFoo() throws Exception {
		final CountDownLatch l = new CountDownLatch(1);
		getActivity().runOnUiThread(new Runnable(){
			public void run() {
				l.countDown();
			}
		});
		l.await();
		assertTrue(true);
	}
}
