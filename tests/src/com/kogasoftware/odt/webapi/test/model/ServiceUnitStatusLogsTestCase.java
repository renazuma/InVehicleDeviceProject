package com.kogasoftware.odt.webapi.test.model;

import com.kogasoftware.odt.webapi.model.ServiceUnitStatusLogs;

import junit.framework.TestCase;


public class ServiceUnitStatusLogsTestCase extends TestCase {
	public void testConstructor() {
		// カバレッジを満たすためのコード.今のところコンストラクタを使うことは無い.
		new ServiceUnitStatusLogs();
		new ServiceUnitStatusLogs.Status();
	}
}
