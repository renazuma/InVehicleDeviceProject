package com.kogasoftware.odt.invehicledevice.apiclient.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTimeUtils;

import android.util.Log;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class OperationSchedule implements Serializable {
	private static final long serialVersionUID = 113833565898783155L;
	private static final String TAG = OperationSchedule.class.getSimpleName();
	private final List<UnmergedOperationSchedule> sourceOperationSchedules = Lists
			.newLinkedList();

	/**
	 * 出発済みか調べる
	 */
	public Boolean isDeparted() {
		for (UnmergedOperationSchedule sourceOperationSchedule : sourceOperationSchedules) {
			if (!sourceOperationSchedule.isDeparted()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 到着済みかどうか調べる
	 */
	public Boolean isArrived() {
		for (UnmergedOperationSchedule sourceOperationSchedule : sourceOperationSchedules) {
			if (!sourceOperationSchedule.isArrived()) {
				return false;
			}
		}
		return true;
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
			for (Integer id : reservation.getDepartureScheduleId().asSet()) {
				for (UnmergedOperationSchedule sourceOperationSchedule : sourceOperationSchedules) {
					if (sourceOperationSchedule.getId().equals(id)) {
						return true;
					}
				}
				return false;
			}
		}
		Log.e(TAG,
				"PassengerRecord has no Reservation or no Reservation.departureScheduleId "
						+ passengerRecord);
		return false;
	}

	/**
	 * 降車予定かどうかを調べる
	 */
	public Boolean isGetOffScheduled(PassengerRecord passengerRecord) {
		for (Reservation reservation : passengerRecord.getReservation().asSet()) {
			for (Integer id : reservation.getArrivalScheduleId().asSet()) {
				for (UnmergedOperationSchedule sourceOperationSchedule : sourceOperationSchedules) {
					if (sourceOperationSchedule.getId().equals(id)) {
						return true;
					}
				}
				return false;
			}
		}
		Log.e(TAG,
				"PassengerRecord has no Reservation or no Reservation.arrivalScheduleId "
						+ passengerRecord);
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

	/**
	 * ReservationとUserをメンバに持つPassengerRecordを取得する
	 */
	public List<PassengerRecord> getPassengerRecordsWithReservationAndUser() {
		List<PassengerRecord> passengerRecords = Lists.newLinkedList();
		for (UnmergedOperationSchedule sourceOperationSchedule : sourceOperationSchedules) {
			for (Reservation reservation : sourceOperationSchedule
					.getDepartureReservation().asSet()) {
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
		}
		return passengerRecords;
	}

	public Optional<Platform> getPlatform() {
		for (UnmergedOperationSchedule sourceOperationSchedule : sourceOperationSchedules) {
			for (Platform platform : sourceOperationSchedule.getPlatform()
					.asSet()) {
				return Optional.of(platform);
			}
		}
		Log.e(TAG, "Merged OperationSchedule must have platform");
		return Optional.absent();
	}

	public List<Integer> getIds() {
		List<Integer> ids = Lists.newLinkedList();
		for (UnmergedOperationSchedule sourceOperationSchedule : sourceOperationSchedules) {
			ids.add(sourceOperationSchedule.getId());
		}
		return ids;
	}

	public String dumpIds() {
		return "{" + Joiner.on(",").join(getIds()) + "}";
	}

	public Optional<Date> getArrivalEstimate() {
		for (UnmergedOperationSchedule sourceOperationSchedule : sourceOperationSchedules) {
			for (Date arrivalEstimate : sourceOperationSchedule
					.getArrivalEstimate().asSet()) {
				return Optional.of(arrivalEstimate);
			}
		}
		return Optional.absent();
	}

	public Optional<Date> getDepartureEstimate() {
		for (UnmergedOperationSchedule sourceOperationSchedule : sourceOperationSchedules) {
			for (Date departureEstimate : sourceOperationSchedule
					.getDepartureEstimate().asSet()) {
				return Optional.of(departureEstimate);
			}
		}
		return Optional.absent();
	}

	public void clearArrivalReservation() {
		for (UnmergedOperationSchedule sourceOperationSchedule : sourceOperationSchedules) {
			sourceOperationSchedule.clearArrivalReservation();
		}
	}

	public void clearDepartureReservation() {
		for (UnmergedOperationSchedule sourceOperationSchedule : sourceOperationSchedules) {
			sourceOperationSchedule.clearDepartureReservation();
		}
	}

	public void arrive() {
		long currentTimeMillis = DateTimeUtils.currentTimeMillis();
		for (UnmergedOperationSchedule sourceOperationSchedule : sourceOperationSchedules) {
			OperationRecord operationRecord = sourceOperationSchedule
					.getOperationRecord().or(new OperationRecord());
			operationRecord.setArrivedAt(new Date(currentTimeMillis));
			sourceOperationSchedule.setOperationRecord(operationRecord);
		}
	}

	public void depart() {
		long currentTimeMillis = DateTimeUtils.currentTimeMillis();
		for (UnmergedOperationSchedule sourceOperationSchedule : sourceOperationSchedules) {
			OperationRecord operationRecord = sourceOperationSchedule
					.getOperationRecord().or(new OperationRecord());
			operationRecord.setDepartedAt(new Date(currentTimeMillis));
			sourceOperationSchedule.setOperationRecord(operationRecord);
		}
	}

	public void cancelArrive() {
		for (UnmergedOperationSchedule sourceOperationSchedule : sourceOperationSchedules) {
			OperationRecord operationRecord = sourceOperationSchedule
					.getOperationRecord().or(new OperationRecord());
			operationRecord.clearArrivedAt();
			operationRecord.clearArrivedAtOffline();
			operationRecord.clearDepartedAt();
			operationRecord.clearDepartedAtOffline();
			sourceOperationSchedule.setOperationRecord(operationRecord);
		}
	}

	public List<UnmergedOperationSchedule> getSourceOperationSchedules() {
		return Lists.newArrayList(sourceOperationSchedules);
	}

	public static List<OperationSchedule> create(
			List<UnmergedOperationSchedule> sourceOperationSchedules) {
		List<OperationSchedule> result = Lists.newLinkedList();
		Optional<Integer> platformId = Optional.absent();
		OperationSchedule operationSchedule = new OperationSchedule();
		for (UnmergedOperationSchedule sourceOperationSchedule : sourceOperationSchedules) {
			Platform platform = sourceOperationSchedule.getPlatform().get(); // platformがない場合、IllegalStateExceptionを投げる
			if (!platformId.equals(Optional.of(platform.getId())) && !operationSchedule.sourceOperationSchedules.isEmpty()) {
				result.add(operationSchedule);
				operationSchedule = new OperationSchedule();
			}
			operationSchedule.sourceOperationSchedules.add(sourceOperationSchedule);
			platformId = Optional.of(platform.getId());
		}
		if (!operationSchedule.getSourceOperationSchedules().isEmpty()) {
			result.add(operationSchedule);
		}
		return result;
	}

	public static OperationSchedule create(UnmergedOperationSchedule sourceOperationSchedule) {
		OperationSchedule result = new OperationSchedule();
		result.sourceOperationSchedules.add(sourceOperationSchedule);
		return result;
	}
}
