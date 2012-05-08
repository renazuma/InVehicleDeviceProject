package com.kogasoftware.odt.invehicledevice.test.unit.logic.event;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.logic.event.UiEventBus;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;

public class UiEventBusTestCase extends EmptyActivityInstrumentationTestCase2 {
	static class Test {
		void test(Object object) {
		}
	}

	UiEventBus ueb;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Thread.sleep(10 * 1000);
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
	 * unregisterしたらオブジェクトにイベントが渡らなくなる
	 */
	public void testCountRegeisteredClass() throws Exception {
		final Semaphore s1 = new Semaphore(0);
		final Semaphore s2 = new Semaphore(0);

		Test t1 = new Test() {
			@Subscribe
			@Override
			public void test(Object object) {
				s1.release();
			}
		};
		Test t2 = new Test() {
			@Subscribe
			@Override
			public void test(Object object) {
				s2.release();
			}
		};

		assertEquals(ueb.countRegisteredClass(t1.getClass()).intValue(), 0);
		assertEquals(ueb.countRegisteredClass(t2.getClass()).intValue(), 0);
		assertEquals(ueb.countRegisteredClass(Test.class).intValue(), 0);
		assertEquals(ueb.countRegisteredClass(Object.class).intValue(), 0);

		ueb.register(t1);
		ueb.register(t2);
		ueb.post(new Object());
		assertTrue(s1.tryAcquire(2, TimeUnit.SECONDS));
		assertTrue(s2.tryAcquire(1, TimeUnit.SECONDS));

		assertEquals(ueb.countRegisteredClass(t1.getClass()).intValue(), 1);
		assertEquals(ueb.countRegisteredClass(t2.getClass()).intValue(), 1);
		assertEquals(ueb.countRegisteredClass(Test.class).intValue(), 2);
		assertEquals(ueb.countRegisteredClass(Object.class).intValue(), 2);

		ueb.unregister(t2);
		ueb.post(new Object());
		assertTrue(s1.tryAcquire(2, TimeUnit.SECONDS));
		assertFalse(s2.tryAcquire(1, TimeUnit.SECONDS));

		assertEquals(ueb.countRegisteredClass(t1.getClass()).intValue(), 1);
		assertEquals(ueb.countRegisteredClass(t2.getClass()).intValue(), 0);
		assertEquals(ueb.countRegisteredClass(Test.class).intValue(), 1);
		assertEquals(ueb.countRegisteredClass(Object.class).intValue(), 1);

		ueb.register(t2);
		ueb.post(new Object());
		assertTrue(s1.tryAcquire(2, TimeUnit.SECONDS));
		assertTrue(s2.tryAcquire(1, TimeUnit.SECONDS));

		assertEquals(ueb.countRegisteredClass(t1.getClass()).intValue(), 1);
		assertEquals(ueb.countRegisteredClass(t2.getClass()).intValue(), 1);
		assertEquals(ueb.countRegisteredClass(Test.class).intValue(), 2);
		assertEquals(ueb.countRegisteredClass(Object.class).intValue(), 2);
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
		assertTrue(s1.tryAcquire(2, TimeUnit.SECONDS));
		assertEquals(ai1.get(), 1);
		ueb.dispose();
		ueb.post(new Object());
		assertFalse(s1.tryAcquire(2, TimeUnit.SECONDS));
		assertEquals(ai1.get(), 1);
		ueb.register(t1);
		ueb.register(t2);
		ueb.post(new Object());
		assertFalse(s1.tryAcquire(2, TimeUnit.SECONDS));
		assertFalse(s2.tryAcquire(1, TimeUnit.SECONDS));
		assertEquals(ai1.get(), 1);
		assertEquals(ai2.get(), 0);
	}

