package com.kogasoftware.odt.invehicledevice.empty;

import com.kogasoftware.odt.invehicledevice.empty.EmptyCloseable;
import com.kogasoftware.odt.invehicledevice.testutil.TestUtil;

import android.test.InstrumentationTestCase;

public class EmptyCloseableTestCase extends InstrumentationTestCase {
	public void testSmallInstance() throws Exception {		
		TestUtil.assertEmptyObject(getInstrumentation(), EmptyCloseable.class);
	}
}
