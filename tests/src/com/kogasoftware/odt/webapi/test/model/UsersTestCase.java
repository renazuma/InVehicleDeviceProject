package com.kogasoftware.odt.webapi.test.model;

import junit.framework.TestCase;

import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.User;
import com.kogasoftware.odt.webapi.model.Users;

public class UsersTestCase extends TestCase {
	public void testConstructor() {
		// カバレッジを満たすためのコード.今のところコンストラクタを使うことは無い.
		new Users();
	}

	public void testGetMemo() {
		Reservation r = new Reservation();
		assertTrue(Users.getMemo(r).isEmpty());
		
		User u = new User();
		assertTrue(Users.getMemo(u).isEmpty());
		r.setUser(u);
		assertTrue(Users.getMemo(r).isEmpty());
		
		u.setHandicapped(true);
		u.setWheelchair(false);
		u.setNeededCare(false);
		u.clearMemo();
		assertEquals(1, Users.getMemo(r).size());
		assertEquals(1, Users.getMemo(u).size());
		
		u.setHandicapped(false);
		u.setWheelchair(true);
		u.setNeededCare(false);
		u.clearMemo();
		assertEquals(1, Users.getMemo(r).size());
		assertEquals(1, Users.getMemo(u).size());
		
		u.setHandicapped(false);
		u.setWheelchair(false);
		u.setNeededCare(true);
		u.clearMemo();
		assertEquals(1, Users.getMemo(r).size());
		assertEquals(1, Users.getMemo(u).size());
		
		u.setHandicapped(false);
		u.setWheelchair(false);
		u.setNeededCare(false);
		u.setMemo("Hello");
		assertEquals(1, Users.getMemo(r).size());
		assertEquals(1, Users.getMemo(u).size());
		
		u.setHandicapped(true);
		u.setWheelchair(true);
		u.setNeededCare(false);
		u.clearMemo();
		assertEquals(2, Users.getMemo(r).size());
		assertEquals(2, Users.getMemo(u).size());
		
		u.setHandicapped(true);
		u.setWheelchair(false);
		u.setNeededCare(false);
		u.setMemo("World");
		assertEquals(2, Users.getMemo(r).size());
		assertEquals(2, Users.getMemo(u).size());
		
		u.setHandicapped(false);
		u.setWheelchair(true);
		u.setNeededCare(false);
		u.setMemo("Hello");
		assertEquals(2, Users.getMemo(r).size());
		assertEquals(2, Users.getMemo(u).size());
		
		u.setHandicapped(false);
		u.setWheelchair(false);
		u.setNeededCare(true);
		u.setMemo("Hello");
		assertEquals(2, Users.getMemo(r).size());
		assertEquals(2, Users.getMemo(u).size());
		
		u.setHandicapped(true);
		u.setWheelchair(true);
		u.setNeededCare(true);
		u.setMemo("Hello");
		assertEquals(4, Users.getMemo(r).size());
		assertEquals(4, Users.getMemo(u).size());
	}
}
