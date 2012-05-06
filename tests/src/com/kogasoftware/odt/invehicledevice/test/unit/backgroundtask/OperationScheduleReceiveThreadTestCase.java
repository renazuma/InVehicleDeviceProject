package com.kogasoftware.odt.invehicledevice.test.unit.backgroundtask;

import com.kogasoftware.odt.invehicledevice.backgroundtask.OperationScheduleReceiveThread;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;

public class OperationScheduleReceiveThreadTestCase extends
		EmptyActivityInstrumentationTestCase2 {

	CommonLogic cl;
	OperationScheduleReceiveThread osrt;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cl = newCommonLogic();
		osrt = new OperationScheduleReceiveThread(cl);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if (osrt != null) {
			osrt.interrupt();
			osrt.join();
		}
	}

	public void test1() throws Exception {
		assertTrue(true);
	}

	public void test2() throws Exception {
		fail("stub!");
	}
}
