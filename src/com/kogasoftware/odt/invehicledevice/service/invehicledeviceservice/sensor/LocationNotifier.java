package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.sensor;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import android.content.SharedPreferences;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.SharedPreferencesKeys;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.logic.ServiceUnitStatusLogLogic;

/**
 * 位置情報を取得する
 *
 * 1.ACCURACY_THRESHOLDを満たす位置を受信したら、その時間を記録し、その位置をサービスに通知
 * 2.ACCURACY_THRESHOLDを満たす位置をRESTART_TIMEOUT内に受信できない場合再起動
 *
 * @see http
 *      ://kamoland.com/wiki/wiki.cgi?Desire%A4%CEGPS%BC%E8%C6%C0%A4%C7%A4%CE
 *      %BB%EE%B9%D4%BA%F8%B8%ED
 */
public class LocationNotifier implements LocationListener, GpsStatus.Listener,
		GpsStatus.NmeaListener {
	private static final String TAG = LocationNotifier.class.getSimpleName();
	public static final Float ACCURACY_THRESHOLD = 50f;
	protected static final Integer DEFAULT_RESTART_CHECK_INTERVAL = 20 * 1000;
	public final Integer restartCheckInterval;
	protected static final Integer DEFAULT_MIN_TIME = 1000;
	protected static final Integer DEFAULT_MIN_DISTANCE = 1;
	protected static final Integer DEFAULT_RESTART_TIMEOUT = 90 * 1000;
	protected final Integer minTime;
	protected final Integer minDistance;
	protected final Integer restartTimeout;
	protected final Handler handler = new Handler();
	protected final long handlerThreadId = handler.getLooper().getThread()
			.getId();
	protected final LocationManager locationManager;
	protected final PowerManager powerManager;
	protected final WakeLock wakeLock;
	protected final AtomicBoolean started = new AtomicBoolean(false);
	protected final AtomicBoolean locationUpdatesStarted = new AtomicBoolean(
			false);
	protected final ServiceUnitStatusLogLogic serviceUnitStatusLogLogic;
	protected final Runnable restartTimeouter = new Runnable() {
		@Override
		public void run() {
			if (!started.get()) {
				Log.d(TAG, "GPS restart timouter removed");
				handler.removeCallbacks(this);
				return;
			}

			Log.d(TAG, "GPS restart check. numSatellites=" + numSatellites
					+ " nextRestartBaseTime=\"" + new Date(nextRestartBaseTime)
					+ "\" now=\"" + new Date() + "\"");
			final Date now = new Date();
			if ((nextRestartBaseTime + restartTimeout) > now.getTime()) {
				Log.d(TAG, "GPS restart unnecessary");
				handler.postDelayed(this, restartCheckInterval);
				return;
			}

			Log.d(TAG, "GPS restart");

			stopLocationUpdates();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					startLocationUpdates();
					Log.d(TAG, "GPS restart complete");
					handler.postDelayed(restartTimeouter, restartCheckInterval);
				}
			}, restartCheckInterval);
		}
	};

	protected Long lastAccurateLocationTime = 0l;
	protected Long nextRestartBaseTime = 0l;
	protected Optional<GpsStatus> gpsStatus = Optional.absent();
	protected Optional<Location> lastLocation = Optional.absent();
	protected Integer numSatellites = 0;
	protected Integer numUsedInFixSatellites = 0;

	public LocationNotifier(
			ServiceUnitStatusLogLogic serviceUnitStatusLogLogic,
			LocationManager locationManager, PowerManager powerManager,
			SharedPreferences preferences, Integer restartCheckInterval) {
		this.serviceUnitStatusLogLogic = serviceUnitStatusLogLogic;
		this.restartCheckInterval = restartCheckInterval;
		this.locationManager = locationManager;
		this.powerManager = powerManager;
		wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
				getClass().getName());

		minTime = preferences.getInt(
				SharedPreferencesKeys.LOCATION_RECEIVE_MIN_TIME,
				DEFAULT_MIN_TIME);
		minDistance = preferences.getInt(
				SharedPreferencesKeys.LOCATION_RECEIVE_MIN_DISTANCE,
				DEFAULT_MIN_DISTANCE);
		restartTimeout = preferences.getInt(
				SharedPreferencesKeys.LOCATION_RECEIVE_RESTART_TIMEOUT,
				DEFAULT_RESTART_TIMEOUT);

		Log.i(TAG, "minTime=" + minTime + " minDistance=" + minDistance
				+ " restartTimeout=" + restartTimeout);
	}

	public LocationNotifier(
			ServiceUnitStatusLogLogic serviceUnitStatusLogLogic,
			LocationManager locationManager, PowerManager powerManager,
			SharedPreferences preferences) {
		this(serviceUnitStatusLogLogic, locationManager, powerManager,
				preferences, DEFAULT_RESTART_CHECK_INTERVAL);
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
		gpsStatus = Optional
				.of(locationManager.getGpsStatus(gpsStatus.orNull()));

		if (gpsStatus.isPresent()) {
			Integer newNumSatellites = 0;
			Integer newNumUsedInFixSatellites = 0;
			for (GpsSatellite gpsSatellite : gpsStatus.get().getSatellites()) {
				newNumSatellites++;
				if (gpsSatellite.usedInFix()) {
					newNumUsedInFixSatellites++;
				}
			}

			if (!numSatellites.equals(newNumSatellites)
					|| !numUsedInFixSatellites
							.equals(newNumUsedInFixSatellites)) {
				Log.d(TAG, "number of satellites " + numUsedInFixSatellites
						+ "/" + numSatellites + " => "
						+ newNumUsedInFixSatellites + "/" + newNumSatellites);
				numSatellites = newNumSatellites;
				numUsedInFixSatellites = newNumUsedInFixSatellites;
				if (lastLocation.isPresent()) {
					serviceUnitStatusLogLogic.changeLocation(
							lastLocation.get(), gpsStatus);
				}
			}
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		String message = "onLocationChanged() provider="
				+ location.getProvider() + " hasAccuracy="
				+ location.hasAccuracy() + " accuracy="
				+ location.getAccuracy();
		if (location.getProvider().equals(LocationManager.GPS_PROVIDER)
				|| (location.hasAccuracy() && location.getAccuracy() < ACCURACY_THRESHOLD)) {
			// 精度が高いデータを受信した場合
			lastAccurateLocationTime = location.getTime();
			nextRestartBaseTime = lastAccurateLocationTime;
			Log.i(TAG, message + " / accurate location");
			lastLocation = Optional.of(location);
			serviceUnitStatusLogLogic.changeLocation(location, gpsStatus);
		} else {
			// 精度が低いデータを受信した場合
			Log.i(TAG, message + " / coarse location. not updated.");
		}
	}

	@Override
	public void onNmeaReceived(long timestamp, String nmea) {
		// Log.d(TAG,
		// "onNmeaReceived(" + timestamp + ", \""
		// + nmea.replaceAll("\\p{Cntrl}", " ") + "\")");
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

	public void start() {
		if (handlerThreadId != Thread.currentThread().getId()) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					start();
				}
			});
		}
		if (started.getAndSet(true)) {
			return;
		}
		startLocationUpdates();
	}

	protected void startLocationUpdates() {
		if (locationUpdatesStarted.getAndSet(true)) {
			return;
		}
		Log.d(TAG, "startLocationUpdates()");
		nextRestartBaseTime = (new Date()).getTime();
		wakeLock.acquire();

		// if
		// (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
		// {
		// locationManager.requestLocationUpdates(
		// LocationManager.NETWORK_PROVIDER, DEFAULT_MIN_TIME,
		// DEFAULT_MIN_DISTANCE, this);
		// }
		// if
		// (locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER))
		// {
		// locationManager.requestLocationUpdates(
		// LocationManager.PASSIVE_PROVIDER, DEFAULT_MIN_TIME,
		// DEFAULT_MIN_DISTANCE, this);
		// }
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			lastLocation = Optional.fromNullable(locationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER));
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, minTime, minDistance, this);
			locationManager.addGpsStatusListener(this);
			locationManager.addNmeaListener(this);
		}
		handler.post(restartTimeouter);
	}

	public void stop() {
		if (handlerThreadId != Thread.currentThread().getId()) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					stop();
				}
			});
		}
		removeAllCallbacks();
		if (!started.get()) {
			return;
		}
		stopLocationUpdates();
	}

	protected void stopLocationUpdates() {
		if (!locationUpdatesStarted.getAndSet(false)) {
			return;
		}
		Log.d(TAG, "stopLocationUpdates()");
		wakeLock.release();
		removeAllCallbacks();
	}

	protected void removeAllCallbacks() {
		locationManager.removeGpsStatusListener(this);
		locationManager.removeNmeaListener(this);
		locationManager.removeUpdates(this);
		handler.removeCallbacks(restartTimeouter);
	}
}
