package com.kogasoftware.odt.invehicledevice.apiclient.model;

import java.util.List;
import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.apiclient.model.base.OperationScheduleBase;

public class UnmergedOperationSchedule extends OperationScheduleBase {
	private static final long serialVersionUID = 1040628741311146500L;

	/**
	 * 出発済みか調べる
	 */
	public Boolean isDeparted() {
		for (OperationRecord operationRecord : getOperationRecord().asSet()) {
			return operationRecord.getDepartedAt().isPresent();
		}
		return false;
	}

	/**
	 * 到着済みかどうか調べる
	 */
	public Boolean isArrived() {
		for (OperationRecord operationRecord : getOperationRecord().asSet()) {
			return operationRecord.getArrivedAt().isPresent();
		}
		return false;
	}

	/**
	 * 現在運行中の運行スケジュールを得る
	 */
	static Optional<UnmergedOperationSchedule> getCurrent(
			Iterable<UnmergedOperationSchedule> operationSchedules) {
		return getRelative(operationSchedules, 0);
	}

	/**
	 * 現在運行中の運行スケジュールから、offset個進めた運行スケジュールを得る
	 */
	static Optional<UnmergedOperationSchedule> getRelative(
			Iterable<UnmergedOperationSchedule> operationSchedules, Integer offset) {
		Integer currentIndex = 0;
		for (UnmergedOperationSchedule operationSchedule : operationSchedules) {
			if (!operationSchedule.isDeparted()) {
				break;
			}
			currentIndex++;
		}
		Integer size = Iterables.size(operationSchedules);
		Integer resultIndex = currentIndex + offset;
		if (0 <= resultIndex && resultIndex < size) {
			return Optional.of(Iterables.get(operationSchedules, resultIndex));
		}
		return Optional.absent();
	}

	/**
	 * ReservationとUserをメンバに持つPassengerRecordを取得する
	 */
	public List<PassengerRecord> getPassengerRecordsWithReservationAndUser() {
		List<PassengerRecord> passengerRecords = Lists.newLinkedList();
		for (Reservation reservation : getDepartureReservation().asSet()) {
			for (PassengerRecord passengerRecord : reservation
					.getPassengerRecords()) {
				for (User user : reservation.getFellowUsers()) {
					if (!passengerRecord.getUserId().equals(
							Optional.of(user.getId()))) {
						continue;
					}
					passengerRecord.setUser(user);
					passengerRecord.setReservation(reservation);
					passengerRecords.add(passengerRecord);
					break;
				}
			}
		}
		return passengerRecords;
	}

	public OperationSchedule toOperationSchedule() {
		return OperationSchedule.create(this);
	}
}
