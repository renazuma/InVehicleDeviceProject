package com.kogasoftware.odt.invehicledevice.apiclient.model.base;

import java.util.Date;
import java.util.LinkedList;

import junit.framework.TestCase;

import com.google.common.collect.Lists;
import com.kogasoftware.odt.apiclient.Serializations;
import com.kogasoftware.odt.invehicledevice.apiclient.model.*;

/**
 * UnitAssignmentBaseのテスト
 */
public class UnitAssignmentBaseTestCase extends TestCase {
	UnitAssignment model;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		model = new UnitAssignment();
	}

	@Override
	public void tearDown() throws Exception {
		try {
		} finally {
			super.setUp();
		}
	}

	public void xtestClone() throws Exception {
		UnitAssignment m1 = new UnitAssignment();
		m1.setId(10);
		UnitAssignment m2 = m1.clone();
		assertFalse(m1 == m2);
		assertEquals(m1, m2);

		m1.setId(11);
		Date ua = new Date();
		m1.setUpdatedAt(ua);
		m2.setUpdatedAt(ua);
		assertFalse(m1.equals(m2));

		UnitAssignment m3 = m1.clone();
		assertEquals(m1, Serializations.clone(m3));
	}

	public void xtestSerializable() throws Exception {
		UnitAssignment m1 = new UnitAssignment();
		m1.setId(10);
		UnitAssignment m2 = Serializations.clone(m1);
		assertFalse(m1 == m2);
		assertEquals(m1, m2);

		m1.setId(11);
		Date ua = new Date();
		m1.setUpdatedAt(ua);
		m2.setUpdatedAt(ua);
		assertFalse(m1.equals(m2));

		UnitAssignment m3 = Serializations.clone(m1);
		assertEquals(m1, m3);
	}

	public void xtestEquals() throws Exception {
		UnitAssignment l = new UnitAssignment();
		UnitAssignment r = l.clone();

		Integer s = 10;
		Date ua = new Date();

		l.setUpdatedAt(ua);
		r.setUpdatedAt(ua);
		assertTrue(l.equals(r));
		assertTrue(r.equals(l));

		l.setId(1);
		Thread.sleep(s);
		assertFalse(l.equals(r));
		assertFalse(r.equals(l));
		l.setUpdatedAt(ua);
		r.setUpdatedAt(ua);
		assertFalse(l.equals(r));
		assertFalse(r.equals(l));

		r.setId(1);
		Thread.sleep(s);
		assertFalse(l.equals(r));
		assertFalse(r.equals(l));
		l.setUpdatedAt(ua);
		r.setUpdatedAt(ua);
		assertTrue(l.equals(r));
		assertTrue(r.equals(l));

		assertFalse(l.equals(null));
		assertFalse(l.equals(new Object()));
	}

	public void testUpdatedAt() throws Exception {
		Integer s = 10;
		Date ua = new Date();
		model.setUpdatedAt(ua);
		assertEquals(ua, model.getUpdatedAt());

		Thread.sleep(s);
		model.setId(model.getId() + 1);
		assertTrue(ua.before(model.getUpdatedAt()));

		Thread.sleep(s);
		model.setId(model.getId());
		assertTrue(ua.before(model.getUpdatedAt()));

		Thread.sleep(s);
		model.setUpdatedAt(ua);
		assertEquals(ua, model.getUpdatedAt());
	}

	public void testCanSerializable() {
		Model.getObjectMapper().canSerialize(UnitAssignmentBase.class);
		Model.getObjectMapper().canSerialize(UnitAssignment.class);
	}

	public void xtestIdentity() {
		UnitAssignment odd = new UnitAssignment();
		UnitAssignment even = new UnitAssignment();
		LinkedList<UnitAssignment> l1 = Lists.newLinkedList();
		l1.add(even);
		l1.add(odd);
		l1.add(even);
		l1.add(odd);
		
		LinkedList<UnitAssignment> l2 = Serializations.clone(l1);
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
