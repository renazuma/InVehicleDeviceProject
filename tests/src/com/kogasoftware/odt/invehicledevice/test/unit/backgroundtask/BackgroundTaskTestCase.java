package com.kogasoftware.odt.invehicledevice.test.unit.backgroundtask;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.backgroundthread.BackgroundTask;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;
import com.kogasoftware.odt.invehicledevice.test.util.datasource.DummyDataSource;

public class BackgroundTaskTestCase extends
		EmptyActivityInstrumentationTestCase2 {

	LocalDataSource sa;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		Thread.sleep(10 * 1000);
		TestUtil.setDataSource(new DummyDataSource());
		sa = new LocalDataSource(getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * スレッドが割り込まれていた場合はループせず終了
	 */
	public void testLoop_1() throws Exception {
		final CountDownLatch cdl = new CountDownLatch(1);
		new Thread() {
			@Override
			public void run() {
				BackgroundTask bt = new BackgroundTask(null);
				Thread.currentThread().interrupt();
				bt.loop();
				cdl.countDown();
			}
		}.start();
		assertTrue(cdl.await(10, TimeUnit.SECONDS));
	}

	/**
	 * スレッドがコンストラクタ後割り込まれていた場合はループせず終了
	 */
	public void testLoop_2() throws Exception {
		final CountDownLatch cdl = new CountDownLatch(1);
		new Thread() {
			@Override
			public void run() {
				Thread.currentThread().interrupt();
				BackgroundTask bt = new BackgroundTask(null);
				bt.loop();
				cdl.countDown();
			}
		}.start();
		assertTrue(cdl.await(10, TimeUnit.SECONDS));
	}

	/**
	 * quit()呼び出しで終了
	 */
	public void testLoop_3() throws Exception {
		final CountDownLatch cdl = new CountDownLatch(1);
		new Thread() {
			@Override
			public void run() {
				final BackgroundTask bt = new BackgroundTask(null);
				new Thread() {
					@Override
					public void run() {
						bt.quit();
					}
				}.start();
				bt.loop();
				cdl.countDown();
			}
		}.start();
		assertTrue(cdl.await(10, TimeUnit.SECONDS));
	}

	/**
	 * loop()を終了する
	 */
	public void testQuit_1() throws Exception {
		final CountDownLatch cdl = new CountDownLatch(1);
		new Thread() {
			@Override
			public void run() {
				final BackgroundTask bt = new BackgroundTask(null);
				new Thread() {
					@Override
					public void run() {
						bt.quit();
					}
				}.start();
				bt.loop();
				cdl.countDown();
			}
		}.start();
		assertTrue(cdl.await(10, TimeUnit.SECONDS));
	}

	/**
	 * loop()されるまえに呼び出してもloop()は終了する
	 */
	public void testQuit_2() throws Exception {
		final CountDownLatch cdl = new CountDownLatch(1);
		new Thread() {
			@Override
			public void run() {
				BackgroundTask bt = new BackgroundTask(null);
				bt.quit();
				bt.loop();
				cdl.countDown();
			}
		}.start();
		assertTrue(cdl.await(10, TimeUnit.SECONDS));
	}
}
