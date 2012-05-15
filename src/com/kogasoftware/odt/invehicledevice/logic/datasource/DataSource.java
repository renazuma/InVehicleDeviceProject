package com.kogasoftware.odt.invehicledevice.logic.datasource;

import java.io.Closeable;
import java.util.Date;
import java.util.List;

import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.InVehicleDevice;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.ReservationCandidate;
import com.kogasoftware.odt.webapi.model.ServiceUnitStatusLog;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public interface DataSource extends Closeable {
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

	Reservation postReservation(Integer reservationCandidateId)
			throws WebAPIException;

	List<ReservationCandidate> postReservationCandidates(Integer userId,
			Integer departurePlatformId, Integer arrivalPlatformId)
			throws WebAPIException;

	void putReservationTransferredAt(Integer id, Date transferredAt)
			throws WebAPIException;

	void putVehicleNotificationReadAt(Integer id, Date readAt)
			throws WebAPIException;

	public int responseVehicleNotification(VehicleNotification vn,
			int response, WebAPICallback<VehicleNotification> callback);

	int sendServiceUnitStatusLog(ServiceUnitStatusLog log,
			WebAPICallback<ServiceUnitStatusLog> callback);
}
