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
	private GenerateMaster master;
	private GenerateRecord record;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		// マスタ生成
		master = new GenerateMaster();
		master.cleanDatabase();
		master.createServiceProvider();
		master.createInVehicleDevice();
		master.createOperator();
		
		record = new GenerateRecord(master);
	}

	public void testPasswordLogin() throws Exception {
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
		WebAPI api = new WebAPI(master.getInVehicleDevice().getAuthenticationToken().orNull());
		latch = new CountDownLatch(1);
		notifications = null;
		
		record.createVehicleNotification("テスト通知メッセージ1です。");
		record.createVehicleNotification("テスト通知メッセージ2です。");
		
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
		assertEquals(2, notifications.size());
		assertEquals("テスト通知メッセージ2です。", notifications.get(1).getBody());
	}
	
	public void testResponseVehicleNotification() throws Exception {
		WebAPI api = new WebAPI(master.getInVehicleDevice().getAuthenticationToken().orNull());
		latch = new CountDownLatch(1);
		notifications = null;
		
		record.createVehicleNotification("テスト通知メッセージ1です。");
		record.createVehicleNotification("テスト通知メッセージ2です。");
		
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
		assertEquals(2, notifications.size());
		
		latch = new CountDownLatch(1);
		api.responseVehicleNotification(notifications.get(0), 1, new WebAPICallback<VehicleNotification>() {
			@Override
			public void onSucceed(int reqkey, int statusCode, VehicleNotification result) {
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

		latch = new CountDownLatch(1);
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
		assertEquals("テスト通知メッセージ2です。", notifications.get(0).getBody().orNull());
	}
}
