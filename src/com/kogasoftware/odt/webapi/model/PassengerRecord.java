package com.kogasoftware.odt.webapi.model;

import org.json.JSONException;

import android.util.Log;

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
	 * 未乗車かどうかを調べる エラー状態。降車済みとして扱うと現在の車載器のUIで操作が不可能なため、未乗車として扱う。
	 */
	public Boolean isUnhandled() {
		if (getGetOnTime().isPresent()) {
			return false;
		}
		if (!getGetOffTime().isPresent()) {
			return true;
		}
		String jsonString = "";
		try {
			jsonString = toJSONObject().toString();
		} catch (JSONException e) {
			Log.w(TAG, e);
		}
		Log.w(TAG,
				"PassengerRecord (!getGetOnTime().isPresent() && getGetOffTime().isPresent()) : "
						+ jsonString);
		return true;
	}
}
