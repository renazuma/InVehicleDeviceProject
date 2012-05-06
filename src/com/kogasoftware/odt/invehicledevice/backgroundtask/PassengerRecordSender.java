package com.kogasoftware.odt.invehicledevice.backgroundtask;

import java.util.List;

import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.Status;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Writer;
import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.Reservation;

/**
 * PassengerRecordをサーバーに送信す
 * 
 * @deprecated WebAPIのリトライ機能により不必要になる予定
 */
@Deprecated
public class PassengerRecordSender implements Runnable {
	private final CommonLogic commonLogic;

	public PassengerRecordSender(CommonLogic commonLogic) {
		this.commonLogic = commonLogic;
	}

	private void remove(final PassengerRecord passengerRecord,
			final Boolean getOn) {

		commonLogic.getStatusAccessDeprecated().write(new Writer() {
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

	@Override
	public void run() {
		List<PassengerRecord> getOnPassengerRecords = commonLogic
				.getGetOnPassengerRecords();

		List<PassengerRecord> getOffPassengerRecords = commonLogic
				.getGetOffPassengerRecords();
		send(commonLogic, getOnPassengerRecords, true);
		send(commonLogic, getOffPassengerRecords, false);
	}

	void send(CommonLogic commonLogic, List<PassengerRecord> passengerRecords,
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
					commonLogic.getDataSource().getOnPassenger(
							operationSchedule.get(), reservation.get(),
							passengerRecord, callback);
				} else {
					commonLogic.getDataSource().getOffPassenger(
							operationSchedule.get(), reservation.get(),
							passengerRecord, callback);
				}
				remove(passengerRecord, getOn);
			} catch (WebAPIException e) {
			}
		}
	}
}
