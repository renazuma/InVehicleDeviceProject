package com.kogasoftware.odt.invehicledevice.test.unit.service.startupservice;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.location.LocationManager;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.Settings;
import android.test.ServiceTestCase;

import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.service.startupservice.StartupService;
import com.kogasoftware.odt.invehicledevice.test.util.DelegateMockContext;
import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;
import com.kogasoftware.odt.invehicledevice.ui.activity.EmptyActivity;
import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;

public class StartupServiceTestCase extends ServiceTestCase<StartupService> {
	PowerManager powerManager;
	LocationManager locationManager;
	ActivityManager activityManager;

	public StartupServiceTestCase() {
		super(StartupService.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();

		powerManager = mock(PowerManager.class);
		when(powerManager.isScreenOn()).thenReturn(true);

		locationManager = mock(LocationManager.class);
		when(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
				.thenReturn(true);

		activityManager = mock(ActivityManager.class);

		Context mockContext = spy(new DelegateMockContext(getSystemContext()));
		when(mockContext.getSystemService(Context.POWER_SERVICE)).thenReturn(
				powerManager);
		when(mockContext.getSystemService(Context.LOCATION_SERVICE))
				.thenReturn(locationManager);
		when(mockContext.getSystemService(Context.ACTIVITY_SERVICE))
				.thenReturn(activityManager);

		setContext(mockContext);
		setupService();

		Settings.System.putInt(getSystemContext().getContentResolver(),
				Settings.System.AIRPLANE_MODE_ON, 0);
	}

	@Override
	public void tearDown() throws Exception {
		try {
			Settings.System.putInt(getSystemContext().getContentResolver(),
					Settings.System.AIRPLANE_MODE_ON, 0);
		} finally {
			super.tearDown();
		}
	}

	public void testIsDeviceReady_スクリーンがOFFの場合() {
		when(powerManager.isScreenOn()).thenReturn(false);
		assertFalse(getService().isDeviceReady());
	}

	public void testIsDeviceReady_機内モードがONの場合() {
		Settings.System.putInt(getSystemContext().getContentResolver(),
				Settings.System.AIRPLANE_MODE_ON, 1);
		assertFalse(getService().isDeviceReady());
	}

	public void testIsDeviceReady_ExternalStorageが無効の場合() {
		assertFalse(getService().isDeviceReady(Environment.MEDIA_UNMOUNTED));
		assertFalse(getService().isDeviceReady(Environment.MEDIA_REMOVED));
		assertFalse(getService().isDeviceReady(Environment.MEDIA_NOFS));
		assertTrue(getService().isDeviceReady(Environment.MEDIA_MOUNTED));
	}

	public void testIsDeviceReady_GPSが無効の場合() {
		when(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
				.thenReturn(false);
		if (StartupService.isGpsRequired()) {
			assertFalse(getService().isDeviceReady());
		} else {
			assertTrue(getService().isDeviceReady());
		}
	}

	public void testIsDeviceReady_InVehicleDeviceActivityがすでに前面にある場合() {
		RunningTaskInfo rti = new RunningTaskInfo();
		rti.topActivity = new ComponentName(getSystemContext(),
				InVehicleDeviceActivity.class);
		when(activityManager.getRunningTasks(anyInt())).thenReturn(
				Lists.<RunningTaskInfo> newArrayList(rti));
		assertFalse(getService().isDeviceReady());

		rti.topActivity = new ComponentName(getSystemContext(),
				EmptyActivity.class);
		assertTrue(getService().isDeviceReady());
	}

	public void testIsDeviceReady_起動条件がそろっている場合() {
		assertTrue(getService().isDeviceReady());
		getService().onCreate();
		getService().checkDeviceAndStartActivity();
		TestUtil.assertShow(getSystemContext(), InVehicleDeviceActivity.class);
	}

	public void testIsGpsRequired() {
		assertTrue(StartupService.isGpsRequired(true, "my android"));
		assertTrue(StartupService.isGpsRequired(true, ""));

		assertFalse(StartupService.isGpsRequired(true, "androVM for VirtualBox ('Phone' version)"));
		assertTrue(StartupService.isGpsRequired(false, "androVM for VirtualBox ('Phone' version)"));
		assertTrue(StartupService.isGpsRequired(true, "!androVM for VirtualBox ('Phone' version)"));

		assertFalse(StartupService.isGpsRequired(true, "Buildroid for VirtualBox ('Tablet' version with phone caps)"));
		assertTrue(StartupService.isGpsRequired(false, "Buildroid for VirtualBox ('Tablet' version with phone caps)"));
		assertTrue(StartupService.isGpsRequired(true, "!Buildroid for VirtualBox ('Tablet' version with phone caps)"));
	}
}

