package com.kogasoftware.odt.invehicledevice.test.unit.logic.datasource;

import com.kogasoftware.odt.invehicledevice.logic.datasource.WebAPIDataSource;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;

public class WebAPIDataSourceTestCase extends EmptyActivityInstrumentationTestCase2 {
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testWebAPIDataSource_1() throws Exception {
		String url = "";
		String token = "";

		WebAPIDataSource result = new WebAPIDataSource(url, token);

		// An unexpected exception was thrown in user code while executing this
		// test:
		// java.lang.RuntimeException: Stub!
		// at android.os.Looper.getMainLooper(Looper.java:7)
		// at
		// com.kogasoftware.odt.invehicledevice.logic.datasource.WebAPIDataSource.<init>(WebAPIDataSource.java:47)
		assertNotNull(result);
	}
}
