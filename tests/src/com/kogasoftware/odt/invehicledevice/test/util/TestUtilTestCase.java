package com.kogasoftware.odt.invehicledevice.test.util;

import java.util.List;

import junit.framework.AssertionFailedError;
import android.app.Activity;
import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;
import com.kogasoftware.odt.invehicledevice.datasource.EmptyDataSource;
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
		Solo s = new Solo(getInstrumentation(), getActivity());
		TestUtil.disableAutoStart(c);
		s.finishOpenedActivities();
		
		Thread.sleep(5000);
		try {
			s.waitForActivity("InVehicleDeviceActivity", 20 * 1000);
			throw new RuntimeException("");
		} catch (AssertionFailedError e) {
		}
		TestUtil.enableAutoStart(c);
		assertTrue(s.waitForActivity("InVehicleDeviceActivity", 20 * 1000));
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
