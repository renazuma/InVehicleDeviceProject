package com.kogasoftware.odt.webapi.model;

public class VehicleNotifications {
	public static class NotificationKind {
		public static final Integer FROM_OPERATOR = 0;
		public static final Integer RESERVATION_CHANGED = 1;
	}

	public static class Response {
		public static final Integer YES = 0;
		public static final Integer NO = 1;
	}
}
