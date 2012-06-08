package com.kogasoftware.odt.invehicledevice.logic.datasource;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

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

public class EmptyDataSource implements DataSource {

	@Override
	public void close() throws IOException {
	}

	@Override
	public int arrivalOperationSchedule(OperationSchedule os,
			WebAPICallback<OperationSchedule> callback) {
		return 0;
	}

	@Override
	public int departureOperationSchedule(OperationSchedule os,
			WebAPICallback<OperationSchedule> callback) {
		return 0;
	}

	@Override
	public InVehicleDevice getInVehicleDevice() throws WebAPIException {
		return new InVehicleDevice();
	}

	@Override
	public int getOffPassenger(OperationSchedule operationSchedule,
			Reservation reservation, PassengerRecord passengerRecord,
			WebAPICallback<PassengerRecord> callback) {
		return 0;
	}

	@Override
	public int getOnPassenger(OperationSchedule operationSchedule,
			Reservation reservation, PassengerRecord passengerRecord,
			WebAPICallback<PassengerRecord> callback) {
		return 0;
	}

	@Override
	public List<OperationSchedule> getOperationSchedules()
			throws WebAPIException {
		return new LinkedList<OperationSchedule>();
	}

	@Override
	public List<VehicleNotification> getVehicleNotifications()
			throws WebAPIException {
		return new LinkedList<VehicleNotification>();
	}

	@Override
	public int responseVehicleNotification(VehicleNotification vn,
			int response, WebAPICallback<VehicleNotification> callback) {
		return 0;
	}

	@Override
	public int sendServiceUnitStatusLog(ServiceUnitStatusLog log,
			WebAPICallback<ServiceUnitStatusLog> callback) {
		return 0;
	}

	@Override
	public int cancelGetOffPassenger(OperationSchedule operationSchedule,
			Reservation reservation, WebAPICallback<PassengerRecord> callback) {
		return 0;
	}

	@Override
	public int cancelGetOnPassenger(OperationSchedule operationSchedule,
			Reservation reservation, WebAPICallback<PassengerRecord> callback) {
		return 0;
	}

	@Override
	public int searchReservationCandidate(Demand demand,
			WebAPICallback<List<ReservationCandidate>> callback) {
		return 0;
	}

	@Override
	public int createReservation(ReservationCandidate reservationCandidate,
			WebAPICallback<Reservation> callback) {
		return 0;
	}

}
