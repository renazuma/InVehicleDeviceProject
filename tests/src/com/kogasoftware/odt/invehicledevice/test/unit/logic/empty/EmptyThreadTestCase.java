package com.kogasoftware.odt.invehicledevice.test.unit.logic.empty;

import com.kogasoftware.odt.invehicledevice.empty.EmptyThread;
import junit.framework.TestCase;

public class EmptyThreadTestCase extends TestCase {
	public void testStart() throws Exception {
		Thread t = new EmptyThread();
		t.start();
		t.join(200); // 即座に終了する
		assertFalse(t.isAlive());
	}
}