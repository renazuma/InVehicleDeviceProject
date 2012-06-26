package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.backgroundthread;

import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;

public class LocationListener implements android.location.LocationListener,
		InVehicleDeviceService.OnPauseActivityListener,
		InVehicleDeviceService.OnResumeActivityListener {
	private final InVehicleDeviceService service;
	private final LocationManager locationManager;
	private final AtomicBoolean started = new AtomicBoolean(false);
	private final Handler handler = new Handler();
	private final long handlerThreadId = handler.getLooper().getThread()
			.getId();

	public LocationListener(InVehicleDeviceService service) {
		this.service = service;
		locationManager = (LocationManager) service
				.getSystemService(Context.LOCATION_SERVICE);
		service.addOnPauseActivityListener(this);
		service.addOnResumeActivityListener(this);
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
		locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				1000, 1, this);
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

		if (!started.getAndSet(false)) {
			return;
		}
		locationManager.removeUpdates(this);
	}

	@Override
	public void onLocationChanged(Location location) {
		service.changeLocation(location);
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
	public void onResumeActivity() {
		start();
	}

	@Override
	public void onPauseActivity() {
		stop();
	}
}
