package com.kogasoftware.odt.invehicledevice.service.trackingservice;

import java.util.Locale;

import com.google.common.base.Objects;
import com.google.common.base.Optional;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

public class TrackingIntent extends Intent {
	public static final String ACTION_TRACKING = TrackingIntent.class.getName()
			+ ".ACTION_TRACKING";
	private final String LOCATION_KEY = "location";
	private final String SATELLITES_COUNT_KEY = "satellites_count";
	private final String USED_IN_FIX_SATELLITES_COUNT_KEY = "used_in_fix_satellites_count";

	public TrackingIntent() {
		setAction(ACTION_TRACKING);
		putExtras(new Bundle());
	}

	public TrackingIntent(Intent intent) {
		setAction(ACTION_TRACKING);
		putExtras(intent);
	}

	public TrackingIntent(Location location) {
		setAction(ACTION_TRACKING);
		setLocation(location);
	}

	public void setSatellitesCount(Integer satellitesCount) {
		putExtra(SATELLITES_COUNT_KEY, satellitesCount);
	}

	public void setLocation(Location location) {
		putExtra(LOCATION_KEY, location);
	}

	public Optional<Location> getLocation() {
		return Optional.fromNullable((Location) getExtras().getParcelable(
				LOCATION_KEY));
	}

	public Integer getSatellitesCount() {
		return getIntExtra(SATELLITES_COUNT_KEY, 0);
	}

	public void setUsedInFixSatellitesCount(Integer satellitesCount) {
		putExtra(USED_IN_FIX_SATELLITES_COUNT_KEY, satellitesCount);
	}

	public Integer getUsedInFixSatellitesCount() {
		return getIntExtra(USED_IN_FIX_SATELLITES_COUNT_KEY, 0);
	}

	@Override
	public String toString() {
		String locationString = "";
		for (Location location : getLocation().asSet()) {
			locationString += String.format(Locale.US, "lat=%.8f, lon=%.8f",
					location.getLatitude(), location.getLongitude());
		}
		return Objects
				.toStringHelper(this)
				.addValue(locationString)
				.addValue(
						String.format("satellites=(%d/%d)",
								getUsedInFixSatellitesCount(),
								getSatellitesCount())).toString();
	}
}
