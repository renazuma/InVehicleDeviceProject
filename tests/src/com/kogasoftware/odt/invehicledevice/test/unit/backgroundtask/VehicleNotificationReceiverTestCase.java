package com.kogasoftware.odt.invehicledevice.test.unit.backgroundtask;

import com.kogasoftware.odt.invehicledevice.backgroundtask.VehicleNotificationReceiver;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.datasource.DataSourceFactory;
import com.kogasoftware.odt.invehicledevice.test.common.MockActivityUnitTestCase;
import com.kogasoftware.odt.invehicledevice.test.common.datasource.DummyDataSource;

public class VehicleNotificationReceiverTestCase extends
		MockActivityUnitTestCase {

	CommonLogic cl;
	DummyDataSource dds;
	VehicleNotificationReceiver vnr;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		dds = new DummyDataSource();
		DataSourceFactory.setInstance(dds);
		cl = new CommonLogic(getActivity(), getActivityHandler());
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