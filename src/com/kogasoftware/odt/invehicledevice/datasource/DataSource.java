package com.kogasoftware.odt.invehicledevice.datasource;

import java.io.Closeable;
import java.util.List;

import android.graphics.Bitmap;

import com.javadocmd.simplelatlng.LatLng;
import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.Demand;
import com.kogasoftware.odt.webapi.model.InVehicleDevice;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.ReservationCandidate;
import com.kogasoftware.odt.webapi.model.ServiceUnitStatusLog;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public interface DataSource extends Closeable {
	void cancel(int reqkey);

	int arrivalOperationSchedule(OperationSchedule os,
			WebAPICallback<OperationSchedule> callback);

	int departureOperationSchedule(OperationSchedule os,
			WebAPICallback<OperationSchedule> callback);

	InVehicleDevice getInVehicleDevice() throws WebAPIException;

	int getOffPassenger(OperationSchedule operationSchedule,
			Reservation reservation, PassengerRecord passengerRecord,
			WebAPICallback<PassengerRecord> callback);

	int getOnPassenger(OperationSchedule operationSchedule,
			Reservation reservation, PassengerRecord passengerRecord,
			WebAPICallback<PassengerRecord> callback);

	List<OperationSchedule> getOperationSchedules() throws WebAPIException;

	List<VehicleNotification> getVehicleNotifications() throws WebAPIException;

	int responseVehicleNotification(VehicleNotification vn, int response,
			WebAPICallback<VehicleNotification> callback);

	int sendServiceUnitStatusLog(ServiceUnitStatusLog log,
			WebAPICallback<ServiceUnitStatusLog> callback);

	int cancelGetOffPassenger(OperationSchedule operationSchedule,
			Reservation reservation, WebAPICallback<PassengerRecord> callback);

	int cancelGetOnPassenger(OperationSchedule operationSchedule,
			Reservation reservation, WebAPICallback<PassengerRecord> callback);

	int searchReservationCandidate(Demand demand,
			WebAPICallback<List<ReservationCandidate>> callback);

	int createReservation(ReservationCandidate reservationCandidate,
			WebAPICallback<Reservation> callback);

	int getMapTile(LatLng center, Integer zoom,
			WebAPICallback<Bitmap> webAPICallback);

	void saveOnClose(int reqkey);
}
