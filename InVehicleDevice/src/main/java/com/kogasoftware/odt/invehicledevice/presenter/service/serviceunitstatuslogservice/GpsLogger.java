package com.kogasoftware.odt.invehicledevice.presenter.service.serviceunitstatuslogservice;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.location.Location;
import android.net.TrafficStats;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.common.annotations.VisibleForTesting;
import com.kogasoftware.odt.invehicledevice.model.contentprovider.table.ServiceUnitStatusLog;

import org.joda.time.DateTimeUtils;

import java.io.Closeable;
import java.math.BigDecimal;

/**
 * GPSの状況をログ
 */
public class GpsLogger extends LocationCallback
		implements
			Runnable,
			Closeable {
	private static final Integer DEFAULT_MIN_TIME = 5000;
	private static final Integer DEFAULT_RESTART_TIMEOUT = 90 * 1000;
	private static final Integer DEFAULT_SLEEP_TIMEOUT = 20 * 1000;
	public static final Integer BROADCAST_PERIOD_MILLIS = 5000;
	private static final String TAG = GpsLogger.class.getSimpleName();

	private final Integer restartTimeout = DEFAULT_RESTART_TIMEOUT;
	private final Integer sleepTimeout = DEFAULT_SLEEP_TIMEOUT;
	private final ContentResolver contentResolver;
	private final FusedLocationProviderClient fusedLocationProviderClient;

	private Boolean started = false;
	private Long startedTimeMillis = DateTimeUtils.currentTimeMillis();
	private Long stoppedTimeMillis = DateTimeUtils.currentTimeMillis();
	private Long lastLocationReceivedTimeMillis = 0L;

	public GpsLogger(Context context) {
		this.contentResolver = context.getContentResolver();
		this.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
		start();
	}

	private void start() {
		if (started) return;
		try {
			LocationRequest request = new LocationRequest();
			request.setInterval(DEFAULT_MIN_TIME);
			request.setFastestInterval(DEFAULT_MIN_TIME);
			request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
			fusedLocationProviderClient.requestLocationUpdates(request,this, null);
			startedTimeMillis = DateTimeUtils.currentTimeMillis();
			started = true;
		} catch (SecurityException e) {
			Log.w(TAG, "ACCESS_FINE_LOCATION is not granted.");
		}
	}

	private void stop() {
		if (!started) return;
		Log.d(TAG, "stop()");
		fusedLocationProviderClient.removeLocationUpdates(this);
		stoppedTimeMillis = DateTimeUtils.currentTimeMillis();
		started = false;
	}

	@Override
	public void onLocationResult(LocationResult locationResult) {
		Log.d(TAG, "onLocationResult");
	    super.onLocationResult(locationResult);
	    lastLocationReceivedTimeMillis = DateTimeUtils.currentTimeMillis();
	    update(locationResult.getLastLocation());
	}

	@Override
	public void run() {
		Long currentTimeMillis = DateTimeUtils.currentTimeMillis();
		if (to_stop_status(currentTimeMillis)) stop();
		if (to_start_status(currentTimeMillis)) start();
	}

	private boolean to_stop_status(Long currentTimeMillis) {
		return (started && currentTimeMillis - lastLocationReceivedTimeMillis >= restartTimeout
						&& currentTimeMillis - startedTimeMillis >= restartTimeout);
	}

	private boolean to_start_status(Long currentTimeMillis) {
		return !started && currentTimeMillis - stoppedTimeMillis >= sleepTimeout;
	}

	private void update(Location location) {
		final ContentValues values = new ContentValues();
		String latitude = new BigDecimal(location.getLatitude()).toPlainString();
		String longitude = new BigDecimal(location.getLongitude()).toPlainString();
		values.put(ServiceUnitStatusLog.Columns.LATITUDE, latitude);
		values.put(ServiceUnitStatusLog.Columns.LONGITUDE, longitude);
		new Thread() {
			@Override
			public void run() {
				//TODO: 大量エラーが発生するための処置。何のためにやっているか不明なので調べること
			    TrafficStats.setThreadStatsTag(1000);
				contentResolver.update(ServiceUnitStatusLog.CONTENT.URI, values, null, null);
			}
		}.start();
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
