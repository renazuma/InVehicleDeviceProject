package com.kogasoftware.odt.webapi.test.model;

import com.kogasoftware.odt.webapi.model.PassengerRecords;

import junit.framework.TestCase;


public class PassengerRecordsTestCase extends TestCase {
	public void testConstructor() {
		// カバレッジを満たすためのコード.今のところコンストラクタを使うことは無い.
		new PassengerRecords();
		new PassengerRecords.Status();
	}
}
