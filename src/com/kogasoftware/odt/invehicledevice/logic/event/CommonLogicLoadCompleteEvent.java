package com.kogasoftware.odt.invehicledevice.logic.event;

import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;

public class CommonLogicLoadCompleteEvent {
	public final CommonLogic commonLogic;

	public CommonLogicLoadCompleteEvent(CommonLogic commonLogic) {
		this.commonLogic = commonLogic;
	}
}
