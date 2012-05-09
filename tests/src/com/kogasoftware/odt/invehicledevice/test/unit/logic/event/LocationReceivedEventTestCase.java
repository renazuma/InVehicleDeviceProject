package com.kogasoftware.odt.invehicledevice.test.unit.logic.event;

import junit.framework.TestCase;
import android.location.Location;

import com.kogasoftware.odt.invehicledevice.logic.event.LocationReceivedEvent;

public class LocationReceivedEventTestCase extends TestCase {
	public void testConstructor() throws Exception {
		Location l = null;
		LocationReceivedEvent e = new LocationReceivedEvent(l);
		assertEquals(e.location, l);
	}
}
