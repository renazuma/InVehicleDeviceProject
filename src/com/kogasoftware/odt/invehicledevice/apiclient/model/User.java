package com.kogasoftware.odt.invehicledevice.apiclient.model;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.kogasoftware.odt.invehicledevice.apiclient.model.base.UserBase;

public class User extends UserBase {
	private static final long serialVersionUID = 8982613020419131854L;

	public static class Sex {
		public static final Integer MALE = 1; // 男性
		public static final Integer FEMALE = 2; // 女性
	}

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

	public Integer getAge(Date base) {
		Calendar now = Calendar.getInstance();
		now.setTime(base);
		Calendar birthday = Calendar.getInstance();
		birthday.setTime(getBirthday());
		if (birthday.get(Calendar.MONTH) < now.get(Calendar.MONTH)
				|| (birthday.get(Calendar.MONTH) == now.get(Calendar.MONTH) && birthday
						.get(Calendar.DAY_OF_MONTH) <= now
						.get(Calendar.DAY_OF_MONTH))) {
			return now.get(Calendar.YEAR) - birthday.get(Calendar.YEAR);
		} else {
			return now.get(Calendar.YEAR) - birthday.get(Calendar.YEAR) - 1;
		}
	}

	public Integer getAge() {
		return getAge(new Date());
	}
}
