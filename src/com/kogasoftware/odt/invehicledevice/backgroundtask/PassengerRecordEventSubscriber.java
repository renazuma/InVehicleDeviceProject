package com.kogasoftware.odt.invehicledevice.backgroundtask;

import java.util.Date;
import java.util.List;

import android.util.Log;

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
import com.kogasoftware.odt.invehicledevice.logic.event.UnexpectedReservationAddedEvent;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.Reservations;
import com.kogasoftware.odt.webapi.model.User;

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
	 * 飛び乗り予約を追加する
	 */
	@Subscribe
	public void addUnexpectedPassenger(UnexpectedReservationAddedEvent e) {
		List<OperationSchedule> operationSchedules = commonLogic
				.getRemainingOperationSchedules();
		if (operationSchedules.isEmpty()) {
			Log.w(TAG, "operationSchedules.isEmpty()", new Exception());
			return;
		}
		OperationSchedule operationSchedule = operationSchedules.get(0);
		final Reservation reservation = new Reservation();

		reservation.setId(Reservations.UNEXPECTED_RESERVATION_ID);
		// 未予約乗車の予約情報はどうするか
		reservation.setDepartureScheduleId(operationSchedule.getId());
		reservation.setArrivalScheduleId(e.arrivalOperationScheduleId);
		final User user = new User();
		statusAccess.write(new Writer() {
			@Override
			public void write(Status status) {
				user.setFirstName("飛び乗りユーザー"
						+ status.unexpectedReservationSequence);
				status.unexpectedReservationSequence++;
			}
		});
		reservation.setUser(user);
		final PassengerRecord passengerRecord = new PassengerRecord();
		passengerRecord.setReservationId(reservation.getId());
		passengerRecord.setReservation(reservation);
		statusAccess.write(new Writer() {
			@Override
			public void write(Status status) {
				status.unexpectedPassengerRecords.add(passengerRecord);
				status.unhandledPassengerRecords.add(passengerRecord);
			}
		});
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
