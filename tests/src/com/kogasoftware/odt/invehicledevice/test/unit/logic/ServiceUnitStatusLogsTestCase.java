package com.kogasoftware.odt.invehicledevice.test.unit.logic;

import junit.framework.TestCase;

import com.kogasoftware.odt.invehicledevice.logic.ServiceUnitStatusLogs;

public class ServiceUnitStatusLogsTestCase extends TestCase {
	public void testConstructor() {
		// カバレッジを満たすためのコード.今のところコンストラクタを使うことは無い.
		new ServiceUnitStatusLogs();
		new ServiceUnitStatusLogs.Status();
	}
}
