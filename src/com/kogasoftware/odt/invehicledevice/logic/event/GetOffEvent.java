package com.kogasoftware.odt.invehicledevice.logic.event;

import java.util.List;

import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;

public class GetOffEvent {
	public final OperationSchedule operationSchedule;
	public final List<PassengerRecord> getOffPassengerRecords;

	public GetOffEvent(OperationSchedule operationSchedule,
			List<PassengerRecord> selectedGetOffPassengerRecords) {
		this.operationSchedule = operationSchedule;
		this.getOffPassengerRecords = selectedGetOffPassengerRecords;
	}
}
