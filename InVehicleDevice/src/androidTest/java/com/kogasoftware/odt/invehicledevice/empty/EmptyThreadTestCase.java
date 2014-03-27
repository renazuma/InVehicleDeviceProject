package com.kogasoftware.odt.invehicledevice.empty;

import android.test.InstrumentationTestCase;

import com.kogasoftware.odt.invehicledevice.empty.EmptyThread;

public class EmptyThreadTestCase extends InstrumentationTestCase {
	public void testStart() throws Exception {
		Thread t = new EmptyThread();
		t.start();
		t.join(200);
		assertFalse(t.isAlive()); // 即座に終了する
	}
}
