package com.kogasoftware.odt.invehicledevice.test.unit.apiclient;

import android.test.InstrumentationTestCase;

import com.kogasoftware.odt.invehicledevice.apiclient.EmptyDataSource;
import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;

public class EmptyDataSourceTestCase extends InstrumentationTestCase {
	public void testSmallInstance() throws Exception {
		TestUtil.assertEmptyObject(getInstrumentation(), EmptyDataSource.class);
	}
}
