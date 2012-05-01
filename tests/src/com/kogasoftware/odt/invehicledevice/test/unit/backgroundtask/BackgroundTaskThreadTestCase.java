package com.kogasoftware.odt.invehicledevice.test.unit.backgroundtask;

import android.os.Handler;

import com.kogasoftware.odt.invehicledevice.backgroundtask.BackgroundTaskThread;
import com.kogasoftware.odt.invehicledevice.test.common.MockActivityUnitTestCase;

public class BackgroundTaskThreadTestCase extends MockActivityUnitTestCase {

	/**
	 * interruptで確実に終了させることができるか確認
	 */
	public void testInterrupt() throws Exception {
		final Handler h = getActivityHandler();

		for (int i = 0; i < 2000; i += 200) {
			BackgroundTaskThread result = new BackgroundTaskThread(
					getActivity(), h);
			result.start();
			Thread.sleep(i);
			result.interrupt();
			result.join(10000);
		}
	}

	public void testRun() {
		fail("stub !");
	}
}
