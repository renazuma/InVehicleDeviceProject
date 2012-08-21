package com.kogasoftware.odt.invehicledevice.test.unit.service.invehicledeviceservice;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.test.AndroidTestCase;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData;

public class LocalDataTestCase extends AndroidTestCase {
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
		LocalData ld1 = new LocalData();
		ld1.url = "http://example.com/" + Math.random();
		ld1.file = new File("foo");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(ld1);
		oos.close();

		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		ObjectInputStream ois = new ObjectInputStream(bais);
		LocalData ld2 = (LocalData) ois.readObject();
		assertEquals(ld1.url, ld2.url);
		assertEquals(ld1.file, ld2.file);
		assertFalse(ld1.file == ld2.file);
	}
}