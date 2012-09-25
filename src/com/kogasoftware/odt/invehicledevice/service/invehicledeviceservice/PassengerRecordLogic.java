package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.datasource.DataSource;
import com.kogasoftware.odt.invehicledevice.empty.EmptyWebAPICallback;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.User;

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
		OperationSchedule operationSchedule = service
				.getCurrentOperationSchedule().get();

		// 降車予定かどうか
		if (!passengerRecord.getReservation().isPresent()) {
			return false;
		}
		Reservation reservation = passengerRecord.getReservation().get();
		return reservation.getArrivalScheduleId().equals(
				Optional.of(operationSchedule.getId()));
	}

	public Boolean isGetOnScheduled(PassengerRecord passengerRecord) {
		if (!service.getCurrentOperationSchedule().isPresent()) {
			return false;
		}
		OperationSchedule operationSchedule = service
				.getCurrentOperationSchedule().get();

		// 乗車予定かどうか
		if (!passengerRecord.getReservation().isPresent()) {
			return false;
		}
		Reservation reservation = passengerRecord.getReservation().get();
		return reservation.getDepartureScheduleId().equals(
				Optional.of(operationSchedule.getId()));
	}

	public Boolean isSelected(PassengerRecord passengerRecord) {
		if (!service.getCurrentOperationSchedule().isPresent()) {
			return false;
		}
		OperationSchedule operationSchedule = service
				.getCurrentOperationSchedule().get();

		if (passengerRecord.isRiding()) {
			return passengerRecord.getDepartureOperationScheduleId().equals(
					Optional.of(operationSchedule.getId()));
		} else if (passengerRecord.isGotOff()) {
			return passengerRecord.getArrivalOperationScheduleId().equals(
					Optional.of(operationSchedule.getId()));
		}
		return false;
	}

	public void select(PassengerRecord passengerRecord) {
		if (!service.getCurrentOperationSchedule().isPresent()) {
			return;
		}
		OperationSchedule operationSchedule = service
				.getCurrentOperationSchedule().get();
		if (!passengerRecord.getReservation().isPresent()
				|| !passengerRecord.getUser().isPresent()) {
			return;
		}
		Reservation reservation = passengerRecord.getReservation().get();
		User user = passengerRecord.getUser().get();
		passengerRecord.setPassengerCount(reservation.getPassengerCount());
		DataSource dataSource = service.getRemoteDataSource();
		Date now = InVehicleDeviceService.getDate();
		if (isGetOffScheduled(passengerRecord) && passengerRecord.isUnhandled()) {
			passengerRecord.setGetOnTime(now);
			passengerRecord.setGetOffTime(now);
			passengerRecord.setDepartureOperationScheduleId(reservation
					.getDepartureScheduleId());
			passengerRecord.setArrivalOperationScheduleId(operationSchedule
					.getId());
			dataSource.withSaveOnClose().getOnPassenger(operationSchedule,
					reservation, user, passengerRecord,
					new EmptyWebAPICallback<Void>());
			dataSource.withSaveOnClose().getOffPassenger(operationSchedule,
					reservation, user, passengerRecord,
					new EmptyWebAPICallback<Void>());
		} else if (passengerRecord.isUnhandled()) {
			passengerRecord.setGetOnTime(now);
			passengerRecord.setDepartureOperationScheduleId(operationSchedule
					.getId());
			dataSource.withSaveOnClose().getOnPassenger(operationSchedule,
					reservation, user, passengerRecord,
					new EmptyWebAPICallback<Void>());
		} else if (passengerRecord.isRiding() || passengerRecord.isGotOff()) {
			passengerRecord.setGetOffTime(now);
			passengerRecord.setArrivalOperationScheduleId(operationSchedule
					.getId());
			dataSource.withSaveOnClose().getOffPassenger(operationSchedule,
					reservation, user, passengerRecord,
					new EmptyWebAPICallback<Void>());
		}
	}

	public void unselect(PassengerRecord passengerRecord) {
		if (!service.getCurrentOperationSchedule().isPresent()) {
			return;
		}
		OperationSchedule operationSchedule = service
				.getCurrentOperationSchedule().get();

		if (!passengerRecord.getReservation().isPresent()
				|| !passengerRecord.getUser().isPresent()) {
			return;
		}
		Reservation reservation = passengerRecord.getReservation().get();
		User user = passengerRecord.getUser().get();
		DataSource dataSource = service.getRemoteDataSource();
		if (isGetOffScheduled(passengerRecord)
				&& passengerRecord.isGotOff()
				&& passengerRecord.getGetOnTime().equals(
						passengerRecord.getGetOffTime())) {
			passengerRecord.clearGetOnTime();
			passengerRecord.clearGetOffTime();
			passengerRecord.clearDepartureOperationScheduleId();
			passengerRecord.clearArrivalOperationScheduleId();
			dataSource.withSaveOnClose().cancelGetOffPassenger(
					operationSchedule, reservation, user,
					new EmptyWebAPICallback<Void>());
			dataSource.withSaveOnClose().cancelGetOnPassenger(
					operationSchedule, reservation, user,
					new EmptyWebAPICallback<Void>());
		} else if (passengerRecord.isUnhandled() || passengerRecord.isRiding()) {
			passengerRecord.clearGetOnTime();
			passengerRecord.clearDepartureOperationScheduleId();
			dataSource.withSaveOnClose().cancelGetOnPassenger(
					operationSchedule, reservation, user,
					new EmptyWebAPICallback<Void>());
		} else if (passengerRecord.isGotOff()) {
			passengerRecord.clearGetOffTime();
			passengerRecord.clearArrivalOperationScheduleId();
			dataSource.withSaveOnClose().cancelGetOffPassenger(
					operationSchedule, reservation, user,
					new EmptyWebAPICallback<Void>());
		}
	}
}
