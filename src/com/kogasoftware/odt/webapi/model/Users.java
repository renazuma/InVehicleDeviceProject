package com.kogasoftware.odt.webapi.model;

import java.util.LinkedList;
import java.util.List;

public class Users {
	public static List<String> getMemo(User user) {
		List<String> memo = new LinkedList<String>();
		if (user.getRememberMe().isPresent()) {
			memo.add(user.getRememberMe().get());
		}
		if (user.getHandicapped().or(false)) {
			memo.add("※要介護");
		}
		if (user.getWheelchair().or(false)) {
			memo.add("※要車椅子");
		}
		return memo;
	}
	
	public static List<String> getMemo(Reservation reservation) {
		if (reservation.getUser().isPresent()) {
			return getMemo(reservation.getUser().get());
		}
		return new LinkedList<String>();
	}
}

