package com.kogasoftware.odt.invehicledevice.preference.test;

import java.util.concurrent.CountDownLatch;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;
import com.kogasoftware.odt.invehicledevice.preference.InVehicleDevicePreferenceActivity;

public class InVehicleDevicePreferenceTestCase extends
		ActivityInstrumentationTestCase2<InVehicleDevicePreferenceActivity> {

	private Solo solo;

	public InVehicleDevicePreferenceTestCase() {
		super("com.kogasoftware.odt.invehicledevice.preference", InVehicleDevicePreferenceActivity.class);
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
