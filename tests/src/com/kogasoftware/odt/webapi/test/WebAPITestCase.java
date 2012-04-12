package com.kogasoftware.odt.webapi.test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import android.test.ActivityInstrumentationTestCase2;

import com.kogasoftware.odt.webapi.WebAPI;
import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.InVehicleDevice;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class WebAPITestCase extends ActivityInstrumentationTestCase2<DummyActivity> {

	public WebAPITestCase() {
		super("com.kogasoftware.odt.webapi.test", DummyActivity.class);
	}
	
	public void testLogin() throws Exception {
		WebAPI api = new WebAPI();
		final CountDownLatch latch = new CountDownLatch(1);
		
		InVehicleDevice ivd = new InVehicleDevice();
		ivd.setLogin("d1");
		ivd.setPassword("1");
		api.login(ivd, new WebAPICallback<InVehicleDevice>() {

			@Override
			public void onSucceed(int reqkey, InVehicleDevice result) {
				latch.countDown();
			}

			@Override
			public void onFailed(int reqkey) {
				latch.countDown();
			}

			@Override
			public void onException(int reqkey, WebAPIException ex) {
				latch.countDown();
			}
		});

		latch.await(100, TimeUnit.SECONDS);
		
		assertNotNull(api.getAuthenticationToken());
		assertTrue(api.getAuthenticationToken().length() > 0);
	}

	List<VehicleNotification> notifications;

	public void testGetVehicleNotifications() throws Exception {
		WebAPI api = new WebAPI("7EhKVSpqMu4a2p8SKDrH");
		final CountDownLatch latch = new CountDownLatch(1);
		notifications = null;
		
		api.getVehicleNotifications(new WebAPICallback<List<VehicleNotification>>() {
			@Override
			public void onSucceed(int reqkey, List<VehicleNotification> result) {
				notifications = result;
				latch.countDown();
			}
			
			@Override
			public void onFailed(int reqkey) {
				latch.countDown();
			}

			@Override
			public void onException(int reqkey, WebAPIException ex) {
				latch.countDown();
			}
		});
		
		
		latch.await(100, TimeUnit.SECONDS);
		
		assertNotNull(notifications);
		assertEquals(1, notifications.size());
	}
	
}
