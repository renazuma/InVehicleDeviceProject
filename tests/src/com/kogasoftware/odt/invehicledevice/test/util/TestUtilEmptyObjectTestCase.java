package com.kogasoftware.odt.invehicledevice.test.util;

import java.util.LinkedList;
import java.util.List;
import junit.framework.AssertionFailedError;
import android.content.Context;
import android.test.AndroidTestCase;

import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;

public class TestUtilEmptyObjectTestCase extends AndroidTestCase {
	static List<NoGCTestClass> instances = new LinkedList<NoGCTestClass>();

	static class NoGCTestClass {
		NoGCTestClass() {
			instances.add(this);
		}
	};

	static class BigTestClass {
		String message = "hello";
		byte[] payload = new byte[5000];
	};

	static class SmallTestClass {
		String message = "hello";
		byte[] payload = new byte[50];
	}

	public void testAssertEmptyObject() throws Exception {
		Context c = getContext();
		TestUtil.assertEmptyObject(c, SmallTestClass.class);
		TestUtil.assertEmptyObject(c, SmallTestClass.class, true);
		TestUtil.assertEmptyObject(c, BigTestClass.class, true);
		instances.clear();
		try {
			TestUtil.assertEmptyObject(c, NoGCTestClass.class, true);
			fail();
		} catch (AssertionFailedError e) {
		} finally {
			instances.clear();
		}
	}
}
