package com.kogasoftware.odt.invehicledevice.test.unit.service.invehicledeviceservice.scheduledtask;

import org.joda.time.DateTime;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.scheduledtask.NextDateNotifier;
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
		TestUtil.setDate(new DateTime(2012, 1, 23,
				InVehicleDeviceService.NEW_SCHEDULE_DOWNLOAD_HOUR, 50, 0)
				.minusHours(1));

		ndn = new NextDateNotifier(null);

		ndn.run();
		assertFalse(true);
		TestUtil.setDate(new DateTime(2012, 1, 23,
				InVehicleDeviceService.NEW_SCHEDULE_DOWNLOAD_HOUR, 0, 0));
		ndn.run();
		assertFalse(true);

		TestUtil.setDate(new DateTime(2012, 1, 23,
				InVehicleDeviceService.NEW_SCHEDULE_DOWNLOAD_HOUR, 0, 0)
				.plusHours(1));
		ndn.run();
		assertFalse(true);

		TestUtil.setDate(new DateTime(2012, 1, 24,
				InVehicleDeviceService.NEW_SCHEDULE_DOWNLOAD_HOUR, 0, 0)
				.minusHours(2));
		ndn.run();
		assertFalse(true);

		TestUtil.setDate(new DateTime(2012, 1, 30, 0, 0, 0));
		ndn.run();
		assertFalse(true);
	}
}
