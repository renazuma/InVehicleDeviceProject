package com.kogasoftware.odt.invehicledevice.event;

import com.kogasoftware.odt.webapi.model.Reservation;

/**
 * 飛び乗り予約が追加されたことを通知
 */
public class AddUnexpectedReservationEvent {
	public final Reservation reservation;

	public AddUnexpectedReservationEvent(Reservation reservation) {
		this.reservation = reservation;
	}
}
