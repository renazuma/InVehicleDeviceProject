package com.kogasoftware.odt.invehicledevice.service.healthcheckservice;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import android.app.ActivityManager;
import android.content.Context;
import android.location.LocationManager;
import android.os.PowerManager;
import android.test.ServiceTestCase;

public class healthcheckServiceTestCase extends ServiceTestCase<HealthCheckService> {
	PowerManager powerManager;
	LocationManager locationManager;
	ActivityManager activityManager;

	public healthcheckServiceTestCase() {
		super(HealthCheckService.class);
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
		if (HealthCheckService.isGpsRequired()) {
			assertFalse(getService().isDeviceReady());
		} else {
			assertTrue(getService().isDeviceReady());
		}
	}

	public void xtestIsGpsRequired() {
		assertTrue(HealthCheckService.isGpsRequired(true, "my android"));
		assertTrue(HealthCheckService.isGpsRequired(true, ""));

		assertFalse(HealthCheckService.isGpsRequired(true,
				"androVM for VirtualBox ('Phone' version)"));
		assertTrue(HealthCheckService.isGpsRequired(false,
				"androVM for VirtualBox ('Phone' version)"));
		assertTrue(HealthCheckService.isGpsRequired(true,
				"!androVM for VirtualBox ('Phone' version)"));

		assertFalse(HealthCheckService.isGpsRequired(true,
				"Buildroid for VirtualBox ('Tablet' version with phone caps)"));
		assertTrue(HealthCheckService.isGpsRequired(false,
				"Buildroid for VirtualBox ('Tablet' version with phone caps)"));
		assertTrue(HealthCheckService.isGpsRequired(true,
				"!Buildroid for VirtualBox ('Tablet' version with phone caps)"));
	}
}
