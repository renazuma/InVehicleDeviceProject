package com.kogasoftware.odt.webapi.model;

import com.kogasoftware.odt.webapi.model.base.OperationScheduleBase;

public class OperationSchedule extends OperationScheduleBase {
	private static final long serialVersionUID = 1040628741311146499L;

	public Boolean isDeparted() {
		for (OperationRecord operationRecord : getOperationRecord().asSet()) {
			return operationRecord.getDepartedAt().isPresent();
		}
		return false;
	}

	public Boolean isArrived() {
		for (OperationRecord operationRecord : getOperationRecord().asSet()) {
			return operationRecord.getArrivedAt().isPresent();
		}
		return false;
	}
}
