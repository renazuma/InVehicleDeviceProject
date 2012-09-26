package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice;

import java.math.BigDecimal;

import android.location.GpsStatus;
import android.location.Location;

import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource.Writer;

public class ServiceUnitStatusLogLogic {
	protected final InVehicleDeviceService service;
	public static final Long ORIENTATION_SAVE_PERIOD_MILLIS = 5000L;
	protected Long lastOrientationSavedMillis = System.currentTimeMillis();

	public ServiceUnitStatusLogLogic(InVehicleDeviceService service) {
		this.service = service;
	}

	public void changeLocation(final Location location, Optional<GpsStatus> gpsStatus) {
		service.getLocalDataSource().withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				localData.serviceUnitStatusLog.setLatitude(new BigDecimal(
						location.getLatitude()));
				localData.serviceUnitStatusLog.setLongitude(new BigDecimal(
						location.getLongitude()));
			}
		});
	}

	public void changeOrientation(final Double orientationDegree) {
		long now = System.currentTimeMillis();
		if (lastOrientationSavedMillis + ORIENTATION_SAVE_PERIOD_MILLIS > now) {
			return;
		}
		lastOrientationSavedMillis = now;

		service.getLocalDataSource().withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				localData.serviceUnitStatusLog.setOrientation(orientationDegree
						.intValue());
			}
		});
	}

	public void changeTemperature(final Double celciusTemperature) {
		service.getLocalDataSource().withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				localData.serviceUnitStatusLog
						.setTemperature(celciusTemperature.intValue());
			}
		});
	}
}
