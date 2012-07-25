package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.kogasoftware.odt.invehicledevice.datasource.DataSource;
import com.kogasoftware.odt.invehicledevice.empty.EmptyWebAPICallback;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.Reservation;

public class PassengerRecordLogic {
	public final InVehicleDeviceService service;
	
	public PassengerRecordLogic(InVehicleDeviceService service) {
		this.service = service;
	}
	
	public Boolean canGetOff(PassengerRecord passengerRecord) {
		if (isSelected(passengerRecord)) {
			if (passengerRecord.isGotOff()) {
				return true;
			}
		} else {
			if (passengerRecord.isRiding()) {
				return true;
			}
		}
		return false;
	}

	public Boolean canGetOn(PassengerRecord passengerRecord) {
		if (isSelected(passengerRecord)) {
			if (passengerRecord.isRiding()) {
				return true;
			}
		} else {
			if (passengerRecord.isUnhandled()) {
				return true;
			}
		}
		return false;
	}

	public List<PassengerRecord> getNoGettingOffPassengerRecords() {
		List<PassengerRecord> noGettingOffReservations = new LinkedList<PassengerRecord>();
		for (PassengerRecord passengerRecord : service.getPassengerRecords()) {
			if (isGetOffScheduled(passengerRecord)
					&& !isSelected(passengerRecord)) {
				noGettingOffReservations.add(passengerRecord);
			}
		}
		return noGettingOffReservations;
	}

	public List<PassengerRecord> getNoGettingOnPassengerRecords() {
		List<PassengerRecord> noGettingOnReservations = new LinkedList<PassengerRecord>();
		for (PassengerRecord passengerRecord : service.getPassengerRecords()) {
			if (canGetOn(passengerRecord) && isGetOnScheduled(passengerRecord)
					&& !isSelected(passengerRecord)) {
				noGettingOnReservations.add(passengerRecord);
			}
		}
		return noGettingOnReservations;
	}

	public List<PassengerRecord> getNoPaymentPassengerRecords() {
		List<PassengerRecord> noPaymentPassengerRecords = new LinkedList<PassengerRecord>();
		return noPaymentPassengerRecords;
	}

	public Boolean isGetOffScheduled(PassengerRecord passengerRecord) {
		if (!service.getCurrentOperationSchedule().isPresent()) {
			return false;
		}
		OperationSchedule operationSchedule = service.getCurrentOperationSchedule().get();

		// 降車予定かどうか
		if (!passengerRecord.getReservation().isPresent()) {
			return false;
		}
		Reservation reservation = passengerRecord.getReservation().get();
		return reservation.getArrivalScheduleId().isPresent()
				&& reservation.getArrivalScheduleId().get()
						.equals(operationSchedule.getId());
	}

	public Boolean isGetOnScheduled(PassengerRecord passengerRecord) {
		if (!service.getCurrentOperationSchedule().isPresent()) {
			return false;
		}
		OperationSchedule operationSchedule = service.getCurrentOperationSchedule().get();

		// 乗車予定かどうか
		if (!passengerRecord.getReservation().isPresent()) {
			return false;
		}
		Reservation reservation = passengerRecord.getReservation().get();
		return reservation.getDepartureScheduleId().isPresent()
				&& reservation.getDepartureScheduleId().get()
						.equals(operationSchedule.getId());
	}

	public Boolean isSelected(PassengerRecord passengerRecord) {
		if (!service.getCurrentOperationSchedule().isPresent()) {
			return false;
		}
		OperationSchedule operationSchedule = service.getCurrentOperationSchedule().get();

		if (passengerRecord.isRiding()) {
			return operationSchedule.getId().equals(
					passengerRecord.getDepartureOperationScheduleId().orNull());
		} else if (passengerRecord.isGotOff()) {
			return operationSchedule.getId().equals(
					passengerRecord.getArrivalOperationScheduleId().orNull());
		}
		return false;
	}

