package com.kogasoftware.odt.invehicledevice.logic.event;

import com.kogasoftware.odt.webapi.model.Reservation;

public class ReturnPathReservationCreatedEvent {
	public final Reservation reservation;
	public ReturnPathReservationCreatedEvent(Reservation reservation) {
		this.reservation = reservation;
	}
}
