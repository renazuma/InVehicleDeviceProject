package com.kogasoftware.odt.invehicledevice.logic;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Reader;

public class LocationSender extends LogicUser implements Runnable,
		LocationListener {

	@Override
	public void onLocationChanged(Location location) {
		if (getLogic().isPresent()) {
			getLogic().get().setLocation(location);
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public void run() {
		if (!getLogic().isPresent()) {
			return;
		}
		final Logic logic = getLogic().get();

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
