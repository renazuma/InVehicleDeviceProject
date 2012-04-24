package com.kogasoftware.odt.invehicledevice.event;

import com.kogasoftware.odt.invehicledevice.CommonLogic;

public class CommonLogicLoadCompleteEvent {
	public final CommonLogic commonLogic;

	public CommonLogicLoadCompleteEvent(CommonLogic commonLogic) {
		this.commonLogic = commonLogic;
	}
}
