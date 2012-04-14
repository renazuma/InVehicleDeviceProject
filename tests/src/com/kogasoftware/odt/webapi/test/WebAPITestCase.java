package com.kogasoftware.odt.webapi.test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.kogasoftware.odt.webapi.WebAPI;
import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.InVehicleDevice;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class WebAPITestCase extends ActivityInstrumentationTestCase2<DummyActivity> {
	public WebAPITestCase() {
		super("com.kogasoftware.odt.webapi.test", DummyActivity.class);
	}
		
	CountDownLatch latch;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		final WebTestAPI testAPI = new WebTestAPI();

		new SyncCall<Void>() {
			@Override
			public int run() throws WebAPIException {
				return testAPI.cleanDatabase(this);
			}
		};
		
		SyncCall<InVehicleDevice> c = new SyncCall<InVehicleDevice>() {
			@Override
			public int run() throws Exception {
				InVehicleDevice ivd = new InVehicleDevice();
				ivd.setLogin("ivd1");
				ivd.setPassword("ivdpass");
				ivd.setPasswordConfirmation("ivdpass");
				ivd.setModelName("モデル名");
				ivd.setTypeNumber("車種");
				
				Log.d("WebAPITest", ivd.toJSONObject().toString());
				return testAPI.createInVehicleDevice(ivd, this);
			}
		};
		
	}

	public void testLogin() throws Exception {
		WebAPI api = new WebAPI();
		latch = new CountDownLatch(1);
		
		InVehicleDevice ivd = new InVehicleDevice();
		ivd.setLogin("ivd1");
		ivd.setPassword("ivdpass");
		api.login(ivd, new WebAPICallback<InVehicleDevice>() {

			@Override
			public void onSucceed(int reqkey, int statusCode, InVehicleDevice result) {
				latch.countDown();
			}

			@Override
			public void onFailed(int reqkey, int statusCode, String response) {
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
			public void onSucceed(int reqkey, int statusCode, List<VehicleNotification> result) {
				notifications = result;
				latch.countDown();
			}
			
			@Override
			public void onFailed(int reqkey, int statusCode, String response) {
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
