package com.kogasoftware.odt.invehicledevice.apiclient.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.apiclient.model.User;

import junit.framework.TestCase;

public class UserTestCase extends TestCase {
	public void testGetMemo() {
		User u = new User();
		assertTrue(u.getNotes().isEmpty());
		
		u.setHandicapped(true);
		u.setWheelchair(false);
		u.setNeededCare(false);
		u.setMemo("");
		assertEquals(1, u.getNotes().size());
		
		u.setHandicapped(false);
		u.setWheelchair(true);
		u.setNeededCare(false);
		u.setMemo("");
		assertEquals(1, u.getNotes().size());
		
		u.setHandicapped(false);
		u.setWheelchair(false);
		u.setNeededCare(true);
		u.setMemo("");
		assertEquals(1, u.getNotes().size());
		
		u.setHandicapped(false);
		u.setWheelchair(false);
		u.setNeededCare(false);
		u.setMemo("Hello");
		assertEquals(1, u.getNotes().size());
		
		u.setHandicapped(true);
		u.setWheelchair(true);
		u.setNeededCare(false);
		u.setMemo("");
		assertEquals(2, u.getNotes().size());
		
		u.setHandicapped(true);
		u.setWheelchair(false);
		u.setNeededCare(false);
		u.setMemo("World");
		assertEquals(2, u.getNotes().size());
		
		u.setHandicapped(false);
		u.setWheelchair(true);
		u.setNeededCare(false);
		u.setMemo("Hello");
		assertEquals(2, u.getNotes().size());
		
		u.setHandicapped(false);
		u.setWheelchair(false);
		u.setNeededCare(true);
		u.setMemo("Hello");
		assertEquals(2, u.getNotes().size());
		
		u.setHandicapped(true);
		u.setWheelchair(true);
		u.setNeededCare(true);
		u.setMemo("Hello");
		assertEquals(4, u.getNotes().size());
	}
	
	public void testGetAge() throws Exception {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		User u = new User();
		
		u.setBirthday(dateFormat.parse("1999-01-01"));
		assertEquals(Optional.of(0), u.getAge(dateFormat.parse("1999-01-01")));
		assertEquals(Optional.of(0), u.getAge(dateFormat.parse("1999-12-31")));
		assertEquals(Optional.of(1), u.getAge(dateFormat.parse("2000-01-01")));
		assertEquals(Optional.of(1), u.getAge(dateFormat.parse("2000-12-31")));
		assertEquals(Optional.of(2), u.getAge(dateFormat.parse("2001-01-01")));
		
		u.setBirthday(dateFormat.parse("2099-01-01"));
		assertEquals(Optional.of(0), u.getAge(dateFormat.parse("2099-01-01")));
		assertEquals(Optional.of(0), u.getAge(dateFormat.parse("2099-12-31")));
		assertEquals(Optional.of(1), u.getAge(dateFormat.parse("2100-01-01")));
		assertEquals(Optional.of(1), u.getAge(dateFormat.parse("2100-12-31")));
		assertEquals(Optional.of(2), u.getAge(dateFormat.parse("2101-01-01")));
		
		u.setBirthday(dateFormat.parse("2000-02-29"));
		assertEquals(Optional.of(8), u.getAge(dateFormat.parse("2009-02-28")));
		assertEquals(Optional.of(9), u.getAge(dateFormat.parse("2009-03-01")));
	}
}
