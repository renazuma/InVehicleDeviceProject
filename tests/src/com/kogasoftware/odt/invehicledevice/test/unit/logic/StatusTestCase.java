package com.kogasoftware.odt.invehicledevice.test.unit.logic;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;

public class LocalDataTestCase extends EmptyActivityInstrumentationTestCase2 {
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		Thread.sleep(10 * 1000); // 別のアプリがLocalDataを保存するかもしれないため、一定時間待つ
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
		s1.url = "http://example.com/" + Math.random();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(s1);
		oos.close();

		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		ObjectInputStream ois = new ObjectInputStream(bais);
		LocalData s2 = (LocalData) ois.readObject();
		assertEquals(s2.url, s1.url);
	}
}