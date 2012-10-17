package com.kogasoftware.odt.webapi.model;

import com.kogasoftware.odt.webapi.model.base.PassengerRecordBase;

public class PassengerRecord extends PassengerRecordBase {
	private static final long serialVersionUID = -7618961978174467119L;

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
}
