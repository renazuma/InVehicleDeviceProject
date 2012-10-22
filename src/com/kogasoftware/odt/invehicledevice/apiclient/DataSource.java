package com.kogasoftware.odt.invehicledevice.apiclient;

import java.io.Closeable;
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

public interface DataSource extends Closeable {
	void cancel(int reqkey);

	int arrivalOperationSchedule(OperationSchedule os,
			ApiClientCallback<OperationSchedule> callback);

	int departureOperationSchedule(OperationSchedule os,
			ApiClientCallback<OperationSchedule> callback);

	int getOffPassenger(OperationSchedule operationSchedule,
			Reservation reservation, User user,
			PassengerRecord passengerRecord,
			ApiClientCallback<Void> callback);

	int getOnPassenger(OperationSchedule operationSchedule,
			Reservation reservation, User user,
			PassengerRecord passengerRecord,
			ApiClientCallback<Void> callback);

	int getOperationSchedules(ApiClientCallback<List<OperationSchedule>> callback);

	int getVehicleNotifications(
			ApiClientCallback<List<VehicleNotification>> callback);

	int responseVehicleNotification(VehicleNotification vn, int response,
			ApiClientCallback<VehicleNotification> callback);

	int sendServiceUnitStatusLog(ServiceUnitStatusLog log,
			ApiClientCallback<ServiceUnitStatusLog> callback);

	int cancelGetOffPassenger(OperationSchedule operationSchedule,
			Reservation reservation, User user, ApiClientCallback<Void> callback);

	int cancelGetOnPassenger(OperationSchedule operationSchedule,
			Reservation reservation, User user, ApiClientCallback<Void> callback);

	int searchReservationCandidate(Demand demand,
			ApiClientCallback<List<ReservationCandidate>> callback);

	int createReservation(ReservationCandidate reservationCandidate,
			ApiClientCallback<Reservation> callback);

	int getMapTile(LatLng center, Integer zoom,
			ApiClientCallback<Bitmap> webAPICallback);

	int getServiceProvider(ApiClientCallback<ServiceProvider> callback);

	DataSource withSaveOnClose();

	DataSource withRetry(Boolean retry);
}
