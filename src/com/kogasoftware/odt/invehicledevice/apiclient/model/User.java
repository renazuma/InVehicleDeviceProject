package com.kogasoftware.odt.invehicledevice.apiclient.model;

import java.util.LinkedList;
import java.util.List;

import com.kogasoftware.odt.invehicledevice.apiclient.model.base.UserBase;

public class User extends UserBase {
	private static final long serialVersionUID = 8982613020419131854L;

	public static class TypeOfUser {
		public static final Integer RESIDENT = 0; // 在住者
		public static final Integer VISITOR = 1; // 県内
		public static final Integer TRAVELER = 2;// 県外
	}

	public List<String> getNotes() {
		List<String> notes = new LinkedList<String>();
		if (getMemo().length() > 0) {
			notes.add(getMemo());
		}
		if (getHandicapped().or(false)) {
			notes.add("※身体障害者");
		}
		if (getWheelchair().or(false)) {
			notes.add("※要車椅子");
		}
		if (getNeededCare().or(false)) {
			notes.add("※要介護");
		}
		return notes;
	}
}
