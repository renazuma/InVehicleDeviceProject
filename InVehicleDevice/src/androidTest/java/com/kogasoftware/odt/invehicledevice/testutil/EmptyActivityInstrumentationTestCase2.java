package com.kogasoftware.odt.invehicledevice.testutil;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import android.app.Instrumentation;
import android.os.Handler;
import android.test.ActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.ui.activity.EmptyActivity;
import com.robotium.solo.Solo;

public class EmptyActivityInstrumentationTestCase2 extends
		ActivityInstrumentationTestCase2<EmptyActivity> {

	public Solo solo;
	public EmptyActivity a;

	public EmptyActivityInstrumentationTestCase2() {
		super(EmptyActivity.class);
	}

	public Handler getActivityHandler() throws InterruptedException {
		// 一定時間取れなかったらInterruptする
		final Thread t = Thread.currentThread();
		final CountDownLatch cdl = new CountDownLatch(1);
		(new Thread() {
			@Override
			public void run() {
				try {
					if (cdl.await(10, TimeUnit.SECONDS)) {
						return;
					}
				} catch (InterruptedException e) {
				}
				t.interrupt();
			}
		}).start();

		Handler h = InVehicleDeviceService.getActivityHandler(a);
		cdl.countDown();
		return h;
	}

	public void runOnUiThreadSync(Runnable runnable, Integer timeoutSeconds)
			throws InterruptedException {
		TestUtil.runOnUiThreadSync(a, runnable, timeoutSeconds);
	}

	public void runOnUiThreadSync(Runnable runnable)
			throws InterruptedException {
		runOnUiThreadSync(runnable, 20);
	}

	@Override
	protected void setUp() throws Exception {
		EmptyActivity.USE_SAVED_INSTANCE_STATE.set(false);
		super.setUp();
		Instrumentation i = getInstrumentation();
		a = getActivity();
		solo = new Solo(i, a);
	}

	@Override
	protected void tearDown() throws Exception {
		try {
			if (solo != null) {
				solo.finishOpenedActivities();
			}
		} finally {
			try {
				super.tearDown();
			} finally {
				EmptyActivity.USE_SAVED_INSTANCE_STATE.set(true);
			}
		}
	}
}
