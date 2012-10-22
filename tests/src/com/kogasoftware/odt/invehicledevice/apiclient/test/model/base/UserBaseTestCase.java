package com.kogasoftware.odt.invehicledevice.apiclient.test.model.base;

import java.util.Date;

import android.test.AndroidTestCase;

import org.apache.commons.lang3.SerializationUtils;

import com.kogasoftware.odt.invehicledevice.apiclient.model.*;
import com.kogasoftware.odt.invehicledevice.apiclient.model.base.*;

/**
 * UserBaseのテスト
 */
@SuppressWarnings("unused")
public class UserBaseTestCase extends AndroidTestCase {
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

	public void testCloneByJSON() throws Exception {
		Date ua = new Date();
		User m1 = new User();
		m1.setId(10);
		User m2 = m1.cloneByJSON();
		assertFalse(m1 == m2);
		assertEquals(m1, m2);

		m1.setId(11);
		assertFalse(m1.equals(m2));

		User m3 = m1.cloneByJSON();
		assertEquals(m1, SerializationUtils.clone(m3));
	}

	public void testSerialize() throws Exception {
		Date ua = new Date();
		User m1 = new User();
		m1.setId(10);
		User m2 = SerializationUtils.clone(m1);
		assertFalse(m1 == m2);
		assertEquals(m1, m2);

		m1.setId(11);
		assertFalse(m1.equals(m2));

		User m3 = SerializationUtils.clone(m1);
		assertEquals(m1, m3);
		assertEquals(m1, m3.cloneByJSON());
	}

	public void testEquals() throws Exception {
		User l = new User();
		User r = l.cloneByJSON();
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
