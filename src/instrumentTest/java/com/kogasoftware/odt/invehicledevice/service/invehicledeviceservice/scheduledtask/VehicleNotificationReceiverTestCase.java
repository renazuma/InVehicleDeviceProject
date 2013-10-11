package com.kogasoftware.odt.invehicledevice.test.unit.service.invehicledeviceservice.scheduledtask;

import java.util.LinkedList;
import java.util.List;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.scheduledtask.VehicleNotificationReceiver;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;
import com.kogasoftware.odt.invehicledevice.test.util.apiclient.DummyApiClient;
import com.kogasoftware.odt.apiclient.ApiClientCallback;
import com.kogasoftware.odt.apiclient.ApiClientException;
import com.kogasoftware.odt.invehicledevice.apiclient.model.VehicleNotification;

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

	public void testRun_新しいVNがあればVehicleNotificationReceivedEvent通知()
			throws Exception {
		final VehicleNotification vn0 = new VehicleNotification();
		final VehicleNotification vn1 = new VehicleNotification();
		class TestApiClient extends DummyApiClient {
			@Override
			public int getVehicleNotifications(
					ApiClientCallback<List<VehicleNotification>> callback) {
				List<VehicleNotification> l = new LinkedList<VehicleNotification>();
				l.add(vn0);
				l.add(vn1);
				callback.onSucceed(0, 200, l);
				return 0;
			}
		}
		TestUtil.setApiClient(new TestApiClient());
		VehicleNotificationReceiver vnr = new VehicleNotificationReceiver(null);
		vnr.run();
		Thread.sleep(5000);
		assertTrue(false);
	}

	public void testRun_新しいVNがなければVehicleNotificationReceivedEvent通知は起きない_1()
			throws Exception {
		class TestApiClient extends DummyApiClient {
			@Override
			public int getVehicleNotifications(
					ApiClientCallback<List<VehicleNotification>> callback) {
				callback.onSucceed(0, 200,
						new LinkedList<VehicleNotification>());
				return 0;
			}
		}
		TestUtil.setApiClient(new TestApiClient());
		VehicleNotificationReceiver vnr = new VehicleNotificationReceiver(null);
		vnr.run();
		Thread.sleep(5000);
		assertTrue(false);
	}

	public void testRun_新しいVNがなければVehicleNotificationReceivedEvent通知は起きない_2()
			throws Exception {
		class TestApiClient extends DummyApiClient {
			@Override
			public int getVehicleNotifications(
					ApiClientCallback<List<VehicleNotification>> callback) {
				callback.onException(0, new ApiClientException("not found"));
				return 0;
			}
		}
		TestUtil.setApiClient(new TestApiClient());
		VehicleNotificationReceiver vnr = new VehicleNotificationReceiver(null);
		vnr.run();
		Thread.sleep(5000);
		assertTrue(false);
	}
}