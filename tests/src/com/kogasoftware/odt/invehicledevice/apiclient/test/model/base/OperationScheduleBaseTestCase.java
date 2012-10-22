package com.kogasoftware.odt.invehicledevice.apiclient.test.model.base;

import java.util.Date;

import android.test.AndroidTestCase;

import org.apache.commons.lang3.SerializationUtils;

import com.kogasoftware.odt.invehicledevice.apiclient.model.*;
import com.kogasoftware.odt.invehicledevice.apiclient.model.base.*;

/**
 * OperationScheduleBaseのテスト
 */
@SuppressWarnings("unused")
public class OperationScheduleBaseTestCase extends AndroidTestCase {
	OperationSchedule model;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		model = new OperationSchedule();
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
		OperationSchedule m1 = new OperationSchedule();
		m1.setId(10);
		OperationSchedule m2 = m1.cloneByJSON();
		assertFalse(m1 == m2);
		assertEquals(m1, m2);

		m1.setId(11);
		m1.setUpdatedAt(ua);
		m2.setUpdatedAt(ua);
		assertFalse(m1.equals(m2));

		OperationSchedule m3 = m1.cloneByJSON();
		assertEquals(m1, SerializationUtils.clone(m3));
	}

	public void testSerialize() throws Exception {
		Date ua = new Date();
		OperationSchedule m1 = new OperationSchedule();
		m1.setId(10);
		OperationSchedule m2 = SerializationUtils.clone(m1);
		assertFalse(m1 == m2);
		assertEquals(m1, m2);

		m1.setId(11);
		m1.setUpdatedAt(ua);
		m2.setUpdatedAt(ua);
		assertFalse(m1.equals(m2));

		OperationSchedule m3 = SerializationUtils.clone(m1);
		assertEquals(m1, m3);
		assertEquals(m1, m3.cloneByJSON());
	}

	public void testEquals() throws Exception {
		Integer s = 10;
		Date ua = new Date();
		OperationSchedule l = new OperationSchedule();
		OperationSchedule r = l.cloneByJSON();

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
}
