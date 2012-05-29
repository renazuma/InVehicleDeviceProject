package com.kogasoftware.odt.invehicledevice.backgroundtask;

import java.util.Calendar;
import java.util.Date;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.event.NewOperationStartEvent;

public class NextDateChecker implements Runnable {
	private final CommonLogic commonLogic;
	private Date nextUpdateDate = createNextUpdateDate();
	
	public NextDateChecker(CommonLogic commonLogic) {
		this.commonLogic = commonLogic;
	}

	@Override
	public void run() {
		if (nextUpdateDate.after(CommonLogic.getDate())) {
			return;
		}
		nextUpdateDate = createNextUpdateDate();
		commonLogic.postEvent(new NewOperationStartEvent());
	}

	protected static Date createNextUpdateDate() {
		Calendar now = Calendar.getInstance();
		now.setTime(CommonLogic.getDate());
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH),
				now.get(Calendar.DAY_OF_MONTH),
				CommonLogic.NEW_SCHEDULE_DOWNLOAD_HOUR, 0);
		if (!calendar.after(now)) {
			calendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		return calendar.getTime();
	}
}
