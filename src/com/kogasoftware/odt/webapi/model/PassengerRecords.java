package com.kogasoftware.odt.webapi.model;

public class PassengerRecords {
	public static class Status {
		public static final Integer UNHANDLED = 0;
		public static final Integer RIDING = 1;
		public static final Integer GOT_OFF = 2;
	}

	public static Boolean isGotOff(Reservation reservation) {
		return isStatus(reservation, Status.GOT_OFF);
	}

	public static Boolean isRiding(Reservation reservation) {
		return isStatus(reservation, Status.RIDING);
	}

	public static Boolean isGotOff(PassengerRecord passengerRecord) {
		return isStatus(passengerRecord, Status.GOT_OFF);
	}

	public static Boolean isRiding(PassengerRecord passengerRecord) {
		return isStatus(passengerRecord, Status.RIDING);
	}
	
	private static Boolean isStatus(Reservation reservation, Integer status) {
		return reservation.getPassengerRecord().isPresent()
				&& isStatus(reservation.getPassengerRecord().get(), status);
	}

	private static Boolean isStatus(PassengerRecord passengerRecord,
			Integer status) {
		return passengerRecord.getStatus().equals(status);
	}

	public static Boolean isUnhandled(Reservation reservation) {
		return isStatus(reservation, Status.UNHANDLED);
	}
	
	public static Boolean isUnhandled(PassengerRecord passengerRecord) {
		return isStatus(passengerRecord, Status.UNHANDLED);
	}
}
