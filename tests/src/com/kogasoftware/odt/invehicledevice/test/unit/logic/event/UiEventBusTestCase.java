package com.kogasoftware.odt.invehicledevice.test.unit.logic.event;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import android.os.Handler;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.logic.event.UiEventBus;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;

public class UiEventBusTestCase extends EmptyActivityInstrumentationTestCase2 {
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
		if (ueb != null) {
			ueb.dispose();
		}
	}

	/**
	 * disposeしたらオブジェクトにイベントが渡らなくなる。新規登録もできない
	 */
	public void testDispose_1() throws Exception {
		final AtomicInteger ai1 = new AtomicInteger(0);
		final AtomicInteger ai2 = new AtomicInteger(0);
		final Semaphore s1 = new Semaphore(0);
		final Semaphore s2 = new Semaphore(0);

		Test t1 = new Test() {
			@Subscribe
			@Override
			public void test(Object object) {
				ai1.addAndGet(1);
				s1.release();
			}
		};
		Test t2 = new Test() {
			@Subscribe
			@Override
			public void test(Object object) {
				ai2.addAndGet(1);
				s2.release();
			}
		};
		ueb.register(t1);
		assertEquals(ai1.get(), 0);
		ueb.post(new Object());
		assertTrue(s1.tryAcquire(500, TimeUnit.MILLISECONDS));
		assertEquals(ai1.get(), 1);
		ueb.dispose();
		ueb.post(new Object());
		assertFalse(s1.tryAcquire(500, TimeUnit.MILLISECONDS));
		assertEquals(ai1.get(), 1);
		ueb.register(t1);
		ueb.register(t2);
		ueb.post(new Object());
		assertFalse(s1.tryAcquire(500, TimeUnit.MILLISECONDS));
		assertFalse(s2.tryAcquire(500, TimeUnit.MILLISECONDS));
		assertEquals(ai1.get(), 1);
		assertEquals(ai2.get(), 0);
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
		final CountDownLatch cdl2 = new CountDownLatch(1);
		ueb.register(new Test() {
			@Subscribe
			@Override
			public void test(Object object) {
				executedThreadId.set(Thread.currentThread().getId());
				cdl2.countDown();
			}
		});

		new Thread() {
			@Override
			public void run() {
				ueb.post(new Object());
			}
		}.start();

		cdl2.await();

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

	/**
	 * unregisterしたらオブジェクトにイベントが渡らなくなる
	 */
	public void testUnregister_1() throws Exception {
		final AtomicInteger ai1 = new AtomicInteger(0);
		final AtomicInteger ai2 = new AtomicInteger(0);
		final Semaphore s1 = new Semaphore(0);
		final Semaphore s2 = new Semaphore(0);

		Test t1 = new Test() {
			@Subscribe
			@Override
			public void test(Object object) {
				ai1.addAndGet(1);
				s1.release();
			}
		};
		Test t2 = new Test() {
			@Subscribe
			@Override
			public void test(Object object) {
				ai2.addAndGet(1);
				s2.release();
			}
		};
		ueb.register(t1);
		ueb.register(t2);
		assertEquals(ai1.get(), 0);
		assertEquals(ai2.get(), 0);
		ueb.post(new Object());
		assertTrue(s1.tryAcquire(500, TimeUnit.MILLISECONDS));
		assertTrue(s2.tryAcquire(500, TimeUnit.MILLISECONDS));
		assertEquals(ai1.get(), 1);
		assertEquals(ai2.get(), 1);
		ueb.unregister(t2);
		ueb.post(new Object());
		assertTrue(s1.tryAcquire(500, TimeUnit.MILLISECONDS));
		assertFalse(s2.tryAcquire(500, TimeUnit.MILLISECONDS));
		assertEquals(ai1.get(), 2);
		assertEquals(ai2.get(), 1);
	}

}
