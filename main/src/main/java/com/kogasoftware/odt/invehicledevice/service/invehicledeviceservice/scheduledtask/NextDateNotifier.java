package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.scheduledtask;

import java.util.Calendar;
import java.util.Date;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.logic.OperationScheduleLogic;

public class NextDateNotifier implements Runnable {
	public static final long RUN_INTERVAL_MILLIS = 60 * 1000;

	protected static Date createNextUpdateDate() {
		Calendar now = Calendar.getInstance();
		now.setTime(InVehicleDeviceService.getDate());
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH),
				now.get(Calendar.DAY_OF_MONTH),
				InVehicleDeviceService.NEW_SCHEDULE_DOWNLOAD_HOUR,
				InVehicleDeviceService.NEW_SCHEDULE_DOWNLOAD_MINUTE);
		if (!calendar.after(now)) {
			calendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		return calendar.getTime();
	}

	protected final OperationScheduleLogic operationScheduleLogic;
	protected Date nextUpdateDate = createNextUpdateDate();

	public NextDateNotifier(OperationScheduleLogic operationScheduleLogic) {
		this.operationScheduleLogic = operationScheduleLogic;
	}

	@Override
	public void run() {
		if (nextUpdateDate.after(InVehicleDeviceService.getDate())) {
			return;
		}
		nextUpdateDate = createNextUpdateDate();
		operationScheduleLogic.startNewOperation(true);
	}
}
