package com.kogasoftware.odt.invehicledevice.test.unit.logic.empty;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.kogasoftware.odt.invehicledevice.logic.empty.EmptyThread;

public class EmptyThreadTestCase extends TestCase {
	public void testRun() throws Exception {
		(new SimpleTimeLimiter()).callWithTimeout(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				Thread t = new EmptyThread();
				t.run();
				return null;
			}
		}, 200, TimeUnit.MILLISECONDS, true);
		// 何もせず即座に制御を返す
	}

	public void testStart() throws Exception {
		Thread t = new EmptyThread();
		t.start();
		t.join(200); // 即座に終了する
		assertFalse(t.isAlive());
	}
}