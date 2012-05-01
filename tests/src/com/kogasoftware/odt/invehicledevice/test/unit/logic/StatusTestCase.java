package com.kogasoftware.odt.invehicledevice.test.unit.logic;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.kogasoftware.odt.invehicledevice.logic.Status;
import com.kogasoftware.odt.invehicledevice.test.common.MockActivityUnitTestCase;

public class StatusTestCase extends MockActivityUnitTestCase {
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * 本当にシリアライズすることができるかのチェック
	 */
	public void testSerializable() throws Exception {
		Status s1 = new Status();
		s1.url = "http://example.com/" + Math.random();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(s1);
		oos.close();

		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		ObjectInputStream ois = new ObjectInputStream(bais);
		Status s2 = (Status) ois.readObject();
		assertEquals(s2.url, s1.url);
	}
}