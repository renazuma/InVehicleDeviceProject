package com.kogasoftware.odt.invehicledevice.service.startupservice;

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
import android.test.ServiceTestCase;

import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.view.activity.EmptyActivity;
import com.kogasoftware.odt.invehicledevice.view.activity.InVehicleDeviceActivity;

public class StartupServiceTestCase extends ServiceTestCase<StartupService> {
	PowerManager powerManager;
	LocationManager locationManager;
	ActivityManager activityManager;

	public StartupServiceTestCase() {
		super(StartupService.class);
	}

	public void test() {
		// stub
	}

	public void xsetUp() throws Exception {
		super.setUp();

		powerManager = mock(PowerManager.class);
		when(powerManager.isScreenOn()).thenReturn(true);

		locationManager = mock(LocationManager.class);
		when(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
				.thenReturn(true);

		activityManager = mock(ActivityManager.class);

		Context mockContext = spy(getSystemContext());
		when(mockContext.getSystemService(Context.POWER_SERVICE)).thenReturn(
				powerManager);
		when(mockContext.getSystemService(Context.LOCATION_SERVICE))
				.thenReturn(locationManager);
		when(mockContext.getSystemService(Context.ACTIVITY_SERVICE))
				.thenReturn(activityManager);

		setContext(mockContext);
		setupService();
	}

	public void xtestIsDeviceReady_スクリーンがOFFの場合() {
		when(powerManager.isScreenOn()).thenReturn(false);
		assertFalse(getService().isDeviceReady());
	}

	public void xtestIsDeviceReady_GPSが無効の場合() {
		when(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
				.thenReturn(false);
		if (StartupService.isGpsRequired()) {
			assertFalse(getService().isDeviceReady());
		} else {
			assertTrue(getService().isDeviceReady());
		}
	}

	public void xtestIsGpsRequired() {
		assertTrue(StartupService.isGpsRequired(true, "my android"));
		assertTrue(StartupService.isGpsRequired(true, ""));

		assertFalse(StartupService.isGpsRequired(true,
				"androVM for VirtualBox ('Phone' version)"));
		assertTrue(StartupService.isGpsRequired(false,
				"androVM for VirtualBox ('Phone' version)"));
		assertTrue(StartupService.isGpsRequired(true,
				"!androVM for VirtualBox ('Phone' version)"));

		assertFalse(StartupService.isGpsRequired(true,
				"Buildroid for VirtualBox ('Tablet' version with phone caps)"));
		assertTrue(StartupService.isGpsRequired(false,
				"Buildroid for VirtualBox ('Tablet' version with phone caps)"));
		assertTrue(StartupService.isGpsRequired(true,
				"!Buildroid for VirtualBox ('Tablet' version with phone caps)"));
	}
}
