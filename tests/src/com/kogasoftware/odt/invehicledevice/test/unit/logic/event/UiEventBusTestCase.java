package com.kogasoftware.odt.invehicledevice.test.unit.logic.event;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import android.os.Handler;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.logic.event.UiEventBus;
import com.kogasoftware.odt.invehicledevice.test.common.MockActivityUnitTestCase;

public class UiEventBusTestCase extends MockActivityUnitTestCase {
	interface Test {
		void test(Object object);
	}

	UiEventBus ueb;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		final CountDownLatch cdl = new CountDownLatch(1);
		final AtomicReference<Handler> ar = new AtomicReference<Handler>();
		Handler h;
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ar.set(new Handler());
				cdl.countDown();
			}
		});
		cdl.await();
		h = ar.get();
		Preconditions.checkNotNull(h);

		// ueb = new UiEventBus(h);
		ueb = new UiEventBus(getActivityHandler());
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		ueb.dispose();
	}

	/**
	 * 別スレッドからpostしてもUIスレッドで確実に実行されるかをチェック
	 */
	public void testPost_1() throws Exception {
		final AtomicLong uiThreadId = new AtomicLong(0);
		final CountDownLatch cdl1 = new CountDownLatch(1);
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				uiThreadId.set(Thread.currentThread().getId());
				cdl1.countDown();
			}
		});
		cdl1.await();

		final AtomicLong executedThreadId = new AtomicLong(0);
		final AtomicBoolean quit = new AtomicBoolean(false);
		ueb.register(new Test() {
			@Subscribe
			@Override
			public void test(Object object) {
				executedThreadId.set(Thread.currentThread().getId());
				quitLoop();
			}
		});

		new Thread() {
			@Override
			public void run() {
				ueb.post(new Object());
			}
		}.start();

		loop();

		assertEquals(uiThreadId.get(), executedThreadId.get());
	}

	/**
	 * UIスレッドからpostしてUIスレッドで確実に実行されるかをチェック
	 */
	public void testPost_2() throws Exception {
		final AtomicLong uiThreadId = new AtomicLong(0);
		final CountDownLatch cdl1 = new CountDownLatch(1);
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				uiThreadId.set(Thread.currentThread().getId());
				cdl1.countDown();
			}
		});
		cdl1.await();

		final AtomicLong executedThreadId = new AtomicLong(0);
		final CountDownLatch cdl2 = new CountDownLatch(1);
		ueb.register(new Test() {
			@Subscribe
			@Override
			public void test(Object object) {
				executedThreadId.set(Thread.currentThread().getId());
				cdl2.countDown();
			}
		});

		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ueb.post(new Object());
			}
		});

		cdl2.await();
		assertEquals(uiThreadId.get(), executedThreadId.get());
	}
}
