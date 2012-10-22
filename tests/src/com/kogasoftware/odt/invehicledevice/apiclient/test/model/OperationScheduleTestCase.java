package com.kogasoftware.odt.invehicledevice.apiclient.test.model;

import static com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule.getCurrentOperationSchedule;
import static com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule.getRelativeOperationSchedule;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationRecord;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule;

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

	public void testGetRelativeOperationScheduleAndGetCurrentOperationSchedule() {
		OperationRecord incomplete = new OperationRecord();
		OperationRecord departed = new OperationRecord();
		departed.setDepartedAt(new Date());

		// 0件
		List<OperationSchedule> oss = new LinkedList<OperationSchedule>();
		assertFalse(getRelativeOperationSchedule(oss, -1).isPresent());
		assertFalse(getRelativeOperationSchedule(oss, 0).isPresent());
		assertFalse(getCurrentOperationSchedule(oss).isPresent());
		assertFalse(getRelativeOperationSchedule(oss, 1).isPresent());

		// 1件
		oss.add(new OperationSchedule());

		oss.get(0).setOperationRecord(incomplete);
		assertFalse(getRelativeOperationSchedule(oss, -1).isPresent());
		assertEquals(Optional.of(oss.get(0)),
				getRelativeOperationSchedule(oss, 0));
		assertEquals(Optional.of(oss.get(0)),
				getCurrentOperationSchedule(oss));
		assertFalse(getRelativeOperationSchedule(oss, 1).isPresent());

		oss.get(0).setOperationRecord(departed);
		assertEquals(Optional.of(oss.get(0)),
				getRelativeOperationSchedule(oss, -1));
		assertFalse(getRelativeOperationSchedule(oss, 0).isPresent());
		assertFalse(getCurrentOperationSchedule(oss).isPresent());
		assertFalse(getRelativeOperationSchedule(oss, 1).isPresent());

		// 2件
		oss.add(new OperationSchedule());

		oss.get(0).setOperationRecord(incomplete);
		oss.get(1).setOperationRecord(incomplete);
		assertFalse(getRelativeOperationSchedule(oss, -1).isPresent());
		assertEquals(Optional.of(oss.get(0)),
				getRelativeOperationSchedule(oss, 0));
		assertEquals(Optional.of(oss.get(0)),
				getCurrentOperationSchedule(oss));
		assertEquals(Optional.of(oss.get(1)),
				getRelativeOperationSchedule(oss, 1));

		oss.get(0).setOperationRecord(departed);
		oss.get(1).setOperationRecord(incomplete);
		assertEquals(Optional.of(oss.get(0)),
				getRelativeOperationSchedule(oss, -1));
		assertEquals(Optional.of(oss.get(1)),
				getRelativeOperationSchedule(oss, 0));
		assertEquals(Optional.of(oss.get(1)),
				getCurrentOperationSchedule(oss));
		assertFalse(getRelativeOperationSchedule(oss, 1).isPresent());

		oss.get(0).setOperationRecord(departed);
		oss.get(1).setOperationRecord(departed);
		assertEquals(Optional.of(oss.get(1)),
				getRelativeOperationSchedule(oss, -1));
		assertFalse(getRelativeOperationSchedule(oss, 0).isPresent());
		assertFalse(getCurrentOperationSchedule(oss).isPresent());
		assertFalse(getRelativeOperationSchedule(oss, 1).isPresent());

		// 3件
		oss.add(new OperationSchedule());

		oss.get(0).setOperationRecord(incomplete);
		oss.get(1).setOperationRecord(incomplete);
		oss.get(2).setOperationRecord(incomplete);
		assertFalse(getRelativeOperationSchedule(oss, -1).isPresent());
		assertEquals(Optional.of(oss.get(0)),
				getRelativeOperationSchedule(oss, 0));
		assertEquals(Optional.of(oss.get(0)),
				getCurrentOperationSchedule(oss));
		assertEquals(Optional.of(oss.get(1)),
				getRelativeOperationSchedule(oss, 1));

		oss.get(0).setOperationRecord(departed);
		oss.get(1).setOperationRecord(incomplete);
		oss.get(2).setOperationRecord(incomplete);
		assertEquals(Optional.of(oss.get(0)),
				getRelativeOperationSchedule(oss, -1));
		assertEquals(Optional.of(oss.get(1)),
				getRelativeOperationSchedule(oss, 0));
		assertEquals(Optional.of(oss.get(1)),
				getCurrentOperationSchedule(oss));
		assertEquals(Optional.of(oss.get(2)),
				getRelativeOperationSchedule(oss, 1));

		oss.get(0).setOperationRecord(departed);
		oss.get(1).setOperationRecord(departed);
		oss.get(2).setOperationRecord(incomplete);
		assertEquals(Optional.of(oss.get(1)),
				getRelativeOperationSchedule(oss, -1));
		assertEquals(Optional.of(oss.get(2)),
				getRelativeOperationSchedule(oss, 0));
		assertEquals(Optional.of(oss.get(2)),
				getCurrentOperationSchedule(oss));
		assertFalse(getRelativeOperationSchedule(oss, 1).isPresent());

		oss.get(0).setOperationRecord(departed);
		oss.get(1).setOperationRecord(departed);
		oss.get(2).setOperationRecord(departed);
		assertEquals(Optional.of(oss.get(2)),
				getRelativeOperationSchedule(oss, -1));
		assertFalse(getRelativeOperationSchedule(oss, 0).isPresent());
		assertFalse(getCurrentOperationSchedule(oss).isPresent());
		assertFalse(getRelativeOperationSchedule(oss, 1).isPresent());
	}
}
