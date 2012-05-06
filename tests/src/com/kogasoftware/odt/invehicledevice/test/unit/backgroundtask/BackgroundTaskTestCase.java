package com.kogasoftware.odt.invehicledevice.test.unit.backgroundtask;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.kogasoftware.odt.invehicledevice.backgroundtask.BackgroundTask;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;

public class BackgroundTaskTestCase extends
		EmptyActivityInstrumentationTestCase2 {

	StatusAccess sa;
	CommonLogic cl;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		sa = new StatusAccess(getActivity());
		cl = new CommonLogic(getActivity(), getActivityHandler(), sa);
	}

	/**
	 * スレッドが割り込まれていた場合はループせず終了
	 */
	public void testLoop_1() throws Exception {
		final CountDownLatch cdl = new CountDownLatch(1);
		new Thread() {
			@Override
			public void run() {
				BackgroundTask bt = new BackgroundTask(cl, getInstrumentation()
						.getContext(), sa);
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
				BackgroundTask bt = new BackgroundTask(cl, getInstrumentation()
						.getContext(), sa);
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
				final BackgroundTask bt = new BackgroundTask(cl,
						getInstrumentation().getContext(), sa);
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
				final BackgroundTask bt = new BackgroundTask(cl,
						getInstrumentation().getContext(), sa);
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
				BackgroundTask bt = new BackgroundTask(cl, getInstrumentation()
						.getContext(), sa);
				bt.quit();
				bt.loop();
				cdl.countDown();
			}
		}.start();
		assertTrue(cdl.await(10, TimeUnit.SECONDS));
	}
}
