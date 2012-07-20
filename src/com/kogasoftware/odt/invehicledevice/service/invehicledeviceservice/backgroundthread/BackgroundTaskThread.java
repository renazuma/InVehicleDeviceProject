package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.backgroundthread;

import android.os.Looper;

import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;

public class BackgroundTaskThread extends Thread {
	private final InVehicleDeviceService service;
	private final Object backgroundTaskQuitLock = new Object();
	private volatile Optional<BackgroundTask> optionalBackgroundTask = Optional
			.absent();

	public BackgroundTaskThread(InVehicleDeviceService service) {
		this.service = service;
	}

	@Override
	public void interrupt() {
		synchronized (backgroundTaskQuitLock) {
			for (BackgroundTask backgroundTask : optionalBackgroundTask.asSet()) {
				backgroundTask.quit();
			}
			// この位置に、別スレッドでのoptionalBackgroundTaskメンバを代入している処理が挟まると
			// optionalBackgroundTaskが終了しなくなるため、注意してsynchronizedする
			super.interrupt();
		}
	}

	/**
	 * BackgroundTaskを開始する.
	 */
	@Override
	public void run() {
		Looper.prepare();
		BackgroundTask backgroundTask = new BackgroundTask(service);
		try {
			synchronized (backgroundTaskQuitLock) {
				optionalBackgroundTask = Optional.of(backgroundTask);
			}
			backgroundTask.loop();
		} finally {
			synchronized (backgroundTaskQuitLock) {
				optionalBackgroundTask = Optional.absent();
			}
		}
	}
}
