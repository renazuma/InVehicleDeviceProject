package com.kogasoftware.odt.invehicledevice.test.unit.backgroundtask;

import java.util.concurrent.TimeUnit;

import android.content.Intent;

import com.kogasoftware.odt.invehicledevice.backgroundtask.ExitBroadcastReceiver;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.event.ExitEvent;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.test.util.Subscriber;

public class ExitBroadcastReceiverTestCase extends
		EmptyActivityInstrumentationTestCase2 {

	CommonLogic cl;
	Subscriber<ExitEvent> s;
	ExitBroadcastReceiver ebr;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		cl = newCommonLogic();
		s = Subscriber.of(ExitEvent.class, cl);
		ebr = new ExitBroadcastReceiver(cl);
	}

	@Override
	public void tearDown() throws Exception {
		if (cl != null) {
			cl.dispose();
		}
		super.tearDown();
	}

	/**
	 * ExitEventを発生させる
	 */
	public void testOnSharedPreferenceChanged1() throws Exception {
		assertFalse(s.cdl.await(3, TimeUnit.SECONDS));
		ebr.onReceive(getInstrumentation().getContext(), new Intent());
		assertTrue(s.cdl.await(3, TimeUnit.SECONDS));
	}
}
