package com.kogasoftware.odt.invehicledevice.test.unit.backgroundtask;

import com.kogasoftware.odt.invehicledevice.backgroundtask.VoiceThread;
import com.kogasoftware.odt.invehicledevice.test.common.MockActivityUnitTestCase;

public class VoiceThreadTestCase extends MockActivityUnitTestCase {
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