	public void select(PassengerRecord passengerRecord) {
		if (!service.getCurrentOperationSchedule().isPresent()) {
			return;
		}
		OperationSchedule operationSchedule = service.getCurrentOperationSchedule().get();

		if (!passengerRecord.getReservation().isPresent()) {
			return;
		}
		Reservation reservation = passengerRecord.getReservation().get();
		passengerRecord.setPassengerCount(reservation.getPassengerCount());
		DataSource dataSource = service.getRemoteDataSource(); 
		if (getGetOffScheduledAndUnhandledPassengerRecords()
				.contains(passengerRecord)) {
			passengerRecord.setGetOnTime(new Date());
			passengerRecord.setGetOffTime(new Date());
			passengerRecord.setDepartureOperationScheduleId(reservation
					.getDepartureScheduleId());
			passengerRecord.setArrivalOperationScheduleId(operationSchedule
					.getId());
			dataSource.saveOnClose(dataSource.getOnPassenger(operationSchedule,
					reservation, passengerRecord,
					new EmptyWebAPICallback<PassengerRecord>()));
			dataSource.saveOnClose(dataSource.getOffPassenger(operationSchedule,
					reservation, passengerRecord,
					new EmptyWebAPICallback<PassengerRecord>()));
		} else if (passengerRecord.isUnhandled()) {
			passengerRecord.setGetOnTime(new Date());
			passengerRecord.setDepartureOperationScheduleId(operationSchedule
					.getId());
			dataSource.saveOnClose(dataSource.getOnPassenger(operationSchedule,
					reservation, passengerRecord,
					new EmptyWebAPICallback<PassengerRecord>()));
		} else if (passengerRecord.isRiding()) {
			passengerRecord.setGetOffTime(new Date());
			passengerRecord.setArrivalOperationScheduleId(operationSchedule
					.getId());
			dataSource.saveOnClose(dataSource.getOffPassenger(operationSchedule,
					reservation, passengerRecord,
					new EmptyWebAPICallback<PassengerRecord>()));
		}
	}

	public void unselect(PassengerRecord passengerRecord) {
		if (!service.getCurrentOperationSchedule().isPresent()) {
			return;
		}
		OperationSchedule operationSchedule = service.getCurrentOperationSchedule().get();

		if (!passengerRecord.getReservation().isPresent()) {
			return;
		}
		DataSource dataSource = service.getRemoteDataSource();
		Reservation reservation = passengerRecord.getReservation().get();
		if (getGetOffScheduledAndUnhandledPassengerRecords()
				.contains(passengerRecord)) {
			passengerRecord.clearGetOnTime();
			passengerRecord.clearGetOffTime();
			passengerRecord.clearDepartureOperationScheduleId();
			passengerRecord.clearArrivalOperationScheduleId();
			reservation.setPassengerRecord(passengerRecord); // TODO:消す
			dataSource.saveOnClose(dataSource.cancelGetOffPassenger(operationSchedule,
					reservation, new EmptyWebAPICallback<PassengerRecord>()));
			dataSource.saveOnClose(dataSource.cancelGetOnPassenger(operationSchedule,
					reservation, new EmptyWebAPICallback<PassengerRecord>()));
		} else if (passengerRecord.isRiding()) {
			passengerRecord.clearGetOnTime();
			passengerRecord.clearDepartureOperationScheduleId();
			reservation.setPassengerRecord(passengerRecord); // TODO:消す
			dataSource.saveOnClose(dataSource.cancelGetOnPassenger(operationSchedule,
					reservation, new EmptyWebAPICallback<PassengerRecord>()));
		} else if (passengerRecord.isGotOff()) {
			passengerRecord.clearGetOffTime();
			passengerRecord.clearArrivalOperationScheduleId();
			reservation.setPassengerRecord(passengerRecord); // TODO:消す
			dataSource.saveOnClose(dataSource.cancelGetOffPassenger(operationSchedule,
					reservation, new EmptyWebAPICallback<PassengerRecord>()));
		}
	}

	public List<PassengerRecord> getGetOffScheduledAndUnhandledPassengerRecords() {
		List<PassengerRecord> passengerRecords = new LinkedList<PassengerRecord>();
		for (PassengerRecord passengerRecord : service.getPassengerRecords()) {
			if (service.isGetOffScheduled(passengerRecord)
					&& passengerRecord.isUnhandled()) {
				passengerRecords.add(passengerRecord);
			}
		}
		return passengerRecords;
	}
}