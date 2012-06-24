package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.backgroundthread;

import java.util.Calendar;
import java.util.Date;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;

public class NextDateNotifier implements Runnable {
	protected static Date createNextUpdateDate() {
		Calendar now = Calendar.getInstance();
		now.setTime(InVehicleDeviceService.getDate());
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH),
				now.get(Calendar.DAY_OF_MONTH),
				InVehicleDeviceService.NEW_SCHEDULE_DOWNLOAD_HOUR, 0);
		if (!calendar.after(now)) {
			calendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		return calendar.getTime();
	}

	protected final InVehicleDeviceService service;
	protected Date nextUpdateDate = createNextUpdateDate();

	public NextDateNotifier(InVehicleDeviceService service) {
		this.service = service;
	}

	@Override
	public void run() {
		if (nextUpdateDate.after(InVehicleDeviceService.getDate())) {
			return;
		}
		nextUpdateDate = createNextUpdateDate();
		service.startNewOperation();
	}
}
