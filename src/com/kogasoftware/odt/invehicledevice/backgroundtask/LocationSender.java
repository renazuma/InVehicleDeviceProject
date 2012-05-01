package com.kogasoftware.odt.invehicledevice.backgroundtask;

import org.json.JSONException;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.Status;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Reader;
import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.ServiceUnitStatusLog;

public class LocationSender implements Runnable, LocationListener {

	private final CommonLogic commonLogic;

	public LocationSender(CommonLogic commonLogic) {
		this.commonLogic = commonLogic;
	}

	@Override
	public void onLocationChanged(Location location) {
		commonLogic.setLocation(location);
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

	/**
	 * 現在のServiceUnitStatusLogをサーバーへ送信。
	 * ただしserviceUnitStatusLogLocationEnabledがfalseの場合は送信しない
	 */
	@Override
	public void run() {
		final Optional<ServiceUnitStatusLog> log = commonLogic
				.getStatusAccess().read(
						new Reader<Optional<ServiceUnitStatusLog>>() {
							@Override
							public Optional<ServiceUnitStatusLog> read(
									Status status) {
								if (!status.serviceUnitStatusLogLocationEnabled) {
									return Optional.absent();
								}
								return Optional.of(status.serviceUnitStatusLog);
							}
						});
		if (!log.isPresent()) {
			return;
		}

		try {
			commonLogic.getDataSource().sendServiceUnitStatusLog(log.get(),
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
