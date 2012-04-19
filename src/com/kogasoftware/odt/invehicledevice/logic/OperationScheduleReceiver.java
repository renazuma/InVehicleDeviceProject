package com.kogasoftware.odt.invehicledevice.logic;

import java.util.LinkedList;
import java.util.List;

import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Writer;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.Reservation;

public class OperationScheduleReceiver implements Runnable {
	final Logic logic;

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
			try {
				Thread.sleep(5000); // TODO
			} catch (InterruptedException e) {
				return;
			}
		}
		logic.getStatusAccess().write(new Writer() {
			@Override
			public void write(Status status) {
				status.operationSchedules.clear();
				status.operationSchedules.addAll(operationSchedules);
				status.unhandledPassengerRecords.clear();
				status.ridingPassengerRecords.clear();
				for (OperationSchedule operationSchedule : operationSchedules) {
					for (Reservation reservation : operationSchedule
							.getReservationsAsDeparture()) {
						PassengerRecord passengerRecord = new PassengerRecord();
						passengerRecord.setReservation(reservation);
						status.unhandledPassengerRecords.add(passengerRecord);
					}
				}
				status.initialized = true;
			}
		});
	}
}
