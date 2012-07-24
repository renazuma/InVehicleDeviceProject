package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.backgroundthread;

import com.kogasoftware.odt.invehicledevice.datasource.DataSource;
import com.kogasoftware.odt.invehicledevice.empty.EmptyWebAPICallback;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData.Phase;
import com.kogasoftware.odt.webapi.model.ServiceUnitStatusLog;

public class ServiceUnitStatusLogSender implements Runnable {
	protected final InVehicleDeviceService service;

	public ServiceUnitStatusLogSender(InVehicleDeviceService service) {
		this.service = service;
	}

	/**
	 * 現在のServiceUnitStatusLogをサーバーへ送信
	 */
	@Override
	public void run() {
		if (service.getPhase() == Phase.FINISH) {
			return;
		}
		final ServiceUnitStatusLog serviceUnitStatusLog = service
				.getServiceUnitStatusLog();
		DataSource dataSource = service.getRemoteDataSource();
		dataSource.saveOnClose(dataSource.sendServiceUnitStatusLog(serviceUnitStatusLog,
				new EmptyWebAPICallback<ServiceUnitStatusLog>()));
	}
}
