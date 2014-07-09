package com.kogasoftware.odt.invehicledevice.contentprovider.json;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

public class OperationScheduleJson {
	public Long id;
	public PlatformJson platform;
	public ReservationJson departureReservation;
	public ReservationJson arrivalReservation;
	public DateTime departureEstimate;
	public DateTime arrivalEstimate;
	public OperationRecordJson operationRecord;
	public LocalDate operationDate;
}
