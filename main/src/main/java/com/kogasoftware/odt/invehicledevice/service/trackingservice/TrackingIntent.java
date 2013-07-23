package com.kogasoftware.odt.invehicledevice.service.trackingservice;

import android.content.Intent;
import android.location.Location;

public class TrackingIntent extends Intent {
	public static final String ACTION_TRACKING = TrackingIntent.class.getName()
			+ ".ACTION_TRACKING";
	private final String LOCATION_KEY = "location";
	private final String SATELLITES_COUNT_KEY = "satellites_count";

	public TrackingIntent() {
		this(new Location(""));
	}

	public TrackingIntent(Location location) {
		this(location, 0);
	}

	public TrackingIntent(Location location, Integer satellitesCount) {
		setAction(ACTION_TRACKING);
		setLocation(location);
		setSatellitesCount(satellitesCount);
	}

	public void setSatellitesCount(Integer satellitesCount) {
		putExtra(SATELLITES_COUNT_KEY, satellitesCount);
	}

	public void setLocation(Location location) {
		putExtra(LOCATION_KEY, location);
	}

	public TrackingIntent(Intent intent) {
		setAction(ACTION_TRACKING);
		putExtras(intent);
	}

	public Location getLocation() {
		return getExtras().getParcelable(LOCATION_KEY);
	}

	public Integer getSatellitesCount() {
		return getIntExtra(SATELLITES_COUNT_KEY, 0);
	}
}
