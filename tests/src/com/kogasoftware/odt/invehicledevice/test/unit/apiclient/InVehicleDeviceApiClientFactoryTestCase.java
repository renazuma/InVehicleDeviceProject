package com.kogasoftware.odt.invehicledevice.test.unit.apiclient;

import android.test.AndroidTestCase;

import com.kogasoftware.odt.invehicledevice.apiclient.EmptyInVehicleDeviceApiClient;
import com.kogasoftware.odt.invehicledevice.apiclient.InVehicleDeviceApiClient;
import com.kogasoftware.odt.invehicledevice.apiclient.InVehicleDeviceApiClientFactory;
import com.kogasoftware.odt.invehicledevice.empty.EmptyFile;

public class InVehicleDeviceApiClientFactoryTestCase extends AndroidTestCase {
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testNewInstance() throws Exception {
		assertTrue(InVehicleDeviceApiClientFactory.newInstance() instanceof EmptyInVehicleDeviceApiClient);
		assertTrue(InVehicleDeviceApiClientFactory.newInstance(
				"http://localhost", "foo", new EmptyFile()) instanceof InVehicleDeviceApiClient);
	}
}
