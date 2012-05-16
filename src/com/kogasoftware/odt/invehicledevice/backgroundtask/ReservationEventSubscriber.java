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
import com.kogasoftware.odt.invehicledevice.logic.event.SelectedReservationsUpdateEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.UiEventBus;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.PassengerRecords;
import com.kogasoftware.odt.webapi.model.Reservation;

/**
 * 予約に関する内部データ処理
 */
@UiEventBus.HighPriority
public class ReservationEventSubscriber {
	private static final String TAG = ReservationEventSubscriber.class
			.getSimpleName();

	private final CommonLogic commonLogic;
	private final StatusAccess statusAccess;

	public ReservationEventSubscriber(CommonLogic commonLogic,
			StatusAccess statusAccess) {
		this.commonLogic = commonLogic;
		this.statusAccess = statusAccess;
	}

	/**
	 * 降車処理
	 */
	@Subscribe
	public void getOff(final GetOffEvent e) {
		statusAccess.write(new Writer() {
			@Override
			public void write(Status status) {
				Date now = CommonLogic.getDate();
				for (Reservation reservation : e.reservations) {
					if (!reservation.getPassengerRecord().isPresent()) {
						continue;
					}
					PassengerRecord passengerRecord = reservation
							.getPassengerRecord().get();
					passengerRecord.setStatus(PassengerRecords.Status.GOT_OFF);
					passengerRecord.setUpdatedAt(now);
					passengerRecord.setGetOffTime(now);
					passengerRecord
							.setArrivalOperationScheduleId(e.operationSchedule
									.getId());
					passengerRecord
							.setArrivalOperationSchedule(e.operationSchedule);
					commonLogic.getDataSource().getOffPassenger(
							e.operationSchedule, reservation, passengerRecord,
							new EmptyWebAPICallback<PassengerRecord>());
				}
			}
		});
	}

	/**
	 * 乗車処理
	 */
	@Subscribe
	public void getOn(final GetOnEvent e) {
		statusAccess.write(new Writer() {
			@Override
			public void write(Status status) {
				Date now = CommonLogic.getDate();
				for (Reservation reservation : e.reservations) {
					if (!reservation.getPassengerRecord().isPresent()) {
						continue;
					}
					PassengerRecord passengerRecord = reservation
							.getPassengerRecord().get();
					passengerRecord.setStatus(PassengerRecords.Status.RIDING);
					passengerRecord.setUpdatedAt(now);
					passengerRecord.setGetOnTime(now);
					passengerRecord
							.setDepartureOperationScheduleId(e.operationSchedule
									.getId());
					passengerRecord
							.setDepartureOperationSchedule(e.operationSchedule);
					if (!commonLogic.getPayTiming().contains(PayTiming.GET_OFF)
							&& commonLogic.getPayTiming().contains(
									PayTiming.GET_ON)
							&& passengerRecord.getReservation().isPresent()) {
						passengerRecord.setPayment(passengerRecord
								.getReservation().get().getPayment());
					}
					commonLogic.getDataSource().getOnPassenger(
							e.operationSchedule, reservation, passengerRecord,
							new EmptyWebAPICallback<PassengerRecord>());
				}
			}
		});
	}

	/**
	 * 選択済みのReservationを保存
	 */
	@Subscribe
	public void setSelectedReservations(final SelectedReservationsUpdateEvent e) {
		statusAccess.write(new Writer() {
			@Override
			public void write(Status status) {
				status.selectedReservations.clear();
				status.selectedReservations.addAll(e.reservations);
			}
		});
	}
}
