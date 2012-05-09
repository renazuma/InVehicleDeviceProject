package com.kogasoftware.odt.invehicledevice.logic.event;

/**
 * 飛び乗り予約が追加されたことを通知
 */
public class UnexpectedReservationAddedEvent {
	public final Integer arrivalOperationScheduleId;

	public UnexpectedReservationAddedEvent(Integer arrivalOperationScheduleId) {
		this.arrivalOperationScheduleId = arrivalOperationScheduleId;
	}
}
