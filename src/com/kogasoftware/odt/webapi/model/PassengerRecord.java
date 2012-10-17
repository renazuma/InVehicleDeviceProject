package com.kogasoftware.odt.webapi.model;

import com.kogasoftware.odt.webapi.model.base.PassengerRecordBase;

public class PassengerRecord extends PassengerRecordBase {
	private static final long serialVersionUID = -7618961978174467119L;

	// 乗車降車エラーを無視するかどうか
	private Boolean ignoreMiss = false;

	public Boolean getIgnoreMiss() {
		return wrapNull(ignoreMiss);
	}

	public void setIgnoreMiss(Boolean ignoreMiss) {
		this.ignoreMiss = wrapNull(ignoreMiss);
	}

	/**
	 * 降車済みかどうかを調べる
	 */
	public Boolean isGotOff() {
		return getGetOnTime().isPresent() && getGetOffTime().isPresent();
	}

	/**
	 * 乗車中かどうかを調べる
	 */
	public Boolean isRiding() {
		return getGetOnTime().isPresent() && !getGetOffTime().isPresent();
	}

	/**
	 * 未乗車かどうかを調べる。
	 */
	public Boolean isUnhandled() {
		return !getGetOnTime().isPresent();
	}

	/**
	 * 乗車予定人数を調べる
	 */
	public Integer getScheduledPassengerCount() {
		for (Reservation reservation : getReservation().asSet()) {
			for (User headUser : reservation.getUser().asSet()) {
				for (User user : getUser().asSet()) {
					if (user.getId().equals(headUser.getId())) {
						return reservation.getPassengerCount()
								- reservation.getFellowUsers().size() + 1;
					}
				}
			}
		}
		return 1;
	}
}
