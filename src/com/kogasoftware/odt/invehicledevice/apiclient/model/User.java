package com.kogasoftware.odt.webapi.model;

import java.util.LinkedList;
import java.util.List;

import com.kogasoftware.odt.webapi.model.base.UserBase;

public class User extends UserBase {
	private static final long serialVersionUID = 8982613020419131854L;

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
