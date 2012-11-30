package com.kogasoftware.odt.invehicledevice.apiclient.model;

import java.util.Comparator;

import android.util.Log;

import com.google.common.base.Optional;
import com.google.common.collect.ComparisonChain;
import com.kogasoftware.odt.invehicledevice.apiclient.model.base.PassengerRecordBase;

public class PassengerRecord extends PassengerRecordBase {
	private static final long serialVersionUID = -7618961978174467119L;
	private static final String TAG = PassengerRecord.class.getSimpleName();
	public static final Comparator<PassengerRecord> DEFAULT_COMPARATOR = new Comparator<PassengerRecord>() {
		@Override
		public int compare(PassengerRecord r, PassengerRecord l) {
			for (User rUser : r.getUser().asSet()) {
				for (User lUser : l.getUser().asSet()) {
					Integer result = rUser.getTypeOfUser().compareTo(
							lUser.getTypeOfUser());
					if (!result.equals(0)) {
						return result;
					}
				}
			}
			return ComparisonChain
					.start()
					.compare(r.getReservationId().or(Integer.MAX_VALUE),
							l.getReservationId().or(Integer.MAX_VALUE))
					.compare(r.getDisplayName(), l.getDisplayName())
					.compare(r.getUserId().or(Integer.MAX_VALUE),
							l.getUserId().or(Integer.MAX_VALUE))
					.compare(r.getId(), l.getId()).result();
		}
	};

	// 乗車エラーを無視するかどうか
	private Boolean ignoreGetOnMiss = false;

	// 降車エラーを無視するかどうか
	private Boolean ignoreGetOffMiss = false;

	public Boolean getIgnoreGetOnMiss() {
		return wrapNull(ignoreGetOnMiss);
	}

	public void setIgnoreGetOnMiss(Boolean ignoreGetOnMiss) {
		this.ignoreGetOnMiss = wrapNull(ignoreGetOnMiss);
	}

	public Boolean getIgnoreGetOffMiss() {
		return wrapNull(ignoreGetOffMiss);
	}

	public void setIgnoreGetOffMiss(Boolean ignoreGetOffMiss) {
		this.ignoreGetOffMiss = wrapNull(ignoreGetOffMiss);
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
			for (Integer headUserId : reservation.getUserId().asSet()) {
				if (getUserId().equals(Optional.of(headUserId))) {
					return reservation.getPassengerCount()
							- reservation.getFellowUsers().size() + 1;
				}
			}
		}
		return 1;
	}

	/**
	 * 表示名を組み立てて出力
	 */
	public String getDisplayName() {
		for (User user : getUser().asSet()) {
			// 居住者の場合、姓名をつなげて帰す
			if (user.getTypeOfUser().equals(User.TypeOfUser.RESIDENT)) {
				return user.getLastName().or("") + " " + user.getFirstName().or("") + " 様";
			}
			// 居住者以外の場合、姓名以外の情報をつなげて識別可能にする
			StringBuilder displayName = new StringBuilder();
			if (getReservationId().isPresent()) {
				displayName.append(String.format("%04d", getReservationId()
						.get()));
			} else {
				Log.e(TAG, "No reservation_id found: " + this);
				displayName.append("    ");
			}
			if (user.getSex().equals(User.Sex.MALE)) {
				displayName.append(" 男性");
			} else {
				displayName.append(" 女性");
			}
			String ageString = "       ";
			for (Integer age : user.getAge().asSet()) {
				if (age < 10) {
					ageString = " お子様";
				} else if (age >= 100) {
					ageString = " ご高齢";
				} else {
					ageString = age / 10 * 10 + "代";
				}
			}
			displayName.append(ageString);
			if (user.getTelephoneNumber().length() > 0) {
				displayName.append("\n" + user.getTelephoneNumber());
			}
			return displayName.toString();
		}
		Log.e(TAG, "No User found: " + this);
		return "ID: " + getId();
	}
}
