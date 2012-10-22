package com.kogasoftware.odt.webapi.model;

import com.kogasoftware.odt.webapi.model.base.ServiceUnitStatusLogBase;

public class ServiceUnitStatusLog extends ServiceUnitStatusLogBase {
	private static final long serialVersionUID = 7080599901211750798L;

	public static class Status {
		public static final Integer OPERATION = 1;
		public static final Integer PAUSE = 2;
		public static final Integer STOP = 3;
	}
}
