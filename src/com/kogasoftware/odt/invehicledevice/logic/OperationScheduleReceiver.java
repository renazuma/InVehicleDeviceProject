package com.kogasoftware.odt.invehicledevice.logic;

import java.util.LinkedList;
import java.util.List;

import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Writer;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.OperationSchedule;

public class OperationScheduleReceiver implements Runnable {
	private final Logic logic;

	public OperationScheduleReceiver(Logic logic) {
		this.logic = logic;
	}

	@Override
	public void run() {
		final List<OperationSchedule> operationSchedules = new LinkedList<OperationSchedule>();
		while (true) {
			try {
				operationSchedules.addAll(logic.getDataSource()
						.getOperationSchedules());
				break;
			} catch (WebAPIException e) {
				e.printStackTrace(); // TODO
			}
		}
		logic.getStatusAccess().write(new Writer() {
			@Override
			public void write(Status status) {
				status.operationSchedules.clear();
				status.operationSchedules.addAll(operationSchedules);
				status.initialized.set(true);
			}
		});
	}
}