package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.logic;

import java.math.BigDecimal;

import android.location.GpsStatus;
import android.location.Location;

import com.google.common.base.Optional;
import com.kogasoftware.odt.apiclient.EmptyApiClientCallback;
import com.kogasoftware.odt.invehicledevice.apiclient.model.ServiceUnitStatusLog;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage.Reader;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage.Writer;

public class ServiceUnitStatusLogLogic {
	protected final InVehicleDeviceService service;
	public static final Long ORIENTATION_SAVE_PERIOD_MILLIS = 500L;
	protected Long lastOrientationSavedMillis = System.currentTimeMillis();

	public ServiceUnitStatusLogLogic(InVehicleDeviceService service) {
		this.service = service;
	}

	public void changeSignalStrength(final Integer signalStrengthPercentage) {
		service.getEventDispatcher().dispatchChangeSignalStrength(
				signalStrengthPercentage);
	}

	public void changeLocation(final Location location,
			Optional<GpsStatus> gpsStatus) {
		service.getLocalStorage().withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				localData.serviceUnitStatusLog.setLatitude(new BigDecimal(
						location.getLatitude()));
				localData.serviceUnitStatusLog.setLongitude(new BigDecimal(
						location.getLongitude()));
			}
		});
		service.getEventDispatcher()
				.dispatchChangeLocation(location, gpsStatus);
	}

	public void changeOrientation(final Double orientationDegree) {
		long now = System.currentTimeMillis();
		if (lastOrientationSavedMillis + ORIENTATION_SAVE_PERIOD_MILLIS > now) {
			return;
		}
		lastOrientationSavedMillis = now;

		service.getLocalStorage().withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				localData.serviceUnitStatusLog.setOrientation(orientationDegree
						.intValue());
			}
		});
		service.getEventDispatcher().dispatchChangeOrientation(
				orientationDegree);
	}

	public void changeTemperature(final Double celciusTemperature) {
		service.getLocalStorage().withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				localData.serviceUnitStatusLog
						.setTemperature(celciusTemperature.intValue());
			}
		});
		service.getEventDispatcher().dispatchChangeTemperature(
				celciusTemperature);
	}

	public ServiceUnitStatusLog getWithReadLock() {
		return service.getLocalStorage().withReadLock(
				new Reader<ServiceUnitStatusLog>() {
					@Override
					public ServiceUnitStatusLog read(LocalData status) {
						return status.serviceUnitStatusLog;
					}
				});
	}

	public void sendWithReadLock() {
		service.getApiClient()
				.withSaveOnClose()
				.sendServiceUnitStatusLog(getWithReadLock(),
						new EmptyApiClientCallback<ServiceUnitStatusLog>());
	}
}