package com.kogasoftware.odt.invehicledevice.logic;

import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Reader;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Writer;
import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.Reservation;

public class PassengerRecordSender extends LogicUser implements Runnable {

	private void remove(final PassengerRecord passengerRecord,
			final Boolean getOn) {
		if (getLogic().isPresent()) {
			getLogic().get().getStatusAccess().write(new Writer() {
				@Override
				public void write(Status status) {
					if (getOn) {
						status.sendLists.getOnPassengerRecords
								.remove(passengerRecord);
					} else {
						status.sendLists.getOffPassengerRecords
								.remove(passengerRecord);
					}
				}
			});
		}
	}

	@Override
	public void run() {
		if (!getLogic().isPresent()) {
			return;
		}
		final Logic logic = getLogic().get();
		List<PassengerRecord> getOnPassengerRecords = logic.getStatusAccess()
				.read(new Reader<List<PassengerRecord>>() {
					@Override
					public List<PassengerRecord> read(Status status) {
						return new LinkedList<PassengerRecord>(
								status.sendLists.getOnPassengerRecords);
					}
				});
		List<PassengerRecord> getOffPassengerRecords = logic.getStatusAccess()
				.read(new Reader<List<PassengerRecord>>() {
					@Override
					public List<PassengerRecord> read(Status status) {
						return new LinkedList<PassengerRecord>(
								status.sendLists.getOffPassengerRecords);
					}
				});
		send(logic, getOnPassengerRecords, true);
		send(logic, getOffPassengerRecords, false);
	}

	void send(Logic logic, List<PassengerRecord> passengerRecords,
			final Boolean getOn) {
		for (final PassengerRecord passengerRecord : passengerRecords) {
			Optional<Reservation> reservation = passengerRecord
					.getReservation();
			if (!reservation.isPresent()) {
				remove(passengerRecord, getOn);
				continue;
			}

			Optional<OperationSchedule> operationSchedule = Optional.absent();
			if (getOn) {
				operationSchedule = passengerRecord
						.getDepartureOperationSchedule();
			} else {
				operationSchedule = passengerRecord
						.getArrivalOperationSchedule();
			}

			WebAPICallback<PassengerRecord> callback = new WebAPICallback<PassengerRecord>() {
				@Override
				public void onException(int reqkey, WebAPIException ex) {
				}

				@Override
				public void onFailed(int reqkey, int statusCode, String response) {
				}

				@Override
				public void onSucceed(int reqkey, int statusCode,
						PassengerRecord result) {
					remove(passengerRecord, getOn);
				}
			};

			try {
				if (getOn) {
					logic.getDataSource().getOnPassenger(
							operationSchedule.get(), reservation.get(),
							passengerRecord, callback);
				} else {
					logic.getDataSource().getOffPassenger(
							operationSchedule.get(), reservation.get(),
							passengerRecord, callback);
				}
				remove(passengerRecord, getOn);
			} catch (WebAPIException e) {
			}
		}
	}
}
