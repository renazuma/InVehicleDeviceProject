package com.kogasoftware.odt.invehicledevice.test.unit.empty;

import android.test.InstrumentationTestCase;

import com.kogasoftware.odt.invehicledevice.empty.EmptyThread;
import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;

public class EmptyThreadTestCase extends InstrumentationTestCase {
	public void testSmallInstance() throws Exception {
		TestUtil.assertEmptyObject(getInstrumentation(), EmptyThread.class, true);
	}

	public void testStart() throws Exception {
		Thread t = new EmptyThread();
		t.start();
		t.join(200);
		assertFalse(t.isAlive()); // 即座に終了する
	}
}

