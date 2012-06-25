package com.kogasoftware.odt.invehicledevice.test.util;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.test.ActivityUnitTestCase;

import com.google.common.base.Objects;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;

public class MockActivityUnitTestCase extends
		ActivityUnitTestCase<MockActivity> {

	static class QuitLooperWorkaroundException extends RuntimeException {
		private static final long serialVersionUID = 6589002041806964185L;
	}

	public static void runOnUiThreadSync(Activity activity,
			final Runnable runnable) throws InterruptedException {
		final CountDownLatch cdl = new CountDownLatch(1);
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					runnable.run();
				} finally {
					cdl.countDown();
				}
			}
		});
		if (!cdl.await(10, TimeUnit.SECONDS)) {
			throw new RuntimeException("runOnUiThreadSync Timeout!");
		}
	}

	MockActivity a;

	Instrumentation i;

	Looper myLooper;

	public MockActivityUnitTestCase() {
		super(MockActivity.class);
	}

	protected MockActivity getActivity2() {
		return Objects.firstNonNull(getActivity(),
				startActivity(new Intent(), null, null));
	}

	/**
	 * Activityに関連付くたHandlerを取得
	 */
	protected Handler getActivityHandler() throws InterruptedException {
		return InVehicleDeviceService.getActivityHandler(getActivity2());
	}

	protected void loop() {
		try {
			Looper.loop();
		} catch (QuitLooperWorkaroundException e) {
		}
	}

	protected void loop(Integer millis) {
		(new Handler(myLooper)).postDelayed(new Runnable() {
			@Override
			public void run() {
				throw new QuitLooperWorkaroundException();
			}
		}, millis);
		loop();
	}

	protected void quitLoop() {
		(new Handler(myLooper)).post(new Runnable() {
			@Override
			public void run() {
				throw new QuitLooperWorkaroundException();
			}
		});
	}

	public void runOnUiThreadSync(Runnable runnable)
			throws InterruptedException {
		runOnUiThreadSync(getActivity2(), runnable);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		if (Looper.myLooper() == null) {
			Looper.prepare();
		}
		myLooper = Looper.myLooper();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
