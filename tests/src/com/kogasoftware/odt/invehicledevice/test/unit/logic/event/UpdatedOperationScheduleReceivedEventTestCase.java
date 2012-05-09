package com.kogasoftware.odt.invehicledevice.test.unit.logic.event;

import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import com.kogasoftware.odt.invehicledevice.logic.event.UpdatedOperationScheduleReceivedEvent;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class UpdatedOperationScheduleReceivedEventTestCase extends TestCase {
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testConstructor() throws Exception {
		OperationSchedule os0 = new OperationSchedule();
		OperationSchedule os1 = new OperationSchedule();
		List<OperationSchedule> oss = new LinkedList<OperationSchedule>();
		oss.add(os0);
		oss.add(os1);

		VehicleNotification vn0 = new VehicleNotification();
		VehicleNotification vn1 = new VehicleNotification();
		List<VehicleNotification> vns = new LinkedList<VehicleNotification>();
		vns.add(vn0);
		vns.add(vn1);

		UpdatedOperationScheduleReceivedEvent e = new UpdatedOperationScheduleReceivedEvent(
				oss, vns);
		assertEquals(e.operationSchedules.get(0), os0);
		assertEquals(e.operationSchedules.get(1), os1);
		assertEquals(e.triggerVehicleNotifications.get(0), vn0);
		assertEquals(e.triggerVehicleNotifications.get(1), vn1);
	}
}
