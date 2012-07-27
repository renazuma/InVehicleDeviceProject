package com.kogasoftware.odt.webapi.model;

import com.kogasoftware.odt.webapi.model.base.VehicleNotificationBase;

public class VehicleNotification extends VehicleNotificationBase {
	private static final long serialVersionUID = 2101852795125634906L;

	public static class NotificationKind {
		public static final Integer FROM_OPERATOR = 0;
		public static final Integer RESERVATION_CHANGED = 1;
	}

	public static class Response {
		public static final Integer YES = 0;
		public static final Integer NO = 1;
	}
}
