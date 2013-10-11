package com.kogasoftware.odt.invehicledevice.apiclient.model;

import java.util.Date;

import junit.framework.TestCase;

import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.apiclient.model.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Reservation;
import com.kogasoftware.odt.invehicledevice.apiclient.model.User;


public class PassengerRecordTestCase extends TestCase {
	public void testStatus() {
		PassengerRecord pr = new PassengerRecord();

		pr.clearGetOnTime();
		pr.clearGetOffTime();
		assertTrue(pr.isUnhandled());
		assertFalse(pr.isRiding());
		assertFalse(pr.isGotOff());

		pr.setGetOnTime(new Date());
		pr.clearGetOffTime();
		assertFalse(pr.isUnhandled());
		assertTrue(pr.isRiding());
		assertFalse(pr.isGotOff());

		pr.setGetOnTime(new Date());
		pr.setGetOffTime(new Date());
		assertFalse(pr.isUnhandled());
		assertFalse(pr.isRiding());
		assertTrue(pr.isGotOff());

		// エラー状態
		pr.clearGetOnTime();
		pr.setGetOffTime(new Date());
		assertTrue(pr.isUnhandled());
		assertFalse(pr.isRiding());
		assertFalse(pr.isGotOff());
	}

	public void testGetScheduledPassengerCount() {
		User headUser = new User();
		User fellowUser1 = new User();
		User fellowUser2 = new User();

		headUser.setId(10);
		fellowUser1.setId(11);
		fellowUser2.setId(12);

		Reservation reservation = new Reservation();
		reservation.setPassengerCount(10);
		reservation.setUserId(headUser.getId());
		reservation.setFellowUsers(Lists.newArrayList(headUser, fellowUser1,
				fellowUser2));

		PassengerRecord headPassengerRecord = new PassengerRecord();
		PassengerRecord fellowPassengerRecord1 = new PassengerRecord();
		PassengerRecord fellowPassengerRecord2 = new PassengerRecord();

		headPassengerRecord.setReservation(reservation);
		fellowPassengerRecord1.setReservation(reservation);
		fellowPassengerRecord2.setReservation(reservation);

		headPassengerRecord.setUser(headUser);
		fellowPassengerRecord1.setUser(fellowUser1);
		fellowPassengerRecord2.setUser(fellowUser2);

		assertEquals(8, headPassengerRecord.getScheduledPassengerCount()
				.intValue());
		assertEquals(1, fellowPassengerRecord1.getScheduledPassengerCount()
				.intValue());
		assertEquals(1, fellowPassengerRecord2.getScheduledPassengerCount()
				.intValue());
	}
	
	public void testRecursiveSerialization() {
		PassengerRecord pr = new PassengerRecord();
		pr.setId(12345);
		Reservation r = new Reservation();
		r.setId(54321);
		pr.setReservation(r);
		r.setPassengerRecords(Lists.newArrayList(pr));
		
		Reservation r2 = r.clone();
		assertEquals(r2, r2.getPassengerRecords().get(0).getReservation().get());
		Reservation r3 = r2.getPassengerRecords().get(0).getReservation().get();
		assertEquals(r3, r3.getPassengerRecords().get(0).getReservation().get());
	}
}
