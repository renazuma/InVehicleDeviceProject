package com.kogasoftware.odt.invehicledevice.backgroundtask;

import android.app.Activity;

import com.google.common.base.Optional;

public class BackgroundTaskThread extends Thread {
	private final Activity activity;
	private final Object backgroundTaskQuitLock = new Object();
	private volatile Optional<BackgroundTask> optionalBackgroundTask = Optional
			.absent();

	public BackgroundTaskThread(Activity activity) {
		this.activity = activity;
	}

	@Override
	public void interrupt() {
		synchronized (backgroundTaskQuitLock) {
			if (optionalBackgroundTask.isPresent()) {
				optionalBackgroundTask.get().quit();
			}
			// この位置に、別スレッドでのoptionalBackgroundTaskメンバを代入している処理が挟まると
			// optionalBackgroundTaskが終了しなくなるため、注意してsynchronizedする
			super.interrupt();
		}
	}

	@Override
	public void run() {
		BackgroundTask backgroundTask = new BackgroundTask(activity);
		synchronized (backgroundTaskQuitLock) {
			optionalBackgroundTask = Optional.of(backgroundTask);
		}
		backgroundTask.loop();
	}
}
