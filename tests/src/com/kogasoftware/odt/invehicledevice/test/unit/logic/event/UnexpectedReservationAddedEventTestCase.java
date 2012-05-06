package com.kogasoftware.odt.invehicledevice.test.unit.logic.event;

import junit.framework.TestCase;

import com.kogasoftware.odt.invehicledevice.logic.event.UnexpectedReservationAddedEvent;

public class UnexpectedReservationAddedEventTestCase extends TestCase {
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testConstructor() throws Exception {
		Integer id = 100;
		UnexpectedReservationAddedEvent e = new UnexpectedReservationAddedEvent(
				id);
		assertEquals(e.arrivalOperationScheduleId, id);
	}
}
