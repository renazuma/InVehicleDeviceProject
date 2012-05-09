package com.kogasoftware.odt.invehicledevice.logic.event;

import java.util.List;

import com.kogasoftware.odt.webapi.model.PassengerRecord;

public class SelectedPassengerRecordsUpdateEvent {
	public final List<PassengerRecord> selectedPassengerRecords;

	public SelectedPassengerRecordsUpdateEvent(
			List<PassengerRecord> selectedPassengerRecords) {
		this.selectedPassengerRecords = selectedPassengerRecords;
	}

}
