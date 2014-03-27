package com.kogasoftware.odt.invehicledevice.testutil;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;

import com.kogasoftware.odt.invehicledevice.apiclient.InVehicleDeviceApiClient;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.SharedPreferencesKeys;
import com.kogasoftware.odt.invehicledevice.testutil.apiclient.DummyApiClient;
import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;
import com.kogasoftware.odt.apiclient.ApiClientCallback;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule;

public class TestUtilWaitForStartUiTestCase extends
		ActivityInstrumentationTestCase2<InVehicleDeviceActivity> {
	InVehicleDeviceActivity a;
	Context c;
	Context tc;

	public void callTestWaitForStartUi(Boolean timeout) throws Exception {
		InVehicleDeviceApiClient ac;
		if (timeout) {
			ac = new DummyApiClient() {
				@Override
				public int getOperationSchedules(
						ApiClientCallback<List<OperationSchedule>> callback) {
					callback.onFailed(0, 401, "");
					return 0;
				}
			};
		} else {
			ac = new DummyApiClient();
		}

		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(tc).edit();
		editor.clear();
		editor.putBoolean(SharedPreferencesKeys.INITIALIZED, true);
		editor.apply();

		TestUtil.clearLocalStorage(getInstrumentation());
		TestUtil.setApiClient(ac);
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
		super(InVehicleDeviceActivity.class);
	}

	public void xtestWaitForStartUi1() throws Exception {
		callTestWaitForStartUi(false);
	}

	public void xtestWaitForStartUi1Timeout() throws Exception {
		callTestWaitForStartUi(true);
	}

	public void xtestWaitForStartUi2() throws Exception {
		callTestWaitForStartUi(false);
	}

	public void xtestWaitForStartUi2Timeout() throws Exception {
		callTestWaitForStartUi(true);
	}

	public void xtestWaitForStartUi3() throws Exception {
		callTestWaitForStartUi(false);
	}

	public void xtestWaitForStartUi3Timeout() throws Exception {
		callTestWaitForStartUi(true);
	}

	public void xtestWaitForStartUi4() throws Exception {
		callTestWaitForStartUi(false);
	}

	public void xtestWaitForStartUi4Timeout() throws Exception {
		callTestWaitForStartUi(true);
	}
}
