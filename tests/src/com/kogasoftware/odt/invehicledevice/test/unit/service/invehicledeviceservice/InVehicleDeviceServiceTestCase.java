package com.kogasoftware.odt.invehicledevice.test.unit.service.invehicledeviceservice;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;

import android.content.Intent;
import android.test.ServiceTestCase;

public class InVehicleDeviceServiceTestCase extends
		ServiceTestCase<InVehicleDeviceService> {

	public InVehicleDeviceServiceTestCase() {
		super(InVehicleDeviceService.class);
	}

	public void testOnCreate() {
		startService(new Intent());
		getService().onCreate();
	}
	
	public void testOnDestroy() {
		startService(new Intent());
		getService().onCreate();
		getService().onDestroy();
	}
}
