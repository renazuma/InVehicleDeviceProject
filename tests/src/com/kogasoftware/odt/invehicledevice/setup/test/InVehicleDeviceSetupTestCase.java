package com.kogasoftware.odt.invehicledevice.setup.test;

import java.util.concurrent.CountDownLatch;

import com.kogasoftware.odt.invehicledevice.setup.InVehicleDeviceSetupActivity;

import android.test.ActivityInstrumentationTestCase2;

public class InVehicleDeviceSetupTestCase extends
		ActivityInstrumentationTestCase2<InVehicleDeviceSetupActivity> {

	public InVehicleDeviceSetupTestCase() {
		super("com.kogasoftware.odt.invehicledevice.setup", InVehicleDeviceSetupActivity.class);
	}
	
	public void testFoo() throws Exception {
		final CountDownLatch l = new CountDownLatch(1);
		getActivity().runOnUiThread(new Runnable(){
			public void run() {
				l.countDown();
			}
		});
		l.await();
	}
}
