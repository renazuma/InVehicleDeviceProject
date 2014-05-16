package com.kogasoftware.odt.invehicledevice.apiclient.model;

import static com.kogasoftware.odt.invehicledevice.apiclient.model.UnmergedOperationSchedule.getCurrent;
import static com.kogasoftware.odt.invehicledevice.apiclient.model.UnmergedOperationSchedule.getRelative;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationRecord;
import com.kogasoftware.odt.invehicledevice.apiclient.model.UnmergedOperationSchedule;

public class OperationScheduleTestCase extends TestCase {
	public void testIsDeparted() {
		UnmergedOperationSchedule os = new UnmergedOperationSchedule();
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
		UnmergedOperationSchedule os = new UnmergedOperationSchedule();
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
		List<UnmergedOperationSchedule> oss = new LinkedList<UnmergedOperationSchedule>();
		assertFalse(getRelative(oss, -1).isPresent());
		assertFalse(getRelative(oss, 0).isPresent());
		assertFalse(getCurrent(oss).isPresent());
		assertFalse(getRelative(oss, 1).isPresent());

		// 1件
		oss.add(new UnmergedOperationSchedule());

		oss.get(0).setOperationRecord(incomplete);
		assertFalse(getRelative(oss, -1).isPresent());
		assertEquals(Optional.of(oss.get(0)),
				getRelative(oss, 0));
		assertEquals(Optional.of(oss.get(0)),
				getCurrent(oss));
		assertFalse(getRelative(oss, 1).isPresent());

		oss.get(0).setOperationRecord(departed);
		assertEquals(Optional.of(oss.get(0)),
				getRelative(oss, -1));
		assertFalse(getRelative(oss, 0).isPresent());
		assertFalse(getCurrent(oss).isPresent());
		assertFalse(getRelative(oss, 1).isPresent());

		// 2件
		oss.add(new UnmergedOperationSchedule());

		oss.get(0).setOperationRecord(incomplete);
		oss.get(1).setOperationRecord(incomplete);
		assertFalse(getRelative(oss, -1).isPresent());
		assertEquals(Optional.of(oss.get(0)),
				getRelative(oss, 0));
		assertEquals(Optional.of(oss.get(0)),
				getCurrent(oss));
		assertEquals(Optional.of(oss.get(1)),
				getRelative(oss, 1));

		oss.get(0).setOperationRecord(departed);
		oss.get(1).setOperationRecord(incomplete);
		assertEquals(Optional.of(oss.get(0)),
				getRelative(oss, -1));
		assertEquals(Optional.of(oss.get(1)),
				getRelative(oss, 0));
		assertEquals(Optional.of(oss.get(1)),
				getCurrent(oss));
		assertFalse(getRelative(oss, 1).isPresent());

		oss.get(0).setOperationRecord(departed);
		oss.get(1).setOperationRecord(departed);
		assertEquals(Optional.of(oss.get(1)),
				getRelative(oss, -1));
		assertFalse(getRelative(oss, 0).isPresent());
		assertFalse(getCurrent(oss).isPresent());
		assertFalse(getRelative(oss, 1).isPresent());

		// 3件
		oss.add(new UnmergedOperationSchedule());

		oss.get(0).setOperationRecord(incomplete);
		oss.get(1).setOperationRecord(incomplete);
		oss.get(2).setOperationRecord(incomplete);
		assertFalse(getRelative(oss, -1).isPresent());
		assertEquals(Optional.of(oss.get(0)),
				getRelative(oss, 0));
		assertEquals(Optional.of(oss.get(0)),
				getCurrent(oss));
		assertEquals(Optional.of(oss.get(1)),
				getRelative(oss, 1));

		oss.get(0).setOperationRecord(departed);
		oss.get(1).setOperationRecord(incomplete);
		oss.get(2).setOperationRecord(incomplete);
		assertEquals(Optional.of(oss.get(0)),
				getRelative(oss, -1));
		assertEquals(Optional.of(oss.get(1)),
				getRelative(oss, 0));
		assertEquals(Optional.of(oss.get(1)),
				getCurrent(oss));
		assertEquals(Optional.of(oss.get(2)),
				getRelative(oss, 1));

		oss.get(0).setOperationRecord(departed);
		oss.get(1).setOperationRecord(departed);
		oss.get(2).setOperationRecord(incomplete);
		assertEquals(Optional.of(oss.get(1)),
				getRelative(oss, -1));
		assertEquals(Optional.of(oss.get(2)),
				getRelative(oss, 0));
		assertEquals(Optional.of(oss.get(2)),
				getCurrent(oss));
		assertFalse(getRelative(oss, 1).isPresent());

		oss.get(0).setOperationRecord(departed);
		oss.get(1).setOperationRecord(departed);
		oss.get(2).setOperationRecord(departed);
		assertEquals(Optional.of(oss.get(2)),
				getRelative(oss, -1));
		assertFalse(getRelative(oss, 0).isPresent());
		assertFalse(getCurrent(oss).isPresent());
		assertFalse(getRelative(oss, 1).isPresent());
	}
}
