package com.kogasoftware.odt.invehicledevice.apiclient;

import java.io.Closeable;
import java.util.List;

import android.graphics.Bitmap;

import com.javadocmd.simplelatlng.LatLng;
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

public interface DataSource extends Closeable {
	void cancel(int reqkey);

	int arrivalOperationSchedule(OperationSchedule os,
			WebAPICallback<OperationSchedule> callback);

	int departureOperationSchedule(OperationSchedule os,
			WebAPICallback<OperationSchedule> callback);

	int getOffPassenger(OperationSchedule operationSchedule,
			Reservation reservation, User user,
			PassengerRecord passengerRecord,
			WebAPICallback<Void> callback);

	int getOnPassenger(OperationSchedule operationSchedule,
			Reservation reservation, User user,
			PassengerRecord passengerRecord,
			WebAPICallback<Void> callback);

	int getOperationSchedules(WebAPICallback<List<OperationSchedule>> callback);

	int getVehicleNotifications(
			WebAPICallback<List<VehicleNotification>> callback);

	int responseVehicleNotification(VehicleNotification vn, int response,
			WebAPICallback<VehicleNotification> callback);

	int sendServiceUnitStatusLog(ServiceUnitStatusLog log,
			WebAPICallback<ServiceUnitStatusLog> callback);

	int cancelGetOffPassenger(OperationSchedule operationSchedule,
			Reservation reservation, User user, WebAPICallback<Void> callback);

	int cancelGetOnPassenger(OperationSchedule operationSchedule,
			Reservation reservation, User user, WebAPICallback<Void> callback);

	int searchReservationCandidate(Demand demand,
			WebAPICallback<List<ReservationCandidate>> callback);

	int createReservation(ReservationCandidate reservationCandidate,
			WebAPICallback<Reservation> callback);

	int getMapTile(LatLng center, Integer zoom,
			WebAPICallback<Bitmap> webAPICallback);

	int getServiceProvider(WebAPICallback<ServiceProvider> callback);

	DataSource withSaveOnClose();

	DataSource withRetry(Boolean retry);
}
