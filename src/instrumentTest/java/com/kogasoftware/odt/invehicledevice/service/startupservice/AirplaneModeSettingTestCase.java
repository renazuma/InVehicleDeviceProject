package com.kogasoftware.odt.invehicledevice.service.startupservice;

import java.io.IOException;

import com.kogasoftware.odt.invehicledevice.service.startupservice.AirplaneModeSetting;

import android.content.Context;
import android.test.AndroidTestCase;

public class AirplaneModeSettingTestCase extends AndroidTestCase {
	public void test() throws IOException {
		Context c = getContext();
		AirplaneModeSetting.set(c, false);
		assertFalse(AirplaneModeSetting.get(c));
		AirplaneModeSetting.set(c, true);
		assertTrue(AirplaneModeSetting.get(c));
		AirplaneModeSetting.set(c, false);
		assertFalse(AirplaneModeSetting.get(c));
	}
}
