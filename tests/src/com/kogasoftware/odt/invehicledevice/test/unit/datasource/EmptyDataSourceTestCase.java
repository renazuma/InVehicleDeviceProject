package com.kogasoftware.odt.invehicledevice.test.unit.datasource;

import android.test.AndroidTestCase;

import com.kogasoftware.odt.invehicledevice.datasource.EmptyDataSource;
import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;

public class EmptyDataSourceTestCase extends AndroidTestCase {
	public void testSmallInstance() throws Exception {
		TestUtil.assertEmptyObject(getContext(), EmptyDataSource.class);
	}
}
