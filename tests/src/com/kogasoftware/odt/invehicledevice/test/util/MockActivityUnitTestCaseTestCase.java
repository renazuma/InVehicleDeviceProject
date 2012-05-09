package com.kogasoftware.odt.invehicledevice.test.util;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import android.os.Handler;
import android.os.Looper;
import android.widget.Button;

public class MockActivityUnitTestCaseTestCase extends MockActivityUnitTestCase {
	@Override
	public void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * ActivityのスレッドのHandlerを取得
	 */
	public void testGetActivityHandler() throws InterruptedException {
		final Handler handler = getActivityHandler();

		// ActivityのスレッドIDと一致しているか
		final CountDownLatch cdl1 = new CountDownLatch(1);
		final AtomicLong activityThreadId = new AtomicLong(-1);
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				activityThreadId.set(Thread.currentThread().getId());
				cdl1.countDown();
			}
		});
		assertTrue(cdl1.await(20, TimeUnit.SECONDS));
		assertEquals(activityThreadId.get(), handler.getLooper().getThread()
				.getId());

		// Handler上でUIを触っても大丈夫か
		new Thread() { // テストスレッドはUIスレッドにアクセス可能なため、さらに別のスレッドを立てる
			@Override
			public void run() {
				handler.post(new Runnable() {
					@Override
					public void run() {
						Button b = new Button(getActivity());
						getActivity().setContentView(b);
						b.performClick();
						b.setText("foo");
						quitLoop();
					}
				});
			}
		}.start();

		loop();
	}

	/**
	 * quitメソッドを実行すると終了する
	 */
	public void testLoop_1() throws InterruptedException {
		final AtomicInteger state = new AtomicInteger(0);
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(500);
					state.compareAndSet(0, 1);
					Thread.sleep(500);
					quitLoop();
				} catch (InterruptedException e) {
				}
			}
		};
		thread.start();
		loop();
		state.compareAndSet(1, 2);
		thread.join();
		assertEquals(2, state.get());
	}

	/**
	 * Looper.loop()が内部で実行される
	 */
	public void testLoop_2() throws InterruptedException {
		final AtomicInteger state = new AtomicInteger(0);
		if (Looper.myLooper() == null) {
			Looper.prepare();
		}
		(new Handler()).post(new Runnable() {
			@Override
			public void run() {
				state.compareAndSet(0, 1);
				quitLoop();
			}
		});
		loop();
		state.compareAndSet(1, 2);
		assertEquals(2, state.get());
	}
}
