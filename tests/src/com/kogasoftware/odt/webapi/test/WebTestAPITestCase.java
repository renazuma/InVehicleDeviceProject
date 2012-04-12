package com.kogasoftware.odt.webapi.test;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

import android.test.ActivityInstrumentationTestCase2;

public class WebTestAPITestCase extends
		ActivityInstrumentationTestCase2<DummyActivity> {

	public WebTestAPITestCase() {
		super("com.kogasoftware.odt.webapi.test", DummyActivity.class);
	}

	CountDownLatch latch;
	boolean succeed = false;
	int resultCode;
	List<VehicleNotification> vehicleNotifications;
	VehicleNotification vehicleNotification;
	
	public void testCleanDatabase() throws Exception {
		WebTestAPI api = new WebTestAPI();
		latch = new CountDownLatch(1);
		succeed = false;
		
		api.cleanDatabase(new WebAPICallback<Void>() {
			@Override
			public void onSucceed(int reqkey, int statusCode, Void result) {
				succeed = true;
				latch.countDown();
			}
			
			@Override
			public void onFailed(int reqkey, int statusCode) {
				latch.countDown();
			}
			
			@Override
			public void onException(int reqkey, WebAPIException ex) {
				latch.countDown();
			}
		});

		latch.await();
		
		assertTrue(succeed);
	}
	
	public void testVehicleNotifications() throws Exception {
		WebTestAPI api = new WebTestAPI();

		// オブジェクトをひとつ生成
		VehicleNotification obj = new VehicleNotification();
		obj.setInVehicleDeviceId(1);
		obj.setOperatorId(20);
		obj.setBody("車載器への通知1です");

		latch = new CountDownLatch(1);
		succeed = false;
		resultCode = -1;
		api.createVehicleNotification(obj, new WebAPICallback<VehicleNotification>() {

			@Override
			public void onSucceed(int reqkey, int statusCode,
					VehicleNotification result) {
				succeed = true;
				resultCode = statusCode;
				latch.countDown();
			}

			@Override
			public void onFailed(int reqkey, int statusCode) {
				resultCode = statusCode;
				latch.countDown();
			}

			@Override
			public void onException(int reqkey, WebAPIException ex) {
				latch.countDown();
			}
		});
		
		latch.await();
		assertTrue(succeed);
		
		// オブジェクトをひとつ生成
		obj = new VehicleNotification();
		obj.setInVehicleDeviceId(1);
		obj.setOperatorId(20);
		obj.setBody("車載器への通知2です");
		
		latch = new CountDownLatch(1);
		succeed = false;
		api.createVehicleNotification(obj, new WebAPICallback<VehicleNotification>() {

			@Override
			public void onSucceed(int reqkey, int statusCode,
					VehicleNotification result) {
				succeed = true;
				latch.countDown();
			}

			@Override
			public void onFailed(int reqkey, int statusCode) {
				latch.countDown();
			}

			@Override
			public void onException(int reqkey, WebAPIException ex) {
				latch.countDown();
			}
		});
		
		latch.await();
		assertTrue(succeed);

		// オブジェクト数を確認
		latch = new CountDownLatch(1);
		succeed = false;
		api.getAllVehicleNotifications(new WebAPICallback<List<VehicleNotification>>() {
			
			@Override
			public void onSucceed(int reqkey, int statusCode,
					List<VehicleNotification> result) {
				vehicleNotifications = result;
				latch.countDown();
			}
			
			@Override
			public void onFailed(int reqkey, int statusCode) {
				latch.countDown();
			}
			
			@Override
			public void onException(int reqkey, WebAPIException ex) {
				latch.countDown();
			}
		});

		latch.await();
		assertEquals(2, vehicleNotifications.size());
		
		// 2つめのオブジェクト取得
		latch = new CountDownLatch(1);
		succeed = false;
		api.getVehicleNotification(vehicleNotifications.get(1).getId(), new WebAPICallback<VehicleNotification>() {

			@Override
			public void onSucceed(int reqkey, int statusCode,
					VehicleNotification result) {
				vehicleNotification = result;
				latch.countDown();
			}

			@Override
			public void onFailed(int reqkey, int statusCode) {
				latch.countDown();
			}

			@Override
			public void onException(int reqkey, WebAPIException ex) {
				latch.countDown();
			}
			
		});
		
		latch.await();
		assertEquals("車載器への通知2です", vehicleNotification.getBody().orNull());
		
		// オブジェクトをひとつ削除
		latch = new CountDownLatch(1);
		succeed = false;
		api.deleteVehicleNotification(vehicleNotifications.get(1).getId(), new WebAPICallback<Void>() {

			@Override
			public void onSucceed(int reqkey, int statusCode, Void result) {
				succeed = true;
				latch.countDown();
			}

			@Override
			public void onFailed(int reqkey, int statusCode) {
				latch.countDown();
			}

			@Override
			public void onException(int reqkey, WebAPIException ex) {
				latch.countDown();
			}
			
		});
		
		latch.await();
		assertTrue(succeed);
		
		// オブジェクト数を確認
		latch = new CountDownLatch(1);
		succeed = false;
		api.getAllVehicleNotifications(new WebAPICallback<List<VehicleNotification>>() {
			
			@Override
			public void onSucceed(int reqkey, int statusCode,
					List<VehicleNotification> result) {
				vehicleNotifications = result;
				latch.countDown();
			}
			
			@Override
			public void onFailed(int reqkey, int statusCode) {
				latch.countDown();
			}
			
			@Override
			public void onException(int reqkey, WebAPIException ex) {
				latch.countDown();
			}
		});

		latch.await();
		assertEquals(1, vehicleNotifications.size());
	}

}
