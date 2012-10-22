package com.kogasoftware.odt.invehicledevice.apiclient;

import java.io.IOException;
import java.util.List;

import android.graphics.Bitmap;

import com.javadocmd.simplelatlng.LatLng;
import com.kogasoftware.odt.apiclient.ApiClientCallback;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Demand;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Reservation;
import com.kogasoftware.odt.invehicledevice.apiclient.model.ReservationCandidate;
import com.kogasoftware.odt.invehicledevice.apiclient.model.ServiceProvider;
import com.kogasoftware.odt.invehicledevice.apiclient.model.ServiceUnitStatusLog;
import com.kogasoftware.odt.invehicledevice.apiclient.model.User;
import com.kogasoftware.odt.invehicledevice.apiclient.model.VehicleNotification;

public class EmptyDataSource implements DataSource {
	@Override
	public void close() throws IOException {
	}

	@Override
	public int arrivalOperationSchedule(OperationSchedule os,
			ApiClientCallback<OperationSchedule> callback) {
		return 0;
	}

	@Override
	public int departureOperationSchedule(OperationSchedule os,
			ApiClientCallback<OperationSchedule> callback) {
		return 0;
	}

	@Override
	public int responseVehicleNotification(VehicleNotification vn,
			int response, ApiClientCallback<VehicleNotification> callback) {
		return 0;
	}

	@Override
	public int sendServiceUnitStatusLog(ServiceUnitStatusLog log,
			ApiClientCallback<ServiceUnitStatusLog> callback) {
		return 0;
	}

	@Override
	public int searchReservationCandidate(Demand demand,
			ApiClientCallback<List<ReservationCandidate>> callback) {
		return 0;
	}

	@Override
	public int createReservation(ReservationCandidate reservationCandidate,
			ApiClientCallback<Reservation> callback) {
		return 0;
	}

	@Override
	public void cancel(int reqkey) {
	}

	@Override
	public int getMapTile(LatLng center, Integer zoom,
			ApiClientCallback<Bitmap> callback) {
		return 0;
	}

	@Override
	public int getServiceProvider(ApiClientCallback<ServiceProvider> callback) {
		return 0;
	}

	@Override
	public int cancelGetOffPassenger(OperationSchedule operationSchedule,
			Reservation reservation, User user, ApiClientCallback<Void> callback) {
		return 0;
	}

	@Override
	public int cancelGetOnPassenger(OperationSchedule operationSchedule,
			Reservation reservation, User user, ApiClientCallback<Void> callback) {
		return 0;
	}

	@Override
	public DataSource withSaveOnClose() {
		return this;
	}

	@Override
	public int getOperationSchedules(
			ApiClientCallback<List<OperationSchedule>> callback) {
		return 0;
	}

	@Override
	public int getVehicleNotifications(
			ApiClientCallback<List<VehicleNotification>> callback) {
		return 0;
	}

	@Override
	public DataSource withRetry(Boolean retry) {
		return this;
	}

	@Override
	public int getOffPassenger(OperationSchedule operationSchedule,
			Reservation reservation, User user,
			PassengerRecord passengerRecord, ApiClientCallback<Void> callback) {
		return 0;
	}

	@Override
	public int getOnPassenger(OperationSchedule operationSchedule,
			Reservation reservation, User user,
			PassengerRecord passengerRecord, ApiClientCallback<Void> callback) {
		return 0;
	}
}
