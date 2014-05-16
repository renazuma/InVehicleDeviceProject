package com.kogasoftware.odt.invehicledevice.apiclient.model.base;

import java.util.LinkedList;

import junit.framework.TestCase;

import com.google.common.collect.Lists;
import com.kogasoftware.odt.apiclient.Serializations;
import com.kogasoftware.odt.invehicledevice.apiclient.model.*;

/**
 * OperationScheduleBaseのテスト
 */
public class OperationScheduleBaseTestCase extends TestCase {
	UnmergedOperationSchedule model;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		model = new UnmergedOperationSchedule();
	}

	@Override
	public void tearDown() throws Exception {
		try {
		} finally {
			super.setUp();
		}
	}

	public void xtestClone() throws Exception {
		UnmergedOperationSchedule m1 = new UnmergedOperationSchedule();
		m1.setId(10);
		UnmergedOperationSchedule m2 = m1.clone();
		assertFalse(m1 == m2);
		assertEquals(m1, m2);

		m1.setId(11);
		assertFalse(m1.equals(m2));

		UnmergedOperationSchedule m3 = m1.clone();
		assertEquals(m1, Serializations.clone(m3));
	}

	public void xtestSerializable() throws Exception {
		UnmergedOperationSchedule m1 = new UnmergedOperationSchedule();
		m1.setId(10);
		UnmergedOperationSchedule m2 = Serializations.clone(m1);
		assertFalse(m1 == m2);
		assertEquals(m1, m2);

		m1.setId(11);
		assertFalse(m1.equals(m2));

		UnmergedOperationSchedule m3 = Serializations.clone(m1);
		assertEquals(m1, m3);
	}

	public void xtestEquals() throws Exception {
		UnmergedOperationSchedule l = new UnmergedOperationSchedule();
		UnmergedOperationSchedule r = l.clone();

		assertTrue(l.equals(r));
		assertTrue(r.equals(l));

		l.setId(1);
		assertFalse(l.equals(r));
		assertFalse(r.equals(l));

		r.setId(1);
		assertTrue(l.equals(r));
		assertTrue(r.equals(l));

		assertFalse(l.equals(null));
		assertFalse(l.equals(new Object()));
	}

	public void testCanSerializable() {
		Model.getObjectMapper().canSerialize(OperationScheduleBase.class);
		Model.getObjectMapper().canSerialize(UnmergedOperationSchedule.class);
	}

	public void xtestIdentity() {
		UnmergedOperationSchedule odd = new UnmergedOperationSchedule();
		UnmergedOperationSchedule even = new UnmergedOperationSchedule();
		LinkedList<UnmergedOperationSchedule> l1 = Lists.newLinkedList();
		l1.add(even);
		l1.add(odd);
		l1.add(even);
		l1.add(odd);
		
		LinkedList<UnmergedOperationSchedule> l2 = Serializations.clone(l1);
		assertEquals(l1, l2);
		
		assertEquals(l1.get(0), l2.get(0));
		assertEquals(l1.get(1), l2.get(1));
		assertEquals(l1.get(2), l2.get(2));
		assertEquals(l1.get(3), l2.get(3));

		assertFalse(l1.get(0) == l2.get(0));
		assertFalse(l1.get(1) == l2.get(1));
		assertFalse(l1.get(2) == l2.get(2));
		assertFalse(l1.get(3) == l2.get(3));

		assertTrue(l2.get(0) == l2.get(2));
		assertTrue(l2.get(1) == l2.get(3));
	}
}
