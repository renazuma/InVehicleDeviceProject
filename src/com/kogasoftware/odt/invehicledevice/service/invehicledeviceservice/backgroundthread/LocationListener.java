package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.backgroundthread;

import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;

/**
 * 位置情報を取得する
 * 
 * @see http://kamoland.com/wiki/wiki.cgi?Desire%A4%CEGPS%BC%E8%C6%C0%A4%C7%A4%CE%BB%EE%B9%D4%BA%F8%B8%ED
 */
public class LocationListener implements android.location.LocationListener,
		InVehicleDeviceService.OnPauseActivityListener,
		InVehicleDeviceService.OnResumeActivityListener {
	private final InVehicleDeviceService service;
	private final LocationManager locationManager;
	private final PowerManager powerManager;
	private final WakeLock wakeLock;
	private final AtomicBoolean started = new AtomicBoolean(false);
	private final Handler handler = new Handler();
	private final long handlerThreadId = handler.getLooper().getThread()
			.getId();

	public LocationListener(InVehicleDeviceService service) {
		this.service = service;
		locationManager = (LocationManager) service
				.getSystemService(Context.LOCATION_SERVICE);
		powerManager = (PowerManager) service
				.getSystemService(Context.POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
				getClass().getName());
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

		// GPS使用開始時．PARTIAL_WAKE_LOCKを取得する
		wakeLock.acquire();

		// 試しにgetLastKnownLocationしてみる
		locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		// 位置検索を始動する
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

		// GPS使用終了時．PARTIAL_WAKE_LOCKを解放する
		locationManager.removeUpdates(this);
		wakeLock.release();
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
