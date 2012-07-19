package com.kogasoftware.odt.webapi.model;

import java.util.LinkedList;
import java.util.List;

import com.kogasoftware.odt.webapi.model.base.ReservationBase;

public class Reservation extends ReservationBase {
	private static final long serialVersionUID = 6058709332183625966L;
	
	public List<User> getUsers() {
		List<User> users = new LinkedList<User>();
		for (ReservationUser reservationUser : getReservationUsers()) {
			for (User user : reservationUser.getUser().asSet()) {
				users.add(user);
			}
		}
		return users;
	}
}
