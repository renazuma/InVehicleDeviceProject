package com.kogasoftware.odt.invehicledevice.test.util;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;

import com.kogasoftware.odt.invehicledevice.service.startupservice.StartupService;
import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;

public class TestUtilAutoStartTestCase extends
		ActivityInstrumentationTestCase2<InVehicleDeviceActivity> {

	private static final String TAG = TestUtilAutoStartTestCase.class
			.getSimpleName();
	Context c;

	public TestUtilAutoStartTestCase() {
		super("com.kogasoftware.odt.invehicledevice",
				InVehicleDeviceActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		c = getInstrumentation().getContext();
	}

	@Override
	public void tearDown() throws Exception {
		super.tearDown();
	}

	public void callTestDisableAutoStart() throws Exception {
		// ActivityのUI起動後、自動起動をOFFにしてActivityを終了
		InVehicleDeviceActivity a = getActivity();
		TestUtil.waitForStartUI(a);
		TestUtil.disableAutoStart(c);
		a.finish();
		a = null;
		Thread.sleep(5 * 1000);

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

	// 二度目以降の実行に失敗することがあるので、複数回実行する
	public void testDisableAutoStart2() throws Exception {
		callTestDisableAutoStart();
	}

	// 二度目以降の実行に失敗することがあるので、複数回実行する
	public void testDisableAutoStart3() throws Exception {
		callTestDisableAutoStart();
	}
}
