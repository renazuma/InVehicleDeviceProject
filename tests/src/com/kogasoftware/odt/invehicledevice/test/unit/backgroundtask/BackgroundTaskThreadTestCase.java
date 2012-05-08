package com.kogasoftware.odt.invehicledevice.test.unit.backgroundtask;

import com.kogasoftware.odt.invehicledevice.backgroundtask.BackgroundTaskThread;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;

public class BackgroundTaskThreadTestCase extends
		EmptyActivityInstrumentationTestCase2 {
	
	public void setUp() throws Exception {
		super.setUp();
		Thread.sleep(10 * 1000);
	}

	/**
	 * interruptで確実に終了させることができるか確認
	 */
	public void testInterrupt() throws Exception {
		for (int i = 0; i < 2000; i += 100) {
			BackgroundTaskThread t = new BackgroundTaskThread(getActivity());
			t.start();
			Thread.sleep(i);
			t.interrupt();
			t.join(10000);
			assertFalse(t.isAlive());
		}
	}

	/**
	 * runは直接呼ばない！
	 */
	public void xtestRun() throws Exception {
		fail("stub !");
	}
}
