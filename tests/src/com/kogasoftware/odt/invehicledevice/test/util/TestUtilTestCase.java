package com.kogasoftware.odt.invehicledevice.test.util;

import java.util.List;

import junit.framework.AssertionFailedError;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.jayway.android.robotium.solo.Solo;
import com.kogasoftware.odt.invehicledevice.datasource.EmptyDataSource;
import com.kogasoftware.odt.invehicledevice.service.startupservice.StartupService;
import com.kogasoftware.odt.invehicledevice.test.util.datasource.DummyDataSource;
import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.OperationSchedule;

public class TestUtilTestCase extends
		ActivityInstrumentationTestCase2<InVehicleDeviceActivity> {

	public TestUtilTestCase() {
		super("com.kogasoftware.odt.invehicledevice",
				InVehicleDeviceActivity.class);
	}

	public void testWaitForStartUi() throws Exception {
		TestUtil.clearStatus();
		TestUtil.setDataSource(new DummyDataSource());
		assertTrue(TestUtil.waitForStartUI(getActivity()));
	}

	public void testWaitForStartUiTimeout() throws Exception {
		Context c = getInstrumentation().getContext();
		final Activity a = getActivity();
		TestUtil.disableAutoStart(c);
		TestUtil.runOnUiThreadSync(a, new Runnable() {
			@Override
			public void run() {
				a.finish();
			}
		});
		getActivity().finish();

		TestUtil.clearStatus();
		TestUtil.setDataSource(new NoOperationScheduleDataSource());
		assertFalse(TestUtil.waitForStartUI(getActivity()));
	}

	public void testDisableAutoStart() throws Exception {
		Context c = getInstrumentation().getContext();
		String pn = getInstrumentation().getTargetContext().getPackageName();
		Activity a = getActivity();
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
		Thread.sleep(2000);

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

class NoOperationScheduleDataSource extends EmptyDataSource {
	@Override
	public List<OperationSchedule> getOperationSchedules()
			throws WebAPIException {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
		}
		throw new WebAPIException("error");
	}
};
