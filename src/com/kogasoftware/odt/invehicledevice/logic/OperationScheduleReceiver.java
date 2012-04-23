package com.kogasoftware.odt.invehicledevice.logic;

import java.util.LinkedList;
import java.util.List;

import com.kogasoftware.odt.invehicledevice.Utility;
import com.kogasoftware.odt.invehicledevice.event.UpdateOperationScheduleCompleteEvent;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Writer;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.Reservation;

public class OperationScheduleReceiver extends LogicUser implements Runnable {
	final Logic logic;

	public OperationScheduleReceiver(Logic logic) {
		this.logic = logic;
	}

	@Override
	public void run() {
		final List<OperationSchedule> operationSchedules = new LinkedList<OperationSchedule>();
		while (true) {
			try {
				operationSchedules.addAll(logic.getDataSource()
						.getOperationSchedules());
				break;
			} catch (WebAPIException e) {
				e.printStackTrace(); // TODO
			}
			try {
				Thread.sleep(5000); // TODO
			} catch (InterruptedException e) {
				return;
			}
		}
		logic.getStatusAccess().write(new Writer() {
			@Override
			public void write(Status status) {
				update(status, logic, operationSchedules);
			}
		});
	}

	private void update(Status status, Logic logic,
			List<OperationSchedule> newOperationSchedules) {

		// 未乗車のPassengerRecordは削除
		status.unhandledPassengerRecords.clear();

		// 予約の追加、書き換え
		for (OperationSchedule operationSchedule : newOperationSchedules) {
			for (Reservation reservation : operationSchedule
					.getReservationsAsDeparture()) {
				updateReservation(status, reservation);
			}
		}

		// 新規の場合はスケジュールを全て交換して終了
		if (!status.initialized) {
			status.remainingOperationSchedules.clear();
			status.finishedOperationSchedules.clear();
			status.remainingOperationSchedules.addAll(newOperationSchedules);
			status.initialized = true;
			return;
		}

		// OperationScheduleの巻き戻し
		LinkedList<OperationSchedule> newRemainingOperationSchedules = new LinkedList<OperationSchedule>(
				newOperationSchedules);
		List<OperationSchedule> newFinishedOperationSchedules = new LinkedList<OperationSchedule>();
		for (OperationSchedule finishedOperationSchedule : status.finishedOperationSchedules) {
			if (newRemainingOperationSchedules.isEmpty()) {
				break;
			}
			if (!newRemainingOperationSchedules.get(0).getId()
					.equals(finishedOperationSchedule.getId())) {
				break;
			}
			newFinishedOperationSchedules.add(newRemainingOperationSchedules
					.pop());
		}

		// 現在乗降場待機画面で、現在のOperationScheduleが無効になった場合強制的に運行中に設定
		if (status.phase == Status.Phase.PLATFORM
				&& !status.remainingOperationSchedules.isEmpty()
				&& !Utility.containsById(newRemainingOperationSchedules,
						status.remainingOperationSchedules.get(0))) {
			status.phase = Status.Phase.DRIVE;
		}

		status.remainingOperationSchedules.clear();
		status.remainingOperationSchedules
				.addAll(newRemainingOperationSchedules);
		status.finishedOperationSchedules.clear();
		status.finishedOperationSchedules.addAll(newFinishedOperationSchedules);

		logic.getEventBus().post(new UpdateOperationScheduleCompleteEvent());
	}

	private void updateReservation(Status status, Reservation reservation) {
		// Reservationに対応するPassengerRecordを新規作成し、未乗車のものと交換
		// 乗車済み、降車済みのPassengerRecordの中に既に対応するものが存在する場合、それのReservationを交換
		for (PassengerRecord passengerRecord : status.ridingPassengerRecords) {
			if (passengerRecord.getReservation().isPresent()) {
				if (passengerRecord.getReservation().get().getId()
						.equals(reservation.getId())) {
					passengerRecord.setReservation(reservation);
					return;
				}
			}
		}
		for (PassengerRecord passengerRecord : status.finishedPassengerRecords) {
			if (passengerRecord.getReservation().isPresent()) {
				if (passengerRecord.getReservation().get().getId()
						.equals(reservation.getId())) {
					passengerRecord.setReservation(reservation);
					return;
				}
			}
		}
		PassengerRecord passengerRecord = new PassengerRecord();
		passengerRecord.setReservation(reservation);
		passengerRecord.setPassengerCount(reservation.getPassengerCount());
		status.unhandledPassengerRecords.add(passengerRecord);
	}
}
