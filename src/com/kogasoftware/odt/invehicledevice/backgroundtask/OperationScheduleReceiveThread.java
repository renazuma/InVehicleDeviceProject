package com.kogasoftware.odt.invehicledevice.backgroundtask;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.CommonLogic;
import com.kogasoftware.odt.invehicledevice.Status;
import com.kogasoftware.odt.invehicledevice.StatusAccess.VoidReader;
import com.kogasoftware.odt.invehicledevice.StatusAccess.Writer;
import com.kogasoftware.odt.invehicledevice.Utility;
import com.kogasoftware.odt.invehicledevice.event.StartOperationScheduleUpdateEvent;
import com.kogasoftware.odt.invehicledevice.event.UpdateOperationScheduleCompleteEvent;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class OperationScheduleReceiveThread extends Thread {
	private final CommonLogic commonLogic;
	private final BlockingQueue<VehicleNotification> queuedVehicleNotifications = new LinkedBlockingQueue<VehicleNotification>();

	public OperationScheduleReceiveThread(CommonLogic commonLogic) {
		this.commonLogic = commonLogic;
		reloadVehicleNotification();
	}

	private void receive(final List<VehicleNotification> vehicleNotifications)
			throws WebAPIException {
		final List<OperationSchedule> operationSchedules = new LinkedList<OperationSchedule>();
		operationSchedules.addAll(commonLogic.getDataSource()
				.getOperationSchedules());

		commonLogic.getStatusAccess().write(new Writer() {
			@Override
			public void write(Status status) {
				update(status, commonLogic, operationSchedules,
						vehicleNotifications);
			}
		});
	}

	public void reloadVehicleNotification() {
		commonLogic.getStatusAccess().read(new VoidReader() {
			@Override
			public void read(Status status) {
				Utility.mergeById(queuedVehicleNotifications,
						status.operationScheduleChangedVehicleNotifications);
			}
		});
	}

	@Subscribe
	public void reloadVehicleNotification(StartOperationScheduleUpdateEvent e) {
		reloadVehicleNotification();
	}

	@Override
	public void run() {
		try {
			// 初回のスケジュールの受信
			if (!commonLogic.isOperationScheduleInitialized()) {
				while (true) {
					Thread.sleep(0); // interruption point
					try {
						receive(new LinkedList<VehicleNotification>());
						break;
					} catch (WebAPIException e) {
						e.printStackTrace();
					}
				}
			}

			// 初回以降のスケジュールの受信
			while (true) {
				final List<VehicleNotification> workingVehicleNotification = new LinkedList<VehicleNotification>();

				// スケジュール変更通知があるまで待つ
				workingVehicleNotification.add(queuedVehicleNotifications
						.take());

				while (true) {
					try {
						// 新しいスケジュール変更通知があるかもしれないので、一定時間待ってからマージしておく
						Thread.sleep(500);
						Utility.mergeById(workingVehicleNotification,
								queuedVehicleNotifications);

						receive(workingVehicleNotification);
						break;
					} catch (WebAPIException e) {
						e.printStackTrace(); // TODO
					}
				}

				queuedVehicleNotifications
						.removeAll(workingVehicleNotification);
			}
		} catch (InterruptedException e) {
			// 正常終了
		}
	}

	private void update(Status status, CommonLogic commonLogic,
			List<OperationSchedule> newOperationSchedules,
			List<VehicleNotification> vehicleNotifications) {

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
		if (!commonLogic.isOperationScheduleInitialized()) {
			status.remainingOperationSchedules.clear();
			status.finishedOperationSchedules.clear();
			status.remainingOperationSchedules.addAll(newOperationSchedules);
			commonLogic.setOperationScheduleInitialized();
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

		commonLogic.speak("運行予定が変更されました");
		commonLogic.getEventBus().post(
				new UpdateOperationScheduleCompleteEvent(vehicleNotifications));
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
