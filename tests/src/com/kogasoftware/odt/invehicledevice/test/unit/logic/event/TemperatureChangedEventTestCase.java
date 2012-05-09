package com.kogasoftware.odt.invehicledevice.test.unit.logic.event;

import junit.framework.TestCase;

import com.kogasoftware.odt.invehicledevice.logic.event.OrientationChangedEvent;

public class TemperatureChangedEventTestCase extends TestCase {
	public void testConstructor() throws Exception {
		Float f = 123f;
		OrientationChangedEvent e = new OrientationChangedEvent(f);
		assertEquals(e.orientationDegree, f);
	}
}
