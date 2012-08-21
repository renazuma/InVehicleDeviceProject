package com.kogasoftware.odt.invehicledevice.test.unit.empty;

import android.test.AndroidTestCase;

import com.kogasoftware.odt.invehicledevice.empty.EmptyThread;
import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;

public class EmptyThreadTestCase extends AndroidTestCase {
	public void testSmallInstance() throws Exception {
		TestUtil.assertEmptyObject(getContext(), EmptyThread.class, true);
	}

	public void testStart() throws Exception {
		Thread t = new EmptyThread();
		t.start();
		t.join(200);
		assertFalse(t.isAlive()); // 即座に終了する
	}
}

