package com.kogasoftware.odt.webapi.test;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.Operator;
import com.kogasoftware.odt.webapi.model.ServiceProvider;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

public class WebTestAPITestCase extends
		ActivityInstrumentationTestCase2<DummyActivity> {

	public WebTestAPITestCase() {
		super("com.kogasoftware.odt.webapi.test", DummyActivity.class);
	}

	boolean succeed = false;
	private ServiceProvider serviceProvider;
	public void testCleanDatabase() throws Exception {
		WebTestAPI api = new WebTestAPI();
		final CountDownLatch latch = new CountDownLatch(1);
		
		api.cleanDatabase(new WebAPICallback<Void>() {
			@Override
			public void onSucceed(int reqkey, int statusCode, Void result) {
				succeed = true;
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

		latch.await();
		
		assertTrue(succeed);
	}

	public void testServiceProviders() throws Exception {
		testCleanDatabase();
		
		final WebTestAPI api = new WebTestAPI();

		// オブジェクトをひとつ生成
		SyncCall<ServiceProvider> sc = new SyncCall<ServiceProvider>() {
			@Override
			public int run() throws Exception {
				ServiceProvider obj = new ServiceProvider();
				obj.setName("もぎ市");
				
				return api.createServiceProvider(obj, this);
			}
		};
		assertEquals(SyncCall.SUCCEED, sc.getCallback());
		this.serviceProvider = sc.getResult();
	
	}

	public void testOperators() throws Exception {
		testCleanDatabase();
		testServiceProviders();
		
		final WebTestAPI api = new WebTestAPI();

		// オブジェクトをひとつ生成
		SyncCall<Operator> sc = new SyncCall<Operator>() {
			@Override
			public int run() throws Exception {
				Operator obj = new Operator();
				obj.setLogin("operator1");
				obj.setPassword("pass");
				obj.setPasswordConfirmation("pass");
				obj.setFirstName("もぎ");
				obj.setLastName("もぎぞう");
				obj.setServiceProvider(serviceProvider);
				
				return api.createOperator(obj, this);
			}
		};
		assertEquals(SyncCall.SUCCEED, sc.getCallback());
	
	}
	
	public void testVehicleNotifications() throws Exception {
		testCleanDatabase();
		testServiceProviders();
		testOperators();
		
		final WebTestAPI api = new WebTestAPI();

		// オブジェクトをひとつ生成
		SyncCall<VehicleNotification> sc = new SyncCall<VehicleNotification>() {
			@Override
			public int run() throws Exception {
				VehicleNotification obj = new VehicleNotification();
				obj.setInVehicleDeviceId(1);
				obj.setOperatorId(20);
				obj.setBody("車載器への通知1です");

				return api.createVehicleNotification(obj, this);
			}
		};
		Log.d("VehicleNotification", sc.getResponseString());
		assertEquals(SyncCall.SUCCEED, sc.getCallback());
		
		// オブジェクトをひとつ生成
		sc = new SyncCall<VehicleNotification>() {
			@Override
			public int run() throws Exception {
				VehicleNotification obj = new VehicleNotification();
				obj.setInVehicleDeviceId(1);
				obj.setOperatorId(20);
				obj.setBody("車載器への通知2です");

				return api.createVehicleNotification(obj, this);
			}
		};		
		assertEquals(SyncCall.SUCCEED, sc.getCallback());

		// オブジェクト数を確認
		SyncCall<List<VehicleNotification>>scl = new SyncCall<List<VehicleNotification>>() {
			@Override
			public int run() throws Exception {
				return api.getAllVehicleNotifications(this);
			}
		};		
		assertEquals(SyncCall.SUCCEED, scl.getCallback());
		assertEquals(2, scl.getResult().size());
		
		final int id = scl.getResult().get(1).getId();
		// 2つめのオブジェクト取得
		sc = new SyncCall<VehicleNotification>() {
			@Override
			public int run() throws Exception {
				return api.getVehicleNotification(id, this);
			}
		};		
		assertEquals("車載器への通知2です", sc.getResult().getBody().orNull());

		final int id2 = sc.getResult().getId();
		// オブジェクトをひとつ削除
		SyncCall<Void> scv = new SyncCall<Void>() {
			@Override
			public int run() throws Exception {
				return api.deleteVehicleNotification(id2, this);
			}
		};
		assertEquals(SyncCall.SUCCEED, scv.getCallback());
		
		// オブジェクト数を確認
		scl = new SyncCall<List<VehicleNotification>>() {
			@Override
			public int run() throws Exception {
				return api.getAllVehicleNotifications(this);
			}
		};		
		assertEquals(SyncCall.SUCCEED, scl.getCallback());
		assertEquals(1, scl.getResult().size());
	}

}
