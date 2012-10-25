package com.kogasoftware.odt.invehicledevice.apiclient.model;

import java.util.List;

import android.util.Log;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.apiclient.model.base.OperationScheduleBase;

public class OperationSchedule extends OperationScheduleBase {
	private static final long serialVersionUID = 1040628741311146499L;
	private static final String TAG = OperationSchedule.class.getSimpleName();

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
	public static Optional<OperationSchedule> getCurrent(
			Iterable<OperationSchedule> operationSchedules) {
		return getRelative(operationSchedules, 0);
	}

	/**
	 * 現在運行中の運行スケジュールから、offset個進めた運行スケジュールを得る
	 */
	public static Optional<OperationSchedule> getRelative(
			Iterable<OperationSchedule> operationSchedules, Integer offset) {
		Integer currentIndex = 0;
		for (OperationSchedule operationSchedule : operationSchedules) {
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
	 * 降車予定なのに未降車の乗車実績を得る
	 */
	public List<PassengerRecord> getNoGetOffErrorPassengerRecords(
			Iterable<PassengerRecord> passengerRecords) {
		List<PassengerRecord> results = Lists.newLinkedList();
		for (PassengerRecord passengerRecord : getGetOffScheduledPassengerRecords(passengerRecords)) {
			if (!passengerRecord.getGetOffTime().isPresent()) {
				results.add(passengerRecord);
			}
		}
		return results;
	}

	/**
	 * 乗車予定なのに未乗車の乗車実績を得る
	 */
	public List<PassengerRecord> getNoGetOnErrorPassengerRecords(
			Iterable<PassengerRecord> passengerRecords) {
		List<PassengerRecord> results = Lists.newLinkedList();
		for (PassengerRecord passengerRecord : getGetOnScheduledPassengerRecords(passengerRecords)) {
			if (!passengerRecord.getGetOnTime().isPresent()) {
				results.add(passengerRecord);
			}
		}
		return results;
	}

	/**
	 * 乗車予定かどうかを調べる
	 */
	public Boolean isGetOnScheduled(PassengerRecord passengerRecord) {
		for (Reservation reservation : passengerRecord.getReservation().asSet()) {
			return reservation.getDepartureScheduleId().equals(
					Optional.of(getId()));
		}
		Log.e(TAG, "PassengerRecord has no Reservation " + passengerRecord);
		return false;
	}

	/**
	 * 降車予定かどうかを調べる
	 */
	public Boolean isGetOffScheduled(PassengerRecord passengerRecord) {
		for (Reservation reservation : passengerRecord.getReservation().asSet()) {
			return reservation.getArrivalScheduleId().equals(
					Optional.of(getId()));
		}
		Log.e(TAG, "PassengerRecord has no Reservation " + passengerRecord);
		return false;
	}

	/**
	 * 乗車予定の乗車実績を得る
	 */
	public List<PassengerRecord> getGetOnScheduledPassengerRecords(
			Iterable<PassengerRecord> passengerRecords) {
		List<PassengerRecord> results = Lists.newLinkedList();
		for (PassengerRecord passengerRecord : passengerRecords) {
			if (isGetOnScheduled(passengerRecord)) {
				results.add(passengerRecord);
			}
		}
		return results;
	}

	/**
	 * 降車予定の乗車実績を得る
	 */
	public List<PassengerRecord> getGetOffScheduledPassengerRecords(
			Iterable<PassengerRecord> passengerRecords) {
		List<PassengerRecord> results = Lists.newLinkedList();
		for (PassengerRecord passengerRecord : passengerRecords) {
			if (isGetOffScheduled(passengerRecord)) {
				results.add(passengerRecord);
			}
		}
		return results;
	}
}
