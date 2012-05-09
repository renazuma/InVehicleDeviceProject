package com.kogasoftware.odt.invehicledevice.logic.event;

import android.location.Location;

public class LocationReceivedEvent {
	public final Location location;

	public LocationReceivedEvent(Location location) {
		this.location = location;
	}
}
