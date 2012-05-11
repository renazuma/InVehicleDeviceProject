package com.kogasoftware.odt.invehicledevice.test.unit.logic;

import junit.framework.TestCase;

import com.kogasoftware.odt.invehicledevice.logic.VehicleNotifications;

public class VehicleNotificationsTestCase extends TestCase {
	public void testConstructor() {
		// カバレッジを満たすためのコード.今のところコンストラクタを使うことは無い.
		new VehicleNotifications();
		new VehicleNotifications.NotificationKind();
		new VehicleNotifications.Response();
	}
}
