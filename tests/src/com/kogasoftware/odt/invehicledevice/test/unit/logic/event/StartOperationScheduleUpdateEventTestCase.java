package com.kogasoftware.odt.invehicledevice.test.unit.logic.event;

import junit.framework.TestCase;

import com.kogasoftware.odt.invehicledevice.logic.event.UpdatedOperationScheduleReceiveStartEvent;

public class StartOperationScheduleUpdateEventTestCase extends TestCase {
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testConstructor() throws Exception {
		new UpdatedOperationScheduleReceiveStartEvent();
	}
}