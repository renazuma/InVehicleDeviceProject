package com.kogasoftware.odt.invehicledevice.logic;

import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;

public class LogicUser {
	private Optional<Logic> logic = Optional.<Logic> absent();
	private final Object logicLock = new Object();

	protected Optional<Logic> getLogic() {
		synchronized (logicLock) {
			return logic;
		}
	}

	@Subscribe
	public void setLogic(LogicLoadThread.CompleteEvent event) {
		synchronized (logicLock) {
			this.logic = Optional.of(event.logic);
		}
	}
}
