package com.kogasoftware.odt.invehicledevice.test.util;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;

import com.kogasoftware.odt.invehicledevice.apiclient.DataSource;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.SharedPreferencesKeys;
import com.kogasoftware.odt.invehicledevice.test.util.apiclient.DummyDataSource;
import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;
import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;
import com.kogasoftware.odt.webapi.model.OperationSchedule;

public class TestUtilWaitForStartUiTestCase extends
		ActivityInstrumentationTestCase2<InVehicleDeviceActivity> {
	InVehicleDeviceActivity a;
	Context c;
	Context tc;

	public void callTestWaitForStartUi(Boolean timeout) throws Exception {
		DataSource ds = new DummyDataSource();
		if (timeout) {
			ds = new DummyDataSource() {
				@Override
				public int getOperationSchedules(
						WebAPICallback<List<OperationSchedule>> callback) {
					callback.onFailed(0, 401, "");
					return 0;
				}
			};
		}

		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(tc).edit();
		editor.clear();
		editor.putBoolean(SharedPreferencesKeys.INITIALIZED, true);
		editor.apply();

		TestUtil.clearStatus();
		TestUtil.setDataSource(ds);
		a = getActivity();
		assertEquals(!timeout, TestUtil.waitForStartUI(a).booleanValue());
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

	public TestUtilWaitForStartUiTestCase() {
		super("com.kogasoftware.odt.invehicledevice",
				InVehicleDeviceActivity.class);

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
}
