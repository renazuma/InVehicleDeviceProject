package com.kogasoftware.odt.invehicledevice.test.unit.service.invehicledeviceservicebackgroundthread;

import org.joda.time.DateTime;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.backgroundthread.NextDateNotifier;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;

public class NextDateNotifierTestCase extends
		EmptyActivityInstrumentationTestCase2 {

	NextDateNotifier ndn;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testしきい時間を過ぎるとNewOperationStartEvent送信() throws Exception {
		TestUtil.setDate(new DateTime(2012, 1, 23, InVehicleDeviceService.NEW_SCHEDULE_DOWNLOAD_HOUR - 1, 50, 0));

		ndn = new NextDateNotifier(null);

		ndn.run();
		assertFalse(true);
		TestUtil.setDate(new DateTime(2012, 1, 23, InVehicleDeviceService.NEW_SCHEDULE_DOWNLOAD_HOUR, 0, 0));
		ndn.run();
		assertFalse(true);

		TestUtil.setDate(new DateTime(2012, 1, 23, InVehicleDeviceService.NEW_SCHEDULE_DOWNLOAD_HOUR + 1, 0, 0));
		ndn.run();
		assertFalse(true);
		
		TestUtil.setDate(new DateTime(2012, 1, 24, InVehicleDeviceService.NEW_SCHEDULE_DOWNLOAD_HOUR - 2, 0, 0));
		ndn.run();
		assertFalse(true);
		
		TestUtil.setDate(new DateTime(2012, 1, 30, 0, 0, 0));
		ndn.run();
		assertFalse(true);
	}
}
