package com.kogasoftware.odt.invehicledevice.test.common;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import android.app.Instrumentation;
import android.os.Handler;
import android.os.Looper;
import android.test.ActivityUnitTestCase;

import com.google.common.base.Preconditions;

public class MockActivityUnitTestCase extends
		ActivityUnitTestCase<MockActivity> {

	static class QuitLooperWorkaroundException extends RuntimeException {
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
		final CountDownLatch latch = new CountDownLatch(1);
		final AtomicReference<Handler> handler = new AtomicReference<Handler>();
		getActivity().runOnUiThread(new Runnable() {
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
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
