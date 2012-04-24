package com.kogasoftware.odt.invehicledevice.logic;

import org.json.JSONException;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Reader;
import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.ServiceUnitStatusLog;

public class LocationSender extends LogicUser implements Runnable,
		LocationListener {

	@Override
	public void onLocationChanged(Location location) {
		if (getLogic().isPresent()) {
			getLogic().get().setLocation(location);
		}
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
	public void run() {
		if (!getLogic().isPresent()) {
			return;
		}
		final Logic logic = getLogic().get();

		final Optional<ServiceUnitStatusLog> log = logic.getStatusAccess()
				.read(new Reader<Optional<ServiceUnitStatusLog>>() {
					@Override
					public Optional<ServiceUnitStatusLog> read(Status status) {
						if (!status.latitude.isPresent()
								|| !status.longitude.isPresent()) {
							return Optional.absent();
						}
						ServiceUnitStatusLog log = new ServiceUnitStatusLog();
						log.setLatitude(status.latitude.get());
						log.setLongitude(status.longitude.get());
						log.setTemperature(status.temperature);
						log.setOrientation(status.orientation);
						return Optional.of(log);
					}
				});
		if (!log.isPresent()) {
			return;
		}

		try {
			logic.getDataSource().sendServiceUnitStatusLog(log.get(),
					new WebAPICallback<ServiceUnitStatusLog>() {
						@Override
						public void onException(int reqkey, WebAPIException ex) {
						}

						@Override
						public void onFailed(int reqkey, int statusCode,
								String response) {
						}

						@Override
						public void onSucceed(int reqkey, int statusCode,
								ServiceUnitStatusLog result) {
						}
					});
		} catch (WebAPIException e) {
		} catch (JSONException e) {
		}
	}
}
