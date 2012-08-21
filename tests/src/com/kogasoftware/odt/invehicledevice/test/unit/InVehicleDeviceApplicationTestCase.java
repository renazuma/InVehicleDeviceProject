package com.kogasoftware.odt.invehicledevice.test.unit;

import com.kogasoftware.odt.invehicledevice.InVehicleDeviceApplication;

import android.test.ApplicationTestCase;

public class InVehicleDeviceApplicationTestCase extends ApplicationTestCase<InVehicleDeviceApplication> {
	public InVehicleDeviceApplicationTestCase() {
		super(InVehicleDeviceApplication.class);
	}
	
	public void testOnCreate() {
		createApplication();
		getApplication().onCreate();
	}
	
	public void testOnTerminate() {
		createApplication();
		getApplication().onCreate();
		getApplication().onTerminate();
	}
}