	/**
	 * HighPriorityがついているオブジェクトは先にイベントを受信
	 */
	public void testHighPriority() throws Exception {
		final AtomicInteger ai = new AtomicInteger(0);
		final Semaphore s1 = new Semaphore(0);
		final Semaphore s2 = new Semaphore(0);
		final Semaphore s3 = new Semaphore(0);
		final Semaphore s4 = new Semaphore(0);

		@UiEventBus.HighPriority
		class HighTest1 {
			@Subscribe
			public void test(Object object) {
				ai.compareAndSet(0, 1);
				s1.release();
			}
		}
		HighTest1 t1 = new HighTest1();

		Test t2 = new Test() {
			@Subscribe
			@Override
			public void test(Object object) {
				ai.compareAndSet(1, 2);
				s2.release();
			}
		};

		@UiEventBus.HighPriority
		class HighTest3 {
			@Subscribe
			public void test(Object object) {
				ai.compareAndSet(0, 3);
				ai.compareAndSet(1, 3);
				s3.release();
			}
		}
		HighTest3 t3 = new HighTest3();

		Test t4 = new Test() {
			@Subscribe
			@Override
			public void test(Object object) {
				ai.compareAndSet(3, 4);
				s4.release();
			}
		};

		assertTrue(HighTest1.class
				.isAnnotationPresent(UiEventBus.HighPriority.class));
		assertTrue((new HighTest3()).getClass().isAnnotationPresent(
				UiEventBus.HighPriority.class));
		assertTrue(t1.getClass().isAnnotationPresent(
				UiEventBus.HighPriority.class));
		assertFalse(t2.getClass().isAnnotationPresent(
				UiEventBus.HighPriority.class));
		assertTrue(t3.getClass().isAnnotationPresent(
				UiEventBus.HighPriority.class));
		assertFalse(t4.getClass().isAnnotationPresent(
				UiEventBus.HighPriority.class));

		// t1とt2では、t1が先に実行される
		ai.set(0);
		ueb.register(t1);
		ueb.register(t2);
		assertEquals(ai.get(), 0);
		ueb.post(new Object());
		assertTrue(s1.tryAcquire(2, TimeUnit.SECONDS));
		assertTrue(s2.tryAcquire(1, TimeUnit.SECONDS));
		assertEquals(ai.get(), 2);

		// t3は最後に追加されたが、t2よりも先に実行される
		ai.set(0);
		ueb.register(t3);
		assertEquals(ai.get(), 0);
		ueb.post(new Object());
		assertTrue(s1.tryAcquire(2, TimeUnit.SECONDS));
		assertTrue(s2.tryAcquire(1, TimeUnit.SECONDS));
		assertTrue(s3.tryAcquire(1, TimeUnit.SECONDS));
		assertEquals(ai.get(), 3);

		// t4は最後に追加されたが、t1,t3よりも後に実行される
		ai.set(0);
		ueb.register(t4);
		assertEquals(ai.get(), 0);
		ueb.post(new Object());
		assertTrue(s1.tryAcquire(2, TimeUnit.SECONDS));
		assertTrue(s2.tryAcquire(1, TimeUnit.SECONDS));
		assertTrue(s3.tryAcquire(1, TimeUnit.SECONDS));
		assertTrue(s4.tryAcquire(1, TimeUnit.SECONDS));
		assertEquals(ai.get(), 4);

		ai.set(0);
		ueb.unregister(t1);
		ueb.unregister(t4);
		assertEquals(ai.get(), 0);
		ueb.post(new Object());
		assertTrue(s2.tryAcquire(2, TimeUnit.SECONDS));
		assertTrue(s3.tryAcquire(1, TimeUnit.SECONDS));
		assertEquals(ai.get(), 3);

		ai.set(1);
		ueb.unregister(t3);
		assertEquals(ai.get(), 1);
		ueb.post(new Object());
		assertTrue(s2.tryAcquire(1, TimeUnit.SECONDS));
		assertEquals(ai.get(), 2);
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
		assertTrue(s1.tryAcquire(2, TimeUnit.SECONDS));
		assertTrue(s2.tryAcquire(1, TimeUnit.SECONDS));
		assertEquals(ai1.get(), 1);
		assertEquals(ai2.get(), 1);
		ueb.unregister(t2);
		ueb.post(new Object());
		assertTrue(s1.tryAcquire(2, TimeUnit.SECONDS));
		assertFalse(s2.tryAcquire(1, TimeUnit.SECONDS));
		assertEquals(ai1.get(), 2);
		assertEquals(ai2.get(), 1);

		// 一度unregisterしてももう一度registerでイベントが渡る
		ueb.register(t2);
		ueb.post(new Object());
		assertTrue(s1.tryAcquire(2, TimeUnit.SECONDS));
		assertTrue(s2.tryAcquire(1, TimeUnit.SECONDS));
		assertEquals(ai1.get(), 3);
		assertEquals(ai2.get(), 2);
	}

	public void testオーバーライドされたメソッドがSubscribeしているとオーバーライドした側はSubscribeする必要は無い() {
		class FooEvent {
		}

		class FooHandler {
			@Subscribe
			public void foo(FooEvent e) {
			}
		}

		class ExtendedFooHandler extends FooHandler {
			public final AtomicInteger extendedFooCounter = new AtomicInteger(0);

			@Override
			// @Subscribeは要らない
			public void foo(FooEvent e) {
				super.foo(e);
				extendedFooCounter.addAndGet(1);
			}
		}

		ExtendedFooHandler efh = new ExtendedFooHandler();
		ueb.register(efh);
		ueb.post(new FooEvent());
		getInstrumentation().waitForIdleSync();
		assertEquals(1, efh.extendedFooCounter.get());
	}
}
