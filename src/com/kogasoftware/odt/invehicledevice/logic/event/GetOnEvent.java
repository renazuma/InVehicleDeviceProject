package com.kogasoftware.odt.invehicledevice.logic.event;

import java.util.List;

import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.Reservation;

public class GetOnEvent {
	public final OperationSchedule operationSchedule;
	public final List<Reservation> reservations;

	public GetOnEvent(OperationSchedule operationSchedule,
			List<Reservation> reservations) {
		this.operationSchedule = operationSchedule;
		this.reservations = reservations;
	}
}
