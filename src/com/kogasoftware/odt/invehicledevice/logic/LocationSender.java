package com.kogasoftware.odt.invehicledevice.logic;

import android.location.Location;

import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Reader;

public class LocationSender implements Runnable {
	private final Logic logic;

	public LocationSender(Logic logic) {
		this.logic = logic;
	}

	@Override
	public void run() {
		Optional<Location> location = logic.getStatusAccess().read(
				new Reader<Optional<Location>>() {
					@Override
					public Optional<Location> read(Status status) {
						return status.location;
					}
				});
		if (!location.isPresent()) {
			return;
		}
	}
}
