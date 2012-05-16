package com.kogasoftware.odt.invehicledevice.logic.event;

import java.util.List;

import com.kogasoftware.odt.webapi.model.Reservation;

public class SelectedReservationsUpdateEvent {
	public final List<Reservation> reservations;

	public SelectedReservationsUpdateEvent(List<Reservation> reservations) {
		this.reservations = reservations;
	}
}
