package com.kogasoftware.odt.invehicledevice.test.unit.datasource;

import android.test.InstrumentationTestCase;

import com.kogasoftware.odt.invehicledevice.datasource.EmptyDataSource;
import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;

public class EmptyDataSourceTestCase extends InstrumentationTestCase {
	public void testSmallInstance() throws Exception {
		TestUtil.assertEmptyObject(getInstrumentation(), EmptyDataSource.class);
	}
}
