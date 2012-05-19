package com.kogasoftware.odt.invehicledevice.backgroundtask;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess;

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
			for (BackgroundTask backgroundTask : optionalBackgroundTask.asSet()) {
				backgroundTask.quit();
			}
			// この位置に、別スレッドでのoptionalBackgroundTaskメンバを代入している処理が挟まると
			// optionalBackgroundTaskが終了しなくなるため、注意してsynchronizedする
			super.interrupt();
		}
	}

	/**
	 * CommonLogicを作り、それを引数にしてBackgroundTaskを開始する. 直接は呼ばない.
	 */
	@Override
	public void run() {
		Looper.prepare();

		Handler activityHandler = new Handler();
		try {
			activityHandler = CommonLogic.getActivityHandler(activity);
		} catch (InterruptedException e) {
			return;
		}
		StatusAccess statusAccess = new StatusAccess(activity);
		CommonLogic commonLogic = new CommonLogic(activity, activityHandler,
				statusAccess);
		try {
			BackgroundTask backgroundTask = new BackgroundTask(commonLogic,
					activity, statusAccess);
			synchronized (backgroundTaskQuitLock) {
				optionalBackgroundTask = Optional.of(backgroundTask);
			}
			backgroundTask.loop();
		} finally {
			commonLogic.dispose();
		}
	}
}
