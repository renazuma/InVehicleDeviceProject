package com.kogasoftware.odt.invehicledevice.backgroundtask;

import java.util.Date;

import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic.PayTiming;
import com.kogasoftware.odt.invehicledevice.logic.Status;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Writer;
import com.kogasoftware.odt.invehicledevice.logic.empty.EmptyWebAPICallback;
import com.kogasoftware.odt.invehicledevice.logic.event.GetOffEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.GetOnEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.UiEventBus;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.Reservation;

/**
 * 乗客に関する内部データ処理
 */
@UiEventBus.HighPriority
public class PassengerRecordEventSubscriber {
	private static final String TAG = PassengerRecordEventSubscriber.class
			.getSimpleName();

	private final CommonLogic commonLogic;
	private final StatusAccess statusAccess;

	public PassengerRecordEventSubscriber(CommonLogic commonLogic,
			StatusAccess statusAccess) {
		this.commonLogic = commonLogic;
		this.statusAccess = statusAccess;
	}

	/**
	 * 降車処理
	 */
	@Subscribe
	public void getOff(final GetOffEvent e) {
		Date now = CommonLogic.getDate();
		for (PassengerRecord passengerRecord : e.getOffPassengerRecords) {
			if (!passengerRecord.getReservation().isPresent()) {
				continue;
			}
			Reservation reservation = passengerRecord.getReservation().get();
			passengerRecord.setGetOffTime(now);
			passengerRecord.setArrivalOperationScheduleId(e.operationSchedule
					.getId());
			passengerRecord.setArrivalOperationSchedule(e.operationSchedule);
			commonLogic.getDataSource().getOffPassenger(e.operationSchedule,
					reservation, passengerRecord,
					new EmptyWebAPICallback<PassengerRecord>());
		}
		statusAccess.write(new Writer() {
			@Override
			public void write(Status status) {
				status.ridingPassengerRecords
						.removeAll(e.getOffPassengerRecords);
				status.finishedPassengerRecords
						.addAll(e.getOffPassengerRecords);
			}
		});
	}

	/**
	 * 乗車処理
	 */
	@Subscribe
	public void getOn(final GetOnEvent e) {
		Date now = CommonLogic.getDate();
		for (PassengerRecord passengerRecord : e.getOnPassengerRecords) {
			if (!passengerRecord.getReservation().isPresent()) {
				continue;
			}
			Reservation reservation = passengerRecord.getReservation().get();
			passengerRecord.setGetOnTime(now);
			passengerRecord.setDepartureOperationScheduleId(e.operationSchedule
					.getId());
			passengerRecord.setDepartureOperationSchedule(e.operationSchedule);
			if (!commonLogic.getPayTiming().contains(PayTiming.GET_OFF)
					&& commonLogic.getPayTiming().contains(PayTiming.GET_ON)
					&& passengerRecord.getReservation().isPresent()) {
				passengerRecord.setPayment(passengerRecord.getReservation()
						.get().getPayment());
			}
			commonLogic.getDataSource().getOnPassenger(e.operationSchedule,
					reservation, passengerRecord,
					new EmptyWebAPICallback<PassengerRecord>());
		}
		statusAccess.write(new Writer() {
			@Override
			public void write(Status status) {
				status.unhandledPassengerRecords
						.removeAll(e.getOnPassengerRecords);
				status.ridingPassengerRecords.addAll(e.getOnPassengerRecords);
			}
		});
	}
}
