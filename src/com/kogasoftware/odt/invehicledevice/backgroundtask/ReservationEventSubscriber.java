package com.kogasoftware.odt.invehicledevice.backgroundtask;

import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess;
import com.kogasoftware.odt.invehicledevice.logic.event.UiEventBus;

/**
 * 予約に関する内部データ処理
 */
@UiEventBus.HighPriority
public class ReservationEventSubscriber {
	private static final String TAG = ReservationEventSubscriber.class
			.getSimpleName();

	private final CommonLogic commonLogic;
	private final StatusAccess statusAccess;

	public ReservationEventSubscriber(CommonLogic commonLogic,
			StatusAccess statusAccess) {
		this.commonLogic = commonLogic;
		this.statusAccess = statusAccess;
	}
}
