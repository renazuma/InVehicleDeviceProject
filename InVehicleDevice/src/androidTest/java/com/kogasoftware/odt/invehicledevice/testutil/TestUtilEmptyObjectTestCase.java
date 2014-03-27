package com.kogasoftware.odt.invehicledevice.testutil;

import java.util.LinkedList;
import java.util.List;
import junit.framework.AssertionFailedError;
import android.app.Instrumentation;
import android.test.InstrumentationTestCase;

import com.kogasoftware.odt.invehicledevice.testutil.TestUtil;

public class TestUtilEmptyObjectTestCase extends InstrumentationTestCase {
	static List<NoGCTestClass> instances;
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		instances = new LinkedList<NoGCTestClass>();
	}

	@Override
	public void tearDown() throws Exception {
		try {
			instances.clear();
			instances = null;
			System.gc();
			Runtime.getRuntime().gc();
		} finally {
			super.tearDown();
		}
	}

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

	public void xtestAssertEmptyObject() throws Exception {
		Instrumentation i = getInstrumentation();
		TestUtil.assertEmptyObject(i, SmallTestClass.class);
		TestUtil.assertEmptyObject(i, SmallTestClass.class, true);
		TestUtil.assertEmptyObject(i, BigTestClass.class, true);
		instances.clear();
		try {
			TestUtil.assertEmptyObject(i, NoGCTestClass.class, true);
			fail();
		} catch (AssertionFailedError e) {
		} finally {
			instances.clear();
		}
	}
}
