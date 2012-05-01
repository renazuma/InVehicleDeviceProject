package com.kogasoftware.odt.invehicledevice.test.unit.logic.event;

import junit.framework.TestCase;

import com.kogasoftware.odt.invehicledevice.logic.event.SignalStrengthChangedEvent;

public class SignalStrengthChangedEventTestCase extends TestCase {
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testConstructor() throws Exception {
		Integer a1 = 1;
		SignalStrengthChangedEvent e1 = new SignalStrengthChangedEvent(a1);
		assertEquals(a1, e1.signalStrengthPercentage);

		Integer a2 = -5;
		SignalStrengthChangedEvent e2 = new SignalStrengthChangedEvent(a2);
		assertEquals(a2, e2.signalStrengthPercentage);
	}
}