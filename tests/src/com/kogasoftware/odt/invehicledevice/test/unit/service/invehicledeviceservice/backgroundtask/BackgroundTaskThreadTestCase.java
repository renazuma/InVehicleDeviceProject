package com.kogasoftware.odt.invehicledevice.test.unit.service.invehicledeviceservice.backgroundtask;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.Uninterruptibles;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.backgroundtask.BackgroundTaskThread;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;

public class BackgroundTaskThreadTestCase extends
		EmptyActivityInstrumentationTestCase2 {

	@Override
	public void setUp() throws Exception {
		super.setUp();
		Thread.sleep(10 * 1000);
	}

	/**
	 * interruptで確実に終了させることができるか確認
	 */
	public void testInterrupt() throws Exception {
		for (int i = 0; i < 2000; i += 100) {
			BackgroundTaskThread t = new BackgroundTaskThread(null);
			t.start();
			Thread.sleep(i);
			t.interrupt();
			t.join(10000);
			assertFalse(t.isAlive());
		}
	}

	/**
	 * BackgroundTaskを大量に発生させても致命的なエラーは発生しない
	 */
	public void testMultipleInstances() throws Exception {
		getActivity().moveTaskToBack(true); // ANRが起きないようにActivityをバックグラウンドへ退避
		Thread.sleep(5000);

		final Queue<Thread> ts = new ConcurrentLinkedQueue<Thread>();
		for (int i = 0; i < 100; i += 5) {
			final int fi = i;
			Thread t = new Thread() {
				@Override
				public void run() {
					Uninterruptibles.sleepUninterruptibly(fi * 50,
							TimeUnit.MILLISECONDS);
					BackgroundTaskThread bt = new BackgroundTaskThread(null);
					bt.start();
					ts.add(bt);
				}
			};
			t.start();
			ts.add(t);
		}
		int l = 0;
		for (Thread t : new LinkedList<Thread>(ts)) {
			if (++l % 2 == 0) {
				continue;
			}
			t.interrupt();
		}
		for (int i = 0; i < 100; i += 5) {
			final int fi = i;
			Thread t = new Thread() {
				@Override
				public void run() {
					Uninterruptibles.sleepUninterruptibly(fi * 50,
							TimeUnit.MILLISECONDS);
					BackgroundTaskThread bt = new BackgroundTaskThread(null);
					bt.start();
					ts.add(bt);
				}
			};
			t.start();
			ts.add(t);
		}
		while (true) {
			Boolean alive = false;
			for (Thread t : new LinkedList<Thread>(ts)) {
				alive |= t.isAlive();
			}
			if (!alive) {
				break;
			}
			for (Thread t : new LinkedList<Thread>(ts)) {
				t.interrupt();
			}
			for (Thread t : new LinkedList<Thread>(ts)) {
				t.join();
			}
		}
	}

	/**
	 * runは直接呼ばない！
	 */
	public void xtestRun() throws Exception {
		fail("stub !");
	}
}
