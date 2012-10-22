package com.kogasoftware.odt.invehicledevice.apiclient.test.model;

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
}
