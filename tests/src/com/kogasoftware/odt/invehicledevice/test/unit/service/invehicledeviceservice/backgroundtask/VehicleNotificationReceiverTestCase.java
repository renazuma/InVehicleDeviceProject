package com.kogasoftware.odt.invehicledevice.test.unit.service.invehicledeviceservice.backgroundtask;

import java.util.LinkedList;
import java.util.List;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.backgroundtask.VehicleNotificationReceiver;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;
import com.kogasoftware.odt.invehicledevice.test.util.datasource.DummyDataSource;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class VehicleNotificationReceiverTestCase extends
		EmptyActivityInstrumentationTestCase2 {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testRun_新しいVNがあればVehicleNotificationReceivedEvent通知() throws Exception {
		final VehicleNotification vn0 = new VehicleNotification();
		final VehicleNotification vn1 = new VehicleNotification();
		class TestDataSource extends DummyDataSource {
			@Override
			public List<VehicleNotification> getVehicleNotifications()
					throws WebAPIException {
				List<VehicleNotification> l = new LinkedList<VehicleNotification>();
				l.add(vn0);
				l.add(vn1);
				return l;
			}
		}
		TestUtil.setDataSource(new TestDataSource());
		VehicleNotificationReceiver vnr = new VehicleNotificationReceiver(null);
		vnr.run();
		Thread.sleep(5000);
		assertTrue(false);
	}
	
	public void testRun_新しいVNがなければVehicleNotificationReceivedEvent通知は起きない_1() throws Exception {
		class TestDataSource extends DummyDataSource {
			@Override
			public List<VehicleNotification> getVehicleNotifications()
					throws WebAPIException {
				return new LinkedList<VehicleNotification>();
			}
		}
		TestUtil.setDataSource(new TestDataSource());
		VehicleNotificationReceiver vnr = new VehicleNotificationReceiver(null);
		vnr.run();
		Thread.sleep(5000);
		assertTrue(false);
	}
	
	public void testRun_新しいVNがなければVehicleNotificationReceivedEvent通知は起きない_2() throws Exception {
		class TestDataSource extends DummyDataSource {
			@Override
			public List<VehicleNotification> getVehicleNotifications()
					throws WebAPIException {
				throw new WebAPIException("not found");
			}
		}
		TestUtil.setDataSource(new TestDataSource());
		VehicleNotificationReceiver vnr = new VehicleNotificationReceiver(null);
		vnr.run();
		Thread.sleep(5000);
		assertTrue(false);
	}
}