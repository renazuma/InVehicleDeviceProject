package com.kogasoftware.odt.invehicledevice.test.unit.service.startupservice;

import com.kogasoftware.odt.invehicledevice.service.startupservice.StartupService;

import android.content.Intent;
import android.test.ServiceTestCase;

public class StartupServiceTestCase extends ServiceTestCase<StartupService> {
	public StartupServiceTestCase() {
		super(StartupService.class);
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

