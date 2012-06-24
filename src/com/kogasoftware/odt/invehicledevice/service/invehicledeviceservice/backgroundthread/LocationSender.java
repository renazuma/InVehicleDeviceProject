package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.backgroundthread;

import com.kogasoftware.odt.invehicledevice.empty.EmptyWebAPICallback;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.webapi.model.ServiceUnitStatusLog;

public class LocationSender implements Runnable {
	protected final InVehicleDeviceService service;

	public LocationSender(InVehicleDeviceService service) {
		this.service = service;
	}

	/**
	 * 現在のServiceUnitStatusLogをサーバーへ送信
	 */
	@Override
	public void run() {
		final ServiceUnitStatusLog serviceUnitStatusLog = service
				.getServiceUnitStatusLog();
		service.getDataSource().sendServiceUnitStatusLog(serviceUnitStatusLog,
				new EmptyWebAPICallback<ServiceUnitStatusLog>());
	}
}
