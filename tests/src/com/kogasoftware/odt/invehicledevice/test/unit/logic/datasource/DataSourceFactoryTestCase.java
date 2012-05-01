package com.kogasoftware.odt.invehicledevice.test.unit.logic.datasource;

import com.kogasoftware.odt.invehicledevice.logic.datasource.DataSource;
import com.kogasoftware.odt.invehicledevice.logic.datasource.DataSourceFactory;
import com.kogasoftware.odt.invehicledevice.logic.datasource.WebAPIDataSource;
import com.kogasoftware.odt.invehicledevice.test.common.MockActivityUnitTestCase;

public class DataSourceFactoryTestCase extends MockActivityUnitTestCase {
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testDataSourceFactory_1() throws Exception {
		DataSourceFactory result = new DataSourceFactory();
		assertNotNull(result);
	}

	public void testNewInstance_1() throws Exception {

		DataSource result = DataSourceFactory.newInstance();

		// An unexpected exception was thrown in user code while executing this
		// test:
		// java.lang.RuntimeException: Stub!
		// at android.os.Looper.getMainLooper(Looper.java:7)
		// at
		// com.kogasoftware.odt.invehicledevice.logic.datasource.WebAPIDataSource.<init>(WebAPIDataSource.java:47)
		// at
		// com.kogasoftware.odt.invehicledevice.logic.datasource.DataSourceFactory.newInstance(DataSourceFactory.java:26)
		// at
		// com.kogasoftware.odt.invehicledevice.logic.datasource.DataSourceFactory.newInstance(DataSourceFactory.java:16)
		assertNotNull(result);
	}

	public void testNewInstance_2() throws Exception {
		String url = "";
		String token = "";

		DataSource result = DataSourceFactory.newInstance(url, token);

		// An unexpected exception was thrown in user code while executing this
		// test:
		// java.lang.RuntimeException: Stub!
		// at android.os.Looper.getMainLooper(Looper.java:7)
		// at
		// com.kogasoftware.odt.invehicledevice.logic.datasource.WebAPIDataSource.<init>(WebAPIDataSource.java:47)
		// at
		// com.kogasoftware.odt.invehicledevice.logic.datasource.DataSourceFactory.newInstance(DataSourceFactory.java:26)
		assertNotNull(result);
	}

	public void testSetInstance_1() throws Exception {
		DataSource dataSource = new WebAPIDataSource("", "");

		DataSourceFactory.setInstance(dataSource);

		// An unexpected exception was thrown in user code while executing this
		// test:
		// java.lang.RuntimeException: Stub!
		// at android.os.Looper.getMainLooper(Looper.java:7)
		// at
		// com.kogasoftware.odt.invehicledevice.logic.datasource.WebAPIDataSource.<init>(WebAPIDataSource.java:47)
	}
}
