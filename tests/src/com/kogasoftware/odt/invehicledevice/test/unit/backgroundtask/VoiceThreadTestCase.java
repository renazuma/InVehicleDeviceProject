package com.kogasoftware.odt.invehicledevice.test.unit.backgroundtask;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import com.kogasoftware.odt.invehicledevice.backgroundtask.BackgroundTask;
import com.kogasoftware.odt.invehicledevice.backgroundtask.VoiceThread;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;

public class VoiceThreadTestCase extends EmptyActivityInstrumentationTestCase2 {
	VoiceThread vt;
	CommonLogic cl;
	StatusAccess sa;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		vt = new VoiceThread(getInstrumentation().getTargetContext());
		sa = new StatusAccess(getActivity());
		cl = new CommonLogic(getActivity(), getActivityHandler(), sa);
	}

	@Override
	protected void tearDown() throws Exception {
		if (vt != null) {
			vt.interrupt();
		}
		if (cl != null) {
			cl.dispose();
		}
		super.tearDown();
	}

	public void testBackgroundTaskによってUiEventBusに自分が登録される() throws Exception {
		final CountDownLatch cdl = new CountDownLatch(1);
		final AtomicReference<BackgroundTask> bt = new AtomicReference<BackgroundTask>();
		Thread t = new Thread() {
			@Override
			public void run() {
				bt.set(new BackgroundTask(cl,
						getInstrumentation().getContext(), sa));
				bt.get().loop();
				cdl.countDown();
			}
		};
		t.start();
		Thread.sleep(1000);
		assertEquals(cl.countRegisteredClass(VoiceThread.class).intValue(), 1);
		bt.get().quit();
		assertTrue(cdl.await(10, TimeUnit.SECONDS));
	}

	public void xtestVoiceThread_1() throws Exception {
		fail("stub! / physical test required");
	}
}
