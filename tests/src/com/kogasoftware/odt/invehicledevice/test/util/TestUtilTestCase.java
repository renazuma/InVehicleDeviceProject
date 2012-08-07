package com.kogasoftware.odt.invehicledevice.test.util;

import java.util.List;

import junit.framework.AssertionFailedError;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.jayway.android.robotium.solo.Solo;
import com.kogasoftware.odt.invehicledevice.datasource.DataSource;
import com.kogasoftware.odt.invehicledevice.datasource.EmptyDataSource;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.startupservice.StartupService;
import com.kogasoftware.odt.invehicledevice.test.util.datasource.DummyDataSource;
import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.OperationSchedule;

public class TestUtilTestCase extends
		ActivityInstrumentationTestCase2<InVehicleDeviceActivity> {

	private static final String TAG = TestUtilTestCase.class.getSimpleName();

	public TestUtilTestCase() {
		super("com.kogasoftware.odt.invehicledevice",
				InVehicleDeviceActivity.class);
	}

	public void setUp() throws Exception {
		TestUtil.disableAutoStart(getInstrumentation().getContext());
	}

	public void tearDown() throws Exception {
		try {
			Context tc = getInstrumentation().getTargetContext();
			TestUtil.disableAutoStart(tc);
			Thread.sleep(10 * 1000);
			TestUtil.exitService(tc);
		} finally {
			super.tearDown();
		}
	}

	public void callTestWaitForStartUi(Boolean timeout) throws Exception {
		TestUtil.clearStatus();
		DataSource ds = new DummyDataSource();
		if (timeout) {
			ds = new DummyDataSource() {
				@Override
				public List<OperationSchedule> getOperationSchedules()
						throws WebAPIException {
					throw new WebAPIException("test");
				}
			};
		}
		TestUtil.setDataSource(ds);
		assertEquals(!timeout, TestUtil.waitForStartUI(getActivity()).booleanValue());
	}

	public void testWaitForStartUi() throws Exception {
		callTestWaitForStartUi(false);
	}

	public void testWaitForStartUiTimeout() throws Exception {
		callTestWaitForStartUi(true);
	}

	public void testDisableAutoStart() throws Exception {
		Context c = getInstrumentation().getContext();
		String pn = getInstrumentation().getTargetContext().getPackageName();
		InVehicleDeviceActivity a = getActivity();
		TestUtil.waitForStartUI(a);
		TestUtil.disableAutoStart(c);
		a.finish();

		Thread.sleep(5000);

		ActivityManager activityManager = (ActivityManager) c
				.getSystemService(Activity.ACTIVITY_SERVICE);
		for (RunningTaskInfo runningTaskInfo : activityManager
				.getRunningTasks(1)) {
			ComponentName topActivity = runningTaskInfo.topActivity;
			if (topActivity.getPackageName().equals(pn)
					&& topActivity.getClassName().equals(
							InVehicleDeviceActivity.class.getName())) {
				fail();
			}
		}

		TestUtil.enableAutoStart(c);
		Thread.sleep(StartupService.CHECK_DEVICE_INTERVAL_MILLIS);
		Thread.sleep(5 * 1000);
		
		for (RunningTaskInfo runningTaskInfo : activityManager
				.getRunningTasks(1)) {
			ComponentName topActivity = runningTaskInfo.topActivity;
			if (topActivity.getPackageName().equals(pn)
					&& topActivity.getClassName().equals(
							InVehicleDeviceActivity.class.getName())) {
				return;
			}
		}
		
		fail();
	}
}
