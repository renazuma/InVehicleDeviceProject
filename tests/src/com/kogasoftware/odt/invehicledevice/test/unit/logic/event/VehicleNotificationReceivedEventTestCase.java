package com.kogasoftware.odt.invehicledevice.test.unit.logic.event;

import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import com.kogasoftware.odt.invehicledevice.logic.event.VehicleNotificationReceivedEvent;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class VehicleNotificationReceivedEventTestCase extends TestCase {
	public void testConstructor() throws Exception {
		List<VehicleNotification> vns = new LinkedList<VehicleNotification>();
		VehicleNotification vn = new VehicleNotification();
		vns.add(vn);
		VehicleNotificationReceivedEvent e = new VehicleNotificationReceivedEvent(
				vns);
		assertEquals(vn, e.vehicleNotifications.get(0));
	}
}
