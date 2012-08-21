package com.kogasoftware.odt.invehicledevice.test.unit.empty;

import com.kogasoftware.odt.invehicledevice.empty.EmptyCloseable;
import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;

import android.test.AndroidTestCase;

public class EmptyCloseableTestCase extends AndroidTestCase {
	public void testSmallInstance() throws Exception {
		TestUtil.assertEmptyObject(getContext(), EmptyCloseable.class);
	}
}
