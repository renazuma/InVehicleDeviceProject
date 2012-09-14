package com.kogasoftware.odt.invehicledevice.test.unit.empty;

import com.kogasoftware.odt.invehicledevice.empty.EmptyCloseable;
import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;

import android.test.InstrumentationTestCase;

public class EmptyCloseableTestCase extends InstrumentationTestCase {
	public void testSmallInstance() throws Exception {		
		TestUtil.assertEmptyObject(getInstrumentation(), EmptyCloseable.class);
	}
}
