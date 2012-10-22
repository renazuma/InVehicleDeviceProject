package com.kogasoftware.odt.invehicledevice.apiclient;

import java.io.File;
import java.util.List;

import android.graphics.Bitmap;

import com.google.common.io.Closeables;
import com.javadocmd.simplelatlng.LatLng;
import com.kogasoftware.odt.webapi.WebAPI;
import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;
import com.kogasoftware.odt.webapi.model.Demand;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.ReservationCandidate;
import com.kogasoftware.odt.webapi.model.ServiceProvider;
import com.kogasoftware.odt.webapi.model.ServiceUnitStatusLog;
import com.kogasoftware.odt.webapi.model.User;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class WebAPIDataSource implements DataSource {
	public static final String DEFAULT_URL = "http://127.0.0.1";
	private final WebAPI api;

	public WebAPIDataSource() {
		api = new WebAPI(DEFAULT_URL);
	}

	public WebAPIDataSource(String url, String token, File file) {
		api = new WebAPI(url, token, file);
	}

	@Override
	public int arrivalOperationSchedule(final OperationSchedule os,
			final WebAPICallback<OperationSchedule> callback) {
		return api.arrivalOperationSchedule(os, callback);
	}

	@Override
	public int departureOperationSchedule(final OperationSchedule os,
			final WebAPICallback<OperationSchedule> callback) {
		return api.departureOperationSchedule(os, callback);
	}

	@Override
	public int responseVehicleNotification(final VehicleNotification vn,
			final int response,
			final WebAPICallback<VehicleNotification> callback) {
		return api.responseVehicleNotification(vn, response, callback);
	}

	@Override
	public int sendServiceUnitStatusLog(
			final ServiceUnitStatusLog serviceUnitStatusLog,
			final WebAPICallback<ServiceUnitStatusLog> callback) {
		return api.sendServiceUnitStatusLog(serviceUnitStatusLog, callback);
	}

	@Override
	public void close() {
		Closeables.closeQuietly(api);
	}

	@Override
	public int cancelGetOnPassenger(final OperationSchedule operationSchedule,
			final Reservation reservation, final User user,
			final WebAPICallback<Void> callback) {
		return api.cancelGetOnPassenger(operationSchedule, reservation, user,
				callback);
	}

	@Override
	public int cancelGetOffPassenger(final OperationSchedule operationSchedule,
			final Reservation reservation, final User user,
			final WebAPICallback<Void> callback) {
		return api.cancelGetOffPassenger(operationSchedule, reservation, user,
				callback);
	}

	@Override
	public int searchReservationCandidate(final Demand demand,
			final WebAPICallback<List<ReservationCandidate>> callback) {
		return api.searchReservationCandidate(demand, callback);
	}

	@Override
	public int createReservation(
			final ReservationCandidate reservationCandidate,
			final WebAPICallback<Reservation> callback) {
		return api.createReservation(reservationCandidate, callback);
	}

	@Override
	public void cancel(int reqkey) {
		api.abort(reqkey);
	}

	@Override
	public int getMapTile(LatLng center, Integer zoom,
			WebAPICallback<Bitmap> callback) {
		return api.getMapTile(center, zoom, callback);
	}

	@Override
	public DataSource withSaveOnClose() {
		api.withSaveOnClose();
		return this;
	}

	@Override
	public int getServiceProvider(final WebAPICallback<ServiceProvider> callback) {
		return api.getServicePrivider(callback);
	}

	@Override
	public int getOffPassenger(OperationSchedule operationSchedule,
			Reservation reservation, User user,
			PassengerRecord passengerRecord,
			WebAPICallback<Void> callback) {
		return api.getOffPassenger(operationSchedule, reservation, user, passengerRecord, callback);
	}

	@Override
	public int getOnPassenger(OperationSchedule operationSchedule,
			Reservation reservation, User user,
			PassengerRecord passengerRecord,
			WebAPICallback<Void> callback) {
		return api.getOnPassenger(operationSchedule, reservation, user,
				passengerRecord, callback);
	}

	@Override
	public int getOperationSchedules(
			WebAPICallback<List<OperationSchedule>> callback) {
		return api.getOperationSchedules(callback);
	}

	@Override
	public int getVehicleNotifications(
			WebAPICallback<List<VehicleNotification>> callback) {
		return api.getVehicleNotifications(callback);
	}

	@Override
	public DataSource withRetry(Boolean retry) {
		api.withRetry(retry);
		return this;
	}
}
