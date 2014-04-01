package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.logic;

import java.util.Date;

import org.joda.time.DateTimeUtils;

import android.util.Log;

import com.google.common.collect.Lists;
import com.kogasoftware.odt.apiclient.EmptyApiClientCallback;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Reservation;
import com.kogasoftware.odt.invehicledevice.apiclient.model.User;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage.BackgroundWriter;

public class PassengerRecordLogic {
	private static final String TAG = PassengerRecordLogic.class
			.getSimpleName();
	public final InVehicleDeviceService service;
	public final OperationScheduleLogic operationScheduleLogic;

	public PassengerRecordLogic(InVehicleDeviceService service) {
		this.service = service;
		operationScheduleLogic = new OperationScheduleLogic(service);
	}

	public void getOff(OperationSchedule operationSchedule,
			PassengerRecord passengerRecord) {
		if (!passengerRecord.getReservation().isPresent()) {
			Log.e(TAG, "passengerRecord (" + passengerRecord
					+ ") has no Reservation");
			return;
		}
		Date now = new Date(DateTimeUtils.currentTimeMillis());
		Reservation reservation = passengerRecord.getReservation().get();

		if (!passengerRecord.getUser().isPresent()) {
			Log.e(TAG, "passengerRecord (" + passengerRecord + ") has no User");
			return;
		}
		User user = passengerRecord.getUser().get();
		if (passengerRecord.getIgnoreGetOnMiss()) {
			passengerRecord.setGetOnTime(now);
			passengerRecord.setDepartureOperationScheduleId(reservation
					.getDepartureScheduleId());
			service.getApiClient()
					.withSaveOnClose()
					.getOnPassenger(operationSchedule, reservation, user,
							passengerRecord, new EmptyApiClientCallback<Void>());
		}
		passengerRecord.setGetOffTime(now);
		passengerRecord.setPassengerCount(reservation.getPassengerCount());
		passengerRecord
				.setArrivalOperationScheduleId(operationSchedule.getId());
		service.getApiClient()
				.withSaveOnClose()
				.getOffPassenger(operationSchedule, reservation, user,
						passengerRecord, new EmptyApiClientCallback<Void>());
		updateAsync(passengerRecord);
	}

	public void cancelGetOff(OperationSchedule operationSchedule,
			PassengerRecord passengerRecord) {
		if (!passengerRecord.getReservation().isPresent()) {
			Log.e(TAG, "passengerRecord (" + passengerRecord
					+ ") has no Reservation");
			return;
		}
		Reservation reservation = passengerRecord.getReservation().get();

		if (!passengerRecord.getUser().isPresent()) {
			Log.e(TAG, "passengerRecord (" + passengerRecord + ") has no User");
			return;
		}
		User user = passengerRecord.getUser().get();

		passengerRecord.clearGetOffTime();
		passengerRecord.clearArrivalOperationScheduleId();
		if (passengerRecord.getIgnoreGetOnMiss()) {
			passengerRecord.clearGetOnTime();
			passengerRecord.clearDepartureOperationScheduleId();
		}
		service.getApiClient()
				.withSaveOnClose()
				.cancelGetOffPassenger(operationSchedule, reservation, user,
						new EmptyApiClientCallback<Void>());
		if (passengerRecord.getIgnoreGetOnMiss()) {
			service.getApiClient()
					.withSaveOnClose()
					.cancelGetOnPassenger(operationSchedule, reservation, user,
							new EmptyApiClientCallback<Void>());
		}
		updateAsync(passengerRecord);
	}

	public void getOn(OperationSchedule operationSchedule,
			final PassengerRecord passengerRecord) {
		if (!passengerRecord.getReservation().isPresent()) {
			Log.e(TAG, "passengerRecord (" + passengerRecord
					+ ") has no Reservation");
			return;
		}
		Reservation reservation = passengerRecord.getReservation().get();

		if (!passengerRecord.getUser().isPresent()) {
			Log.e(TAG, "passengerRecord (" + passengerRecord + ") has no User");
			return;
		}
		User user = passengerRecord.getUser().get();

		passengerRecord.setGetOnTime(new Date(DateTimeUtils.currentTimeMillis()));
		passengerRecord.setPassengerCount(reservation.getPassengerCount());
		passengerRecord.setDepartureOperationScheduleId(operationSchedule
				.getId());
		updateAsync(passengerRecord);
		service.getApiClient()
				.withSaveOnClose()
				.getOnPassenger(operationSchedule, reservation, user,
						passengerRecord, new EmptyApiClientCallback<Void>());
	}

	public void cancelGetOn(OperationSchedule operationSchedule,
			PassengerRecord passengerRecord) {
		if (!passengerRecord.getReservation().isPresent()) {
			Log.e(TAG, "passengerRecord (" + passengerRecord
					+ ") has no Reservation");
			return;
		}
		Reservation reservation = passengerRecord.getReservation().get();

		if (!passengerRecord.getUser().isPresent()) {
			Log.e(TAG, "passengerRecord (" + passengerRecord + ") has no User");
			return;
		}
		User user = passengerRecord.getUser().get();

		passengerRecord.clearGetOnTime();
		passengerRecord.clearDepartureOperationScheduleId();
		updateAsync(passengerRecord);
		service.getApiClient()
				.withSaveOnClose()
				.cancelGetOnPassenger(operationSchedule, reservation, user,
						new EmptyApiClientCallback<Void>());
	}

	private void updateAsync(final PassengerRecord passengerRecord) {
		service.getLocalStorage().write(new BackgroundWriter() {
			@Override
			public void writeInBackground(LocalData ld) {
				for (PassengerRecord oldPassengerRecord : Lists
						.newArrayList(ld.operation.passengerRecords)) {
					if (oldPassengerRecord.getId().equals(
							passengerRecord.getId())) {
						ld.operation.passengerRecords.remove(oldPassengerRecord);
						ld.operation.passengerRecords.add(passengerRecord);
					}
				}
			}

			@Override
			public void onWrite() {
				service.getEventDispatcher().dispatchUpdatePassengerRecord(passengerRecord);
			}
		});
	}

	public void setIgnoreGetOffMiss(PassengerRecord passengerRecord,
			Boolean value) {
		passengerRecord.setIgnoreGetOffMiss(value);
		updateAsync(passengerRecord);
	}

	public void setIgnoreGetOnMiss(PassengerRecord passengerRecord,
			Boolean value) {
		passengerRecord.setIgnoreGetOnMiss(value);
		updateAsync(passengerRecord);
	}
}
