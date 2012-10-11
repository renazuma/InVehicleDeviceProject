package com.kogasoftware.odt.webapi.test.model;

import java.util.Date;

import junit.framework.TestCase;

import com.kogasoftware.odt.webapi.model.OperationRecord;
import com.kogasoftware.odt.webapi.model.OperationSchedule;

public class OperationScheduleTestCase extends TestCase {
	public void testIsDeparted() {
		OperationSchedule os = new OperationSchedule();
		os.clearOperationRecord();
		assertFalse(os.isDeparted());
		OperationRecord or = new OperationRecord();
		or.clearDepartedAt();
		os.setOperationRecord(or);
		assertFalse(os.isDeparted());
		or.setDepartedAt(new Date());
		assertTrue(os.isDeparted());
		or.clearDepartedAt();
		assertFalse(os.isDeparted());
	}

	public void testIsArrived() {
		OperationSchedule os = new OperationSchedule();
		os.clearOperationRecord();
		assertFalse(os.isArrived());
		OperationRecord or = new OperationRecord();
		or.clearArrivedAt();
		os.setOperationRecord(or);
		assertFalse(os.isArrived());
		or.setArrivedAt(new Date());
		assertTrue(os.isArrived());
		or.clearArrivedAt();
		assertFalse(os.isArrived());
	}
}
