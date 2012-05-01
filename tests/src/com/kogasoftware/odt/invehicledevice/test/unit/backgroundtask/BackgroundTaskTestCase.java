package com.kogasoftware.odt.invehicledevice.test.unit.backgroundtask;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import android.os.Handler;

import com.kogasoftware.odt.invehicledevice.backgroundtask.BackgroundTask;
import com.kogasoftware.odt.invehicledevice.test.common.MockActivityUnitTestCase;

public class BackgroundTaskTestCase extends MockActivityUnitTestCase {
	/**
	 * スレッドが割り込まれていた場合はループせず終了
	 */
	public void testLoop_1() throws Exception {
		final Handler h = getActivityHandler();
		final CountDownLatch cdl = new CountDownLatch(1);
		new Thread() {
			@Override
			public void run() {
				BackgroundTask bt = new BackgroundTask(getActivity(), h);
				Thread.currentThread().interrupt();
				bt.loop();
				cdl.countDown();
			}
		}.start();
		cdl.await(10, TimeUnit.SECONDS);
	}

	/**
	 * スレッドがコンストラクタ後割り込まれていた場合はループせず終了
	 */
	public void testLoop_2() throws Exception {
		final Handler h = getActivityHandler();
		final CountDownLatch cdl = new CountDownLatch(1);
		new Thread() {
			@Override
			public void run() {
				Thread.currentThread().interrupt();
				BackgroundTask bt = new BackgroundTask(getActivity(), h);
				bt.loop();
				cdl.countDown();
			}
		}.start();
		cdl.await(10, TimeUnit.SECONDS);
	}

	/**
	 * quit()呼び出しで終了
	 */
	public void testLoop_3() throws Exception {
		final Handler h = getActivityHandler();
		final CountDownLatch cdl = new CountDownLatch(1);
		new Thread() {
			@Override
			public void run() {
				final BackgroundTask bt = new BackgroundTask(getActivity(), h);
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
		cdl.await(10, TimeUnit.SECONDS);
	}

	/**
	 * loop()を終了する
	 */
	public void testQuit_1() throws Exception {
		final Handler h = getActivityHandler();
		final CountDownLatch cdl = new CountDownLatch(1);
		new Thread() {
			@Override
			public void run() {
				final BackgroundTask bt = new BackgroundTask(getActivity(), h);
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
		cdl.await(10, TimeUnit.SECONDS);
	}

	/**
	 * loop()されるまえに呼び出してもloop()は終了する
	 */
	public void testQuit_2() throws Exception {
		final Handler h = getActivityHandler();
		final CountDownLatch cdl = new CountDownLatch(1);
		new Thread() {
			@Override
			public void run() {
				BackgroundTask bt = new BackgroundTask(getActivity(), h);
				bt.quit();
				bt.loop();
				cdl.countDown();
			}
		}.start();
		cdl.await(10, TimeUnit.SECONDS);
	}
}
