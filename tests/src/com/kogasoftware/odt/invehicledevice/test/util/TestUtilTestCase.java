package com.kogasoftware.odt.invehicledevice.test.util;

import java.util.LinkedList;
import java.util.List;

import junit.framework.AssertionFailedError;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;

import com.kogasoftware.odt.invehicledevice.datasource.DataSource;
import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;
import com.kogasoftware.odt.invehicledevice.test.util.datasource.DummyDataSource;
import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.OperationSchedule;

public class TestUtilTestCase extends
		ActivityInstrumentationTestCase2<InVehicleDeviceActivity> {

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

	private static final String TAG = TestUtilTestCase.class.getSimpleName();

	InVehicleDeviceActivity a;
	Context c;
	Context tc;

	public TestUtilTestCase() {
		super("com.kogasoftware.odt.invehicledevice",
				InVehicleDeviceActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		c = getInstrumentation().getContext();
		tc = getInstrumentation().getTargetContext();
		TestUtil.disableAutoStart(c);
	}

	@Override
	public void tearDown() throws Exception {
		if (a != null) {
			a.finish();
			Thread.sleep(InVehicleDeviceActivity.PAUSE_FINISH_TIMEOUT_MILLIS);
			Thread.sleep(3 * 1000);
		}
		super.tearDown();
	}

	public void callTestWaitForStartUi(Boolean timeout) throws Exception {
	}

	public void testWaitForStartUi1() throws Exception {
		callTestWaitForStartUi(false);
	}

	public void testWaitForStartUi1Timeout() throws Exception {
		callTestWaitForStartUi(true);
	}

	public void testWaitForStartUi2() throws Exception {
		callTestWaitForStartUi(false);
	}

	public void testWaitForStartUi2Timeout() throws Exception {
		callTestWaitForStartUi(true);
	}

	public void testWaitForStartUi3() throws Exception {
		callTestWaitForStartUi(false);
	}

	public void testWaitForStartUi3Timeout() throws Exception {
		callTestWaitForStartUi(true);
	}

	public void testWaitForStartUi4() throws Exception {
		callTestWaitForStartUi(false);
	}

	public void testWaitForStartUi4Timeout() throws Exception {
		callTestWaitForStartUi(true);
	}

	public void testAssertEmptyObject() throws Exception {
		Context c = getInstrumentation().getContext();
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

