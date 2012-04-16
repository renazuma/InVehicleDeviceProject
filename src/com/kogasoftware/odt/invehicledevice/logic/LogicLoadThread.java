package com.kogasoftware.odt.invehicledevice.logic;

import java.util.concurrent.CountDownLatch;

import android.app.Activity;

import com.google.common.base.Function;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class LogicLoadThread extends Thread {
	public static class CompleteEvent {
		public final Logic logic;

		public CompleteEvent(Logic logic) {
			this.logic = logic;
		}
	}

	private final Activity activity;

	private final CountDownLatch completeLatch = new CountDownLatch(1);

	public LogicLoadThread(Activity activity) {
		this.activity = activity;
	}

	@Override
	public void run() {
		Logic logic = null;
		try {
			logic = new Logic(activity);
			EventBus eventBus = logic.getEventBus();
			eventBus.register(new Function<CompleteEvent, Void>() {
				@Subscribe
				@Override
				public Void apply(CompleteEvent e) {
					completeLatch.countDown();
					return null;
				}
			});
			eventBus.post(new CompleteEvent(logic));
			completeLatch.await();
		} catch (InterruptedException e) {
			if (logic != null) {
				logic.shutdown();
			}
		}
	}
}
