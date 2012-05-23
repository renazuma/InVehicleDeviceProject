package com.kogasoftware.odt.webapi.test.model;

import java.util.Date;

import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.PassengerRecords;
import com.kogasoftware.odt.webapi.model.Reservation;

import junit.framework.TestCase;


public class PassengerRecordsTestCase extends TestCase {
	public void testConstructor() {
		// カバレッジを満たすためのコード.今のところコンストラクタを使うことは無い.
		new PassengerRecords();
	}
	
	public void test() {
		Reservation r = new Reservation();
		r.clearPassengerRecord();
		assertFalse(PassengerRecords.isUnhandled(r));
		assertFalse(PassengerRecords.isRiding(r));
		assertFalse(PassengerRecords.isGotOff(r));
		
		PassengerRecord pr = new PassengerRecord();
		r.setPassengerRecord(pr);

		pr.clearGetOnTime();
		pr.clearGetOffTime();
		assertTrue(PassengerRecords.isUnhandled(r));
		assertTrue(PassengerRecords.isUnhandled(pr));
		assertFalse(PassengerRecords.isRiding(r));
		assertFalse(PassengerRecords.isRiding(pr));
		assertFalse(PassengerRecords.isGotOff(r));
		assertFalse(PassengerRecords.isGotOff(pr));
		
		pr.setGetOnTime(new Date());
		pr.clearGetOffTime();
		assertFalse(PassengerRecords.isUnhandled(r));
		assertFalse(PassengerRecords.isUnhandled(pr));
		assertTrue(PassengerRecords.isRiding(r));
		assertTrue(PassengerRecords.isRiding(pr));
		assertFalse(PassengerRecords.isGotOff(r));
		assertFalse(PassengerRecords.isGotOff(pr));
		
		pr.setGetOnTime(new Date());
		pr.setGetOffTime(new Date());
		assertFalse(PassengerRecords.isUnhandled(r));
		assertFalse(PassengerRecords.isUnhandled(pr));
		assertFalse(PassengerRecords.isRiding(r));
		assertFalse(PassengerRecords.isRiding(pr));
		assertTrue(PassengerRecords.isGotOff(r));
		assertTrue(PassengerRecords.isGotOff(pr));
		
		// エラー状態
		pr.clearGetOnTime();
		pr.setGetOffTime(new Date());
		assertTrue(PassengerRecords.isUnhandled(r));
		assertTrue(PassengerRecords.isUnhandled(pr));
		assertFalse(PassengerRecords.isRiding(r));
		assertFalse(PassengerRecords.isRiding(pr));
		assertFalse(PassengerRecords.isGotOff(r));
		assertFalse(PassengerRecords.isGotOff(pr));
	}
}
