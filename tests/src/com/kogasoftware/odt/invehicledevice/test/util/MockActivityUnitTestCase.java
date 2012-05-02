package com.kogasoftware.odt.invehicledevice.test.util;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import android.app.Activity;
import android.app.Instrumentation;
import android.os.Handler;
import android.os.Looper;
import android.test.ActivityUnitTestCase;

import com.google.common.base.Preconditions;

public class MockActivityUnitTestCase extends
		ActivityUnitTestCase<MockActivity> {

	static class QuitLooperWorkaroundException extends RuntimeException {
		private static final long serialVersionUID = 6589002041806964185L;
	}

	public static Handler getActivityHandler(Activity activity)
			throws InterruptedException {
		final CountDownLatch latch = new CountDownLatch(1);
		final AtomicReference<Handler> handler = new AtomicReference<Handler>();
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				handler.set(new Handler());
				latch.countDown();
			}
		});
		latch.await();
		Preconditions.checkNotNull(handler.get());
		return handler.get();
	}

	MockActivity a;

	Instrumentation i;

	Looper myLooper;

	public MockActivityUnitTestCase() {
		super(MockActivity.class);
	}

	/**
	 * Activityに関連付くたHandlerを取得
	 */
	protected Handler getActivityHandler() throws InterruptedException {
		return getActivityHandler(getActivity());
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
