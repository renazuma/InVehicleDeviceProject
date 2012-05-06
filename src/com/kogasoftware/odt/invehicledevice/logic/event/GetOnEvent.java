package com.kogasoftware.odt.invehicledevice.logic.event;

import java.util.List;

import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;

public class GetOnEvent {
	public final OperationSchedule operationSchedule;
	public final List<PassengerRecord> getOnPassengerRecords;

	public GetOnEvent(OperationSchedule operationSchedule,
			List<PassengerRecord> selectedGetOnPassengerRecords) {
		this.operationSchedule = operationSchedule;
		this.getOnPassengerRecords = selectedGetOnPassengerRecords;
	}
}
