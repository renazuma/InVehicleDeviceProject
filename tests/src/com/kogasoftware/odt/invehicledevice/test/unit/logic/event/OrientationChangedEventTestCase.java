package com.kogasoftware.odt.invehicledevice.test.unit.logic.event;

import junit.framework.TestCase;

import com.kogasoftware.odt.invehicledevice.logic.event.TemperatureChangedEvent;

public class OrientationChangedEventTestCase extends TestCase {
	public void testConstructor() throws Exception {
		Float t = 15f;
		TemperatureChangedEvent e = new TemperatureChangedEvent(t);
		assertEquals(e.celciusTemperature, t);
	}
}
