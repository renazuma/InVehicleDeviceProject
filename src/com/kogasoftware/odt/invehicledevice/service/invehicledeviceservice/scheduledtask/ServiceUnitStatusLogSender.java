package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.scheduledtask;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData.Phase;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.logic.OperationScheduleLogic;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.logic.ServiceUnitStatusLogLogic;

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
		if (operationScheduleLogic.getPhaseWithReadLock() == Phase.FINISH) {
			return;
		}
		serviceUnitStatusLogLogic.send();
	}
}
