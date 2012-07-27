package com.kogasoftware.odt.webapi.test.model;

import java.util.Date;

import com.kogasoftware.odt.webapi.model.PassengerRecord;

import junit.framework.TestCase;

public class PassengerRecordTestCase extends TestCase {
	public void test() {
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
}
