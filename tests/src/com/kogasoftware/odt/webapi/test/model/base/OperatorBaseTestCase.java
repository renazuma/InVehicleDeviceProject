package com.kogasoftware.odt.webapi.test.model.base;

import java.util.Date;

import android.test.AndroidTestCase;

import org.apache.commons.lang3.SerializationUtils;

import com.kogasoftware.odt.webapi.model.*;
import com.kogasoftware.odt.webapi.model.base.*;

/**
 * OperatorBaseのテスト
 */
@SuppressWarnings("unused")
public class OperatorBaseTestCase extends AndroidTestCase {
	Operator model;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		model = new Operator();
	}

	@Override
	public void tearDown() throws Exception {
		try {
		} finally {
			super.setUp();
		}
	}

	public void testCloneByJSON() throws Exception {
		Date ua = new Date();
		Operator m1 = new Operator();
		m1.setId(10);
		Operator m2 = m1.cloneByJSON();
		assertFalse(m1 == m2);
		assertEquals(m1, m2);

		m1.setId(11);
		assertFalse(m1.equals(m2));

		Operator m3 = m1.cloneByJSON();
		assertEquals(m1, m3);
	}

	public void testSerialize() throws Exception {
		Date ua = new Date();
		Operator m1 = new Operator();
		m1.setId(10);
		Operator m2 = SerializationUtils.clone(m1);
		assertFalse(m1 == m2);
		assertEquals(m1, m2);

		m1.setId(11);
		assertFalse(m1.equals(m2));

		Operator m3 = SerializationUtils.clone(m1);
		assertEquals(m1, m3);
	}

	public void testEquals() throws Exception {
		Operator l = new Operator();
		Operator r = new Operator();
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
}
