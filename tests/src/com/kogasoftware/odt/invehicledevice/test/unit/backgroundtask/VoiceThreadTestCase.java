package com.kogasoftware.odt.invehicledevice.test.unit.backgroundtask;

import com.kogasoftware.odt.invehicledevice.backgroundtask.VoiceThread;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;

public class VoiceThreadTestCase extends EmptyActivityInstrumentationTestCase2 {
	VoiceThread vt;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		vt = new VoiceThread(getActivity());
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testVoiceThread_1() throws Exception {
		fail("stub! / physical test required");
	}
}
