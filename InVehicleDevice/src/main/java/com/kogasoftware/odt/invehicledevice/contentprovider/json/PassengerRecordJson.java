package com.kogasoftware.odt.invehicledevice.contentprovider.json;

import org.joda.time.DateTime;

public class PassengerRecordJson {
	public Long id;
	public Long reservationId;
	public DateTime getOnTime;
	public DateTime getOffTime;
	public Long userId;
	public Boolean ignoreGetOnMiss;
	public Boolean ignoreGetOffMiss;
}
