package com.kogasoftware.odt.invehicledevice.test.unit.datasource;

import com.kogasoftware.odt.invehicledevice.datasource.DataSourceFactory;
import com.kogasoftware.odt.invehicledevice.empty.EmptyFile;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;

public class DataSourceFactoryTestCase extends
		EmptyActivityInstrumentationTestCase2 {
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testNewInstance_1() throws Exception {
		DataSourceFactory.newInstance("http://localhost", "foo",
				new EmptyFile());
	}
}