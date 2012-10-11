package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.backgroundtask;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData.Phase;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.OperationScheduleLogic;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.ServiceUnitStatusLogLogic;

public class ServiceUnitStatusLogSender implements Runnable {
	ServiceUnitStatusLogLogic serviceUnitStatusLogLogic;
	OperationScheduleLogic operationScheduleLogic;

	public ServiceUnitStatusLogSender(
			ServiceUnitStatusLogLogic serviceUnitStatusLogLogic,
			OperationScheduleLogic operationScheduleLogic) {
		this.serviceUnitStatusLogLogic = serviceUnitStatusLogLogic;
		this.operationScheduleLogic = operationScheduleLogic;
	}

	/**
	 * 現在のServiceUnitStatusLogをサーバーへ送信
	 */
	@Override
	public void run() {
		if (operationScheduleLogic.getPhase() == Phase.FINISH) {
			return;
		}
		serviceUnitStatusLogLogic.send();
	}
}
