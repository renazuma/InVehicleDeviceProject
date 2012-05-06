package com.kogasoftware.odt.invehicledevice.test.unit.backgroundtask;

import com.kogasoftware.odt.invehicledevice.backgroundtask.VehicleNotificationReceiver;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.datasource.DataSourceFactory;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.test.util.datasource.DummyDataSource;

public class VehicleNotificationReceiverTestCase extends
		EmptyActivityInstrumentationTestCase2 {

	CommonLogic cl;
	DummyDataSource dds;
	VehicleNotificationReceiver vnr;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		dds = new DummyDataSource();
		DataSourceFactory.setInstance(dds);
		cl = newCommonLogic();
		vnr = new VehicleNotificationReceiver(cl);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testVehicleNotificationReceiver_1() throws Exception {
		// 引数のクラスがnewできないのでstub
		fail("stub!");
	}
}