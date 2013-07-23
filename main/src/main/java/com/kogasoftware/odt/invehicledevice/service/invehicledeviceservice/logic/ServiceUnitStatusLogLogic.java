package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.logic;

import java.math.BigDecimal;

import android.location.Location;

import com.google.common.base.Optional;
import com.kogasoftware.odt.apiclient.EmptyApiClientCallback;
import com.kogasoftware.odt.invehicledevice.apiclient.model.ServiceUnitStatusLog;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage.BackgroundWriter;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage.Reader;

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

	public void changeLocation(final Optional<Location> location,
			final Optional<Integer> satellitesCount) {
		service.getLocalStorage().write(new BackgroundWriter() {
			@Override
			public void writeInBackground(LocalData localData) {
				if (location.isPresent()) {
					localData.serviceUnitStatusLog.setLatitude(new BigDecimal(
							location.get().getLatitude()));
					localData.serviceUnitStatusLog.setLongitude(new BigDecimal(
							location.get().getLongitude()));
				}
			}

			@Override
			public void onWrite() {
				service.getEventDispatcher().dispatchChangeLocation(location,
						satellitesCount);
			}
		});
	}

	public void changeOrientation(final Double orientationDegree) {
		long now = System.currentTimeMillis();
		if (lastOrientationSavedMillis + ORIENTATION_SAVE_PERIOD_MILLIS > now) {
			return;
		}
		lastOrientationSavedMillis = now;

		service.getLocalStorage().write(new BackgroundWriter() {
			@Override
			public void writeInBackground(LocalData localData) {
				localData.serviceUnitStatusLog.setOrientation(orientationDegree
						.intValue());
			}

			public void onWrite() {
				service.getEventDispatcher().dispatchChangeOrientation(
						orientationDegree);
			}
		});
	}

	public void changeTemperature(final Double celciusTemperature) {
		service.getLocalStorage().write(new BackgroundWriter() {
			@Override
			public void writeInBackground(LocalData localData) {
				localData.serviceUnitStatusLog
						.setTemperature(celciusTemperature.intValue());
			}

			@Override
			public void onWrite() {
				service.getEventDispatcher().dispatchChangeTemperature(
						celciusTemperature);
			}
		});
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
