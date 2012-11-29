package com.kogasoftware.odt.invehicledevice.apiclient.test.model.base;

import java.util.LinkedList;

import junit.framework.TestCase;

import org.apache.commons.lang3.SerializationUtils;

import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.apiclient.model.*;
import com.kogasoftware.odt.invehicledevice.apiclient.model.base.*;

/**
 * PlatformBaseのテスト
 */
public class PlatformBaseTestCase extends TestCase {
	Platform model;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		model = new Platform();
	}

	@Override
	public void tearDown() throws Exception {
		try {
		} finally {
			super.setUp();
		}
	}

	public void testClone() throws Exception {
		Platform m1 = new Platform();
		m1.setId(10);
		Platform m2 = m1.clone();
		assertFalse(m1 == m2);
		assertEquals(m1, m2);

		m1.setId(11);
		assertFalse(m1.equals(m2));

		Platform m3 = m1.clone();
		assertEquals(m1, SerializationUtils.clone(m3));
	}

	public void testSerializable() throws Exception {
		Platform m1 = new Platform();
		m1.setId(10);
		Platform m2 = SerializationUtils.clone(m1);
		assertFalse(m1 == m2);
		assertEquals(m1, m2);

		m1.setId(11);
		assertFalse(m1.equals(m2));

		Platform m3 = SerializationUtils.clone(m1);
		assertEquals(m1, m3);
	}

	public void testEquals() throws Exception {
		Platform l = new Platform();
		Platform r = l.clone();

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
		Model.getObjectMapper().canSerialize(PlatformBase.class);
		Model.getObjectMapper().canSerialize(Platform.class);
	}

	public void testIdentity() {
		Platform odd = new Platform();
		Platform even = new Platform();
		LinkedList<Platform> l1 = Lists.newLinkedList();
		l1.add(even);
		l1.add(odd);
		l1.add(even);
		l1.add(odd);
		
		LinkedList<Platform> l2 = SerializationUtils.clone(l1);
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
