package com.kogasoftware.odt.invehicledevice.test.unit.apiclient;

import android.test.InstrumentationTestCase;

import com.kogasoftware.odt.invehicledevice.apiclient.InVehicleDeviceApiClient;
import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;

public class InVehicleDeviceApiClientTestCase extends InstrumentationTestCase {
	public void testSmallInstance() throws Exception {
		TestUtil.assertEmptyObject(getInstrumentation(), InVehicleDeviceApiClient.class);
	}
}
