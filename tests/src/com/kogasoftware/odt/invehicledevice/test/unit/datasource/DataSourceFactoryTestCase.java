package com.kogasoftware.odt.invehicledevice.test.unit.datasource;

import android.test.AndroidTestCase;

import com.kogasoftware.odt.invehicledevice.datasource.DataSourceFactory;
import com.kogasoftware.odt.invehicledevice.datasource.EmptyDataSource;
import com.kogasoftware.odt.invehicledevice.empty.EmptyFile;

public class DataSourceFactoryTestCase extends AndroidTestCase {
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testNewInstance_1() throws Exception {
		assertTrue(DataSourceFactory.newInstance() instanceof EmptyDataSource);
		assertFalse(DataSourceFactory.newInstance("http://localhost", "foo",
				new EmptyFile()) instanceof EmptyDataSource);
	}
}
