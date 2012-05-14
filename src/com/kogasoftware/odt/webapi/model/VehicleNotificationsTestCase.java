package com.kogasoftware.odt.webapi.model;

import junit.framework.TestCase;


public class VehicleNotificationsTestCase extends TestCase {
	public void testConstructor() {
		// カバレッジを満たすためのコード.今のところコンストラクタを使うことは無い.
		new VehicleNotifications();
		new VehicleNotifications.NotificationKind();
		new VehicleNotifications.Response();
	}
}
