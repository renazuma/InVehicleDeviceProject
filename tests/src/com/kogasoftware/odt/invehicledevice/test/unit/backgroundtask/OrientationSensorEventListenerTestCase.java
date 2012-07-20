package com.kogasoftware.odt.invehicledevice.test.unit.backgroundtask;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.backgroundthread.OrientationSensorEventListener;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;
import com.kogasoftware.odt.invehicledevice.test.util.datasource.DummyDataSource;

public class OrientationSensorEventListenerTestCase extends
		EmptyActivityInstrumentationTestCase2 {

	DummyDataSource dds;
	OrientationSensorEventListener osel;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		dds = new DummyDataSource();
		TestUtil.setDataSource(dds);
		osel = new OrientationSensorEventListener(null);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void xtestOnSensorChanged() throws Exception {
		fail("stub!");
	}
}
