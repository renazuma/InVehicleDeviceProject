package com.kogasoftware.odt.invehicledevice.apiclient.test.model.base;

import java.util.LinkedList;

import junit.framework.TestCase;

import org.apache.commons.lang3.SerializationUtils;

import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.apiclient.model.*;
import com.kogasoftware.odt.invehicledevice.apiclient.model.base.*;

/**
 * UserBaseのテスト
 */
public class UserBaseTestCase extends TestCase {
	User model;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		model = new User();
	}

	@Override
	public void tearDown() throws Exception {
		try {
		} finally {
			super.setUp();
		}
	}

	public void testClone() throws Exception {
		User m1 = new User();
		m1.setId(10);
		User m2 = m1.clone();
		assertFalse(m1 == m2);
		assertEquals(m1, m2);

		m1.setId(11);
		assertFalse(m1.equals(m2));

		User m3 = m1.clone();
		assertEquals(m1, SerializationUtils.clone(m3));
	}

	public void testSerializable() throws Exception {
		User m1 = new User();
		m1.setId(10);
		User m2 = SerializationUtils.clone(m1);
		assertFalse(m1 == m2);
		assertEquals(m1, m2);

		m1.setId(11);
		assertFalse(m1.equals(m2));

		User m3 = SerializationUtils.clone(m1);
		assertEquals(m1, m3);
	}

	public void testEquals() throws Exception {
		User l = new User();
		User r = l.clone();

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
		Model.getObjectMapper().canSerialize(UserBase.class);
		Model.getObjectMapper().canSerialize(User.class);
	}

	public void testIdentity() {
		User odd = new User();
		User even = new User();
		LinkedList<User> l1 = Lists.newLinkedList();
		l1.add(even);
		l1.add(odd);
		l1.add(even);
		l1.add(odd);
		
		LinkedList<User> l2 = SerializationUtils.clone(l1);
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
