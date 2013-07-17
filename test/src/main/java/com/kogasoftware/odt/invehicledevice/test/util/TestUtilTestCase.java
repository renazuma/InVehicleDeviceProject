package com.kogasoftware.odt.invehicledevice.test.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import android.test.AndroidTestCase;
import android.test.MoreAsserts;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;

public class TestUtilTestCase extends AndroidTestCase {

	public void testReadWithNonBlock() throws Exception {
		byte[] ba = new byte[] { 1, 2, 3 };
		ByteArrayInputStream baos = new ByteArrayInputStream(ba);
		MoreAsserts.assertEquals(ba, TestUtil.readWithNonBlock(baos));
		MoreAsserts.assertEquals(new byte[0], TestUtil.readWithNonBlock(baos));
	}
	
	public void testAdvanceDate() {
		Date now = new Date();
		InVehicleDeviceService.setMockDate(now);
		
		assertEquals(now, InVehicleDeviceService.getDate());
		
		Integer m = 5000;
		TestUtil.advanceDate(m);
		
		assertEquals(now.getTime() + m, InVehicleDeviceService.getDate().getTime());
	}
	
	public void testAdvanceDate_WakeUpThread() throws Exception {
		Thread t1 = new Thread() {
			@Override
			public void run() {
				try {
					InVehicleDeviceService.sleep(500 * 1000);
				} catch (InterruptedException e) {
				}
			}
		};

		Thread t2 = new Thread() {
			@Override
			public void run() {
				try {
					InVehicleDeviceService.sleep(1000 * 1000);
				} catch (InterruptedException e) {
				}
			}
		};
		
		Date now = new Date();
		InVehicleDeviceService.setMockDate(now);
		
		t1.start();
		t2.start();
		Thread.sleep(1000);
		
		TestUtil.advanceDate(501 * 1000);
		t1.join(1000);
		assertFalse(t1.isAlive());
		assertTrue(t2.isAlive());
		
		TestUtil.advanceDate(501 * 1000);
		t2.join(1000);
		assertFalse(t2.isAlive());
	}
	
	public void testReadWithNonBlock_NoClose() throws Exception {
		InputStream is = new InputStream() {
			int a = 5;

			@Override
			public int available() {
				return a;
			}

			@Override
			public int read() throws IOException {
				return a--;
			}

			@Override
			public void close() {
				throw new RuntimeException("close!");
			}
		};
		MoreAsserts.assertEquals(new byte[] { 5, 4, 3, 2, 1 },
				TestUtil.readWithNonBlock(is));
		MoreAsserts.assertEquals(new byte[0], TestUtil.readWithNonBlock(is));
		try {
			is.close();
			fail();
		} catch (RuntimeException e) {
		}
	}
}
