package com.kogasoftware.odt.invehicledevice.service.voiceservice;

import com.kogasoftware.odt.invehicledevice.service.voiceservice.VoiceService;

import android.content.Intent;
import android.test.ServiceTestCase;

public class VoiceServiceTestCase extends ServiceTestCase<VoiceService> {
	public VoiceServiceTestCase() {
		super(VoiceService.class);
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

