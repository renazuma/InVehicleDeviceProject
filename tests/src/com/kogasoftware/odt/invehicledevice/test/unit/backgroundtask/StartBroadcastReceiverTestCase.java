package com.kogasoftware.odt.invehicledevice.test.unit.backgroundtask;

import android.app.Activity;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;
import com.kogasoftware.odt.invehicledevice.service.LauncherService.StartBroadcastReceiver;
import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;

public class StartBroadcastReceiverTestCase extends
		ActivityInstrumentationTestCase2<InVehicleDeviceActivity> {

	Solo solo;
	StartBroadcastReceiver sbr;

	public StartBroadcastReceiverTestCase() {
		super("com.kogasoftware.odt.invehicledevice",
				InVehicleDeviceActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation());
		sbr = new StartBroadcastReceiver();
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
	}

	public void testInVehicleDeviceActivityを起動() throws Exception {
		for (Activity a : solo.getAllOpenedActivities()) {
			assertNotSame(InVehicleDeviceActivity.class, a.getClass());
		}
		sbr.onReceive(getInstrumentation().getTargetContext()
				.getApplicationContext(), new Intent(
				Intent.ACTION_POWER_CONNECTED));
		assertTrue(solo.waitForActivity(InVehicleDeviceActivity.class
				.getSimpleName()));
	}

	public void testすでにInVehicleDeviceActivity起動していた場合は新しいActivityを起動しない()
			throws Exception {
		Activity a1 = getActivity();
		solo.sleep(2000);
		sbr.onReceive(getInstrumentation().getTargetContext()
				.getApplicationContext(), new Intent(Intent.ACTION_SEND));
		solo.sleep(2000);
		Activity a2 = solo.getCurrentActivity();
		assertTrue(a1 == a2);
	}
}
