package com.kogasoftware.odt.invehicledevice.apiclient;

public class InVehicleDeviceApiClientFactory {
	public static InVehicleDeviceApiClient newInstance(String host) {
		return new DefaultInVehicleDeviceApiClient(host);
	}
}
