package com.kogasoftware.odt.invehicledevice.service.trackingservice;

import com.google.common.base.Optional;

import android.content.Intent;
import android.location.Location;

public class TrackingIntent extends Intent {
	public static final String ACTION_TRACKING = TrackingIntent.class.getName()
			+ ".ACTION_TRACKING";
	private final String LOCATION_KEY = "location";
	private final String SATELLITES_COUNT_KEY = "satellites_count";

	public TrackingIntent() {
		this(0);
	}

	public TrackingIntent(Intent intent) {
		setAction(ACTION_TRACKING);
		putExtras(intent);
	}

	public TrackingIntent(Integer satellitesCount) {
		setAction(ACTION_TRACKING);
		setSatellitesCount(satellitesCount);
	}

	public TrackingIntent(Integer satellitesCount, Location location) {
		setAction(ACTION_TRACKING);
		setSatellitesCount(satellitesCount);
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
}
