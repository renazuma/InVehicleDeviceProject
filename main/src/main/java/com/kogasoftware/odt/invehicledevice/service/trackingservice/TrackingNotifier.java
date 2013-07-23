package com.kogasoftware.odt.invehicledevice.service.trackingservice;

import java.io.Closeable;
import org.joda.time.DateTimeUtils;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Iterables;
import android.content.Context;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

public class TrackingNotifier implements Runnable, LocationListener,
		GpsStatus.Listener, Closeable {
	private static final Integer DEFAULT_MIN_TIME = 1000;
	private static final Integer DEFAULT_MIN_DISTANCE = 1;
	private static final Integer DEFAULT_RESTART_TIMEOUT = 90 * 1000;
	private static final Integer DEFAULT_SLEEP_TIMEOUT = 20 * 1000;
	public static final Integer BROADCAST_PERIOD_MILLIS = 5000;
	private static final String TAG = TrackingNotifier.class.getSimpleName();

	private final Integer minTime = DEFAULT_MIN_TIME;
	private final Integer minDistance = DEFAULT_MIN_DISTANCE;
	private final Integer restartTimeout = DEFAULT_RESTART_TIMEOUT;
	private final Integer sleepTimeout = DEFAULT_SLEEP_TIMEOUT;
	private final Context context;
	private final TrackingIntent trackingIntent;
	private final LocationManager locationManager;

	private GpsStatus gpsStatus = null;
	private Boolean started = false;
	private Long startedTimeMillis = DateTimeUtils.currentTimeMillis();
	private Long stoppedTimeMillis = DateTimeUtils.currentTimeMillis();
	private Long lastRunTimeMillis = DateTimeUtils.currentTimeMillis();
	private Long lastLocationReceivedTimeMillis = 0L;

	public TrackingNotifier(Context context) {
		this.context = context;
		this.trackingIntent = new TrackingIntent();
		this.locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		start();
	}

	private void start() {
		if (started) {
			return;
		}
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Log.d(TAG,
					"start(): failed by !locationManager.isProviderEnabled()");
			return;
		}
		Log.d(TAG, "start()");

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				minTime, minDistance, this);
		locationManager.addGpsStatusListener(this);
		started = true;
		startedTimeMillis = DateTimeUtils.currentTimeMillis();
	}

	private void stop() {
		if (!started) {
			return;
		}
		Log.d(TAG, "stop()");
		locationManager.removeGpsStatusListener(this);
		locationManager.removeUpdates(this);
		stoppedTimeMillis = DateTimeUtils.currentTimeMillis();
		started = false;
	}

	public void run() {
		Long currentTimeMillis = DateTimeUtils.currentTimeMillis();
		if (started) {
			if (currentTimeMillis - lastLocationReceivedTimeMillis >= restartTimeout
					&& currentTimeMillis - startedTimeMillis >= restartTimeout) {
				stop();
			}
		} else {
			if (currentTimeMillis - stoppedTimeMillis >= sleepTimeout) {
				start();
			}
		}
		if (currentTimeMillis - lastRunTimeMillis >= BROADCAST_PERIOD_MILLIS) {
			sendBroadcast();
		}
		lastRunTimeMillis = currentTimeMillis;
	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.d(TAG, "onProviderDisabled(\"" + provider + "\")");
	}

	@Override
	public void onProviderEnabled(String provider) {
		Log.d(TAG, "onProviderEnabled(\"" + provider + "\")");
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		String message = "onStatusChanged(\"" + provider + "\", ";
		switch (status) {
		case LocationProvider.OUT_OF_SERVICE:
			message += "OUT_OF_SERVICE";
			break;
		case LocationProvider.AVAILABLE:
			message += "AVAILABLE";
			break;
		case LocationProvider.TEMPORARILY_UNAVAILABLE:
			message += "TEMPORARILY_UNAVAILABLE";
			break;
		default:
			message += "unknown:" + status;
			break;
		}
		Log.d(TAG, message + ", " + extras + ")");
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.d(TAG, "onLocationChanged() provider=" + location.getProvider()
				+ " hasAccuracy=" + location.hasAccuracy() + " accuracy="
				+ location.getAccuracy());
		lastLocationReceivedTimeMillis = DateTimeUtils.currentTimeMillis();
		trackingIntent.setLocation(location);
		sendBroadcast();
	}

	@Override
	public void onGpsStatusChanged(int event) {
		switch (event) {
		case GpsStatus.GPS_EVENT_STARTED:
			Log.d(TAG, "onGpsStatusChanged(STARTED)");
			break;
		case GpsStatus.GPS_EVENT_STOPPED:
			Log.d(TAG, "onGpsStatusChanged(STOPPED)");
			break;
		case GpsStatus.GPS_EVENT_FIRST_FIX:
			Log.d(TAG, "onGpsStatusChanged(FIRST_FIX)");
			break;
		case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
			// Log.d(TAG, "onGpsStatusChanged(SATELLITE_STATUS)");
			break;
		default:
			Log.d(TAG, "onGpsStatusChanged(unknown:" + event + ")");
			break;
		}
		gpsStatus = locationManager.getGpsStatus(gpsStatus); // onGpsStatusChanged()以外で呼ばないように注意する
		onSatellitesCountChanged(Iterables.size(gpsStatus.getSatellites()));
	}

	public void onSatellitesCountChanged(int satellitesCount) {
		Log.d(TAG, "onSatellitesCountChanged(" + satellitesCount + ")");
		trackingIntent.setSatellitesCount(satellitesCount);
		sendBroadcast();
	}

	private void sendBroadcast() {
		context.sendBroadcast(trackingIntent);
	}

	@Override
	public void close() {
		stop();
	}

	@VisibleForTesting
	public Integer getRestartTimeout() {
		return restartTimeout;
	}

	@VisibleForTesting
	public Integer getSleepTimeout() {
		return sleepTimeout;
	}
}