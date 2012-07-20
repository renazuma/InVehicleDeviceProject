package com.kogasoftware.odt.invehicledevice.test.unit.backgroundtask;

import android.content.Intent;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.backgroundthread.ExitBroadcastReceiver;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;

public class ExitBroadcastReceiverTestCase extends
		EmptyActivityInstrumentationTestCase2 {

	ExitBroadcastReceiver ebr;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		ebr = new ExitBroadcastReceiver(null);
	}

	@Override
	public void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * ExitEventを発生させる
	 */
	public void testOnSharedPreferenceChanged1() throws Exception {
		assertFalse(true);
		ebr.onReceive(getInstrumentation().getContext(), new Intent());
		assertTrue(false);
	}
}
