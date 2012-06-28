package com.kogasoftware.odt.webapi.model;

import java.util.LinkedList;
import java.util.List;

public class Users {
	public static List<String> getMemo(User user) {
		List<String> memos = new LinkedList<String>();
		for (String memo : user.getMemo().asSet()) {
			if (memo.length() > 0) {
				memos.add(memo);
			}
		}
		if (user.getHandicapped().or(false)) {
			memos.add("※身体障害者");
		}
		if (user.getWheelchair().or(false)) {
			memos.add("※要車椅子");
		}
		if (user.getNeededCare().or(false)) {
			memos.add("※要介護");
		}
		return memos;
	}
	
	public static List<String> getMemo(Reservation reservation) {
		if (reservation.getUser().isPresent()) {
			return getMemo(reservation.getUser().get());
		}
		return new LinkedList<String>();
	}
}

