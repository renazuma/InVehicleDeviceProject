package com.kogasoftware.odt.invehicledevice.test.util;

import java.util.List;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;

import com.kogasoftware.odt.invehicledevice.datasource.DataSource;
import com.kogasoftware.odt.invehicledevice.service.startupservice.StartupService;
import com.kogasoftware.odt.invehicledevice.test.util.datasource.DummyDataSource;
import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.OperationSchedule;

public class TestUtilTestCase extends
		ActivityInstrumentationTestCase2<InVehicleDeviceActivity> {

	private static final String TAG = TestUtilTestCase.class.getSimpleName();
	InVehicleDeviceActivity a;

	public TestUtilTestCase() {
		super("com.kogasoftware.odt.invehicledevice",
				InVehicleDeviceActivity.class);
	}

	public void setUp() throws Exception {
		super.setUp();
		TestUtil.disableAutoStart(getInstrumentation().getContext());
		Thread.sleep(10 * 1000);
	}

	public void tearDown() throws Exception {
		try {
			if (a != null) {
				a.finish();
			}
			a = null;
			Context tc = getInstrumentation().getTargetContext();
			TestUtil.disableAutoStart(tc);
			TestUtil.exitService(tc);
		} finally {
			super.tearDown();
		}
	}

	public void callTestWaitForStartUi(Boolean timeout) throws Exception {
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

		TestUtil.clearStatus();
		TestUtil.setDataSource(ds);
		a = getActivity();
		assertEquals(!timeout, TestUtil.waitForStartUI(a).booleanValue());
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

	public void callTestDisableAutoStart() throws Exception {
		Context c = getInstrumentation().getContext();

		// ActivityのUI起動後、自動起動をOFFにしてActivityを終了
		a = getActivity();
		TestUtil.waitForStartUI(a);
		TestUtil.disableAutoStart(c);
		a.finish();
		a.moveTaskToBack(true);
		a = null;
		Thread.sleep(5000);

		// 最前面のActivityが車載器の場合失敗
		if (TestUtil.getTopActivity(c).getClassName()
				.equals(InVehicleDeviceActivity.class.getName())) {
			fail();
		}

		// 自動起動をON
		TestUtil.enableAutoStart(c);
		Thread.sleep(StartupService.CHECK_DEVICE_INTERVAL_MILLIS);
		Thread.sleep(5 * 1000);

		// 最前面のActivityが車載器の場合成功
		if (TestUtil.getTopActivity(c).getClassName()
				.equals(InVehicleDeviceActivity.class.getName())) {
			TestUtil.disableAutoStart(c);
			sendKeys(KeyEvent.KEYCODE_HOME);
			Thread.sleep(10 * 1000);
			return;
		}

		fail();
	}

	public void testDisableAutoStart1() throws Exception {
		callTestDisableAutoStart();
	}

	public void testDisableAutoStart2() throws Exception {
		callTestDisableAutoStart();
	}
}
