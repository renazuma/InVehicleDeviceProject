package com.kogasoftware.odt.invehicledevice.test.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.test.AndroidTestCase;
import android.test.MoreAsserts;

import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;

public class TestUtilTestCase extends AndroidTestCase {

	public void testReadWithNonBlock() throws Exception {
		byte[] ba = new byte[] { 1, 2, 3 };
		ByteArrayInputStream baos = new ByteArrayInputStream(ba);
		MoreAsserts.assertEquals(ba, TestUtil.readWithNonBlock(baos));
		MoreAsserts.assertEquals(new byte[0], TestUtil.readWithNonBlock(baos));
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
