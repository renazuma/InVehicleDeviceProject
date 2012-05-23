package com.kogasoftware.odt.webapi.model;

import org.json.JSONException;

import android.util.Log;

public class PassengerRecords {
	private static final String TAG = PassengerRecords.class.getSimpleName();

	/**
	 * 降車済みかどうかを調べる
	 */
	public static Boolean isGotOff(Reservation reservation) {
		return reservation.getPassengerRecord().isPresent()
				&& isGotOff(reservation.getPassengerRecord().get());
	}

	public static Boolean isGotOff(PassengerRecord passengerRecord) {
		return passengerRecord.getGetOnTime().isPresent()
				&& passengerRecord.getGetOffTime().isPresent();
	}

	/**
	 * 乗車中かどうかを調べる
	 */
	public static Boolean isRiding(Reservation reservation) {
		return reservation.getPassengerRecord().isPresent()
				&& isRiding(reservation.getPassengerRecord().get());
	}

	public static Boolean isRiding(PassengerRecord passengerRecord) {
		return passengerRecord.getGetOnTime().isPresent()
				&& !passengerRecord.getGetOffTime().isPresent();
	}

	/**
	 * 未乗車かどうかを調べる エラー状態。降車済みとして扱うと現在の車載器のUIで操作が不可能なため、未乗車として扱う。
	 */
	public static Boolean isUnhandled(Reservation reservation) {
		return reservation.getPassengerRecord().isPresent()
				&& isUnhandled(reservation.getPassengerRecord().get());
	}

	public static Boolean isUnhandled(PassengerRecord passengerRecord) {
		if (passengerRecord.getGetOnTime().isPresent()) {
			return false;
		}
		if (!passengerRecord.getGetOffTime().isPresent()) {
			return true;
		}
		String jsonString = "";
		try {
			jsonString = passengerRecord.toJSONObject().toString();
		} catch (JSONException e) {
			Log.w(TAG, e);
		}
		Log.w(TAG,
				"PassengerRecord (!getGetOnTime().isPresent() && getGetOffTime().isPresent()) : "
						+ jsonString);
		return true;
	}
}
