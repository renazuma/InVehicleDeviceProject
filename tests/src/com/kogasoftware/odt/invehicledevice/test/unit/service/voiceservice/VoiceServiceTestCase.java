package com.kogasoftware.odt.invehicledevice.test.unit.service.voiceservice;

import com.kogasoftware.odt.invehicledevice.service.startupservice.StartupService;

import android.content.Intent;
import android.test.ServiceTestCase;

public class VoiceServiceTestCase extends ServiceTestCase<StartupService> {
	public VoiceServiceTestCase() {
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

