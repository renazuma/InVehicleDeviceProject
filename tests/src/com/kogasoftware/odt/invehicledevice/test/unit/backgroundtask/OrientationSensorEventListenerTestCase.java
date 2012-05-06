package com.kogasoftware.odt.invehicledevice.test.unit.backgroundtask;

import com.kogasoftware.odt.invehicledevice.backgroundtask.OrientationSensorEventListener;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.datasource.DataSourceFactory;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.test.util.datasource.DummyDataSource;

public class OrientationSensorEventListenerTestCase extends
		EmptyActivityInstrumentationTestCase2 {

	CommonLogic cl;
	DummyDataSource dds;
	OrientationSensorEventListener osel;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		dds = new DummyDataSource();
		DataSourceFactory.setInstance(dds);
		cl = newCommonLogic();
		osel = new OrientationSensorEventListener(cl);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testOnSensorChanged() throws Exception {
		fail("stub!");
	}
}
