package com.kogasoftware.odt.invehicledevice.contentprovider.json;

import java.util.List;

import com.google.common.collect.Lists;

public class ReservationJson {
	public Long id;
	public Long userId;
	public Long arrivalScheduleId;
	public Long departureScheduleId;
	public List<UserJson> fellowUsers = Lists.newLinkedList();
	public List<PassengerRecordJson> passengerRecords = Lists.newLinkedList();
	public String memo;
	public Integer passengerCount;
}
