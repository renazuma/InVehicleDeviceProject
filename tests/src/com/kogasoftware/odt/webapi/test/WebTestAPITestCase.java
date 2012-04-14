package com.kogasoftware.odt.webapi.test;

import java.util.List;

import android.test.ActivityInstrumentationTestCase2;

import com.kogasoftware.odt.webapi.model.Operator;
import com.kogasoftware.odt.webapi.model.ServiceProvider;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class WebTestAPITestCase extends
		ActivityInstrumentationTestCase2<DummyActivity> {

	public WebTestAPITestCase() {
		super("com.kogasoftware.odt.webapi.test", DummyActivity.class);
	}

	GenerateMaster master = new GenerateMaster();
	
	private ServiceProvider serviceProvider;
	public void testCleanDatabase() throws Exception {
		assertTrue(master.cleanDatabase());
	}

	public void testServiceProviders() throws Exception {
		master.cleanDatabase();
		assertNotNull(master.createServiceProvider());
	}

	public void testOperators() throws Exception {
		master.cleanDatabase();
		master.createServiceProvider();

		assertNotNull(master.createOperator());	
	}
	
	public void testVehicleNotifications() throws Exception {
		master.cleanDatabase();
		master.createServiceProvider();

		final Operator operator = master.createOperator();
		
		final WebTestAPI api = new WebTestAPI();

		// オブジェクトをひとつ生成
		SyncCall<VehicleNotification> sc = new SyncCall<VehicleNotification>() {
			@Override
			public int run() throws Exception {
				VehicleNotification obj = new VehicleNotification();
				obj.setInVehicleDeviceId(1);
				obj.setOperator(operator);
				obj.setBody("車載器への通知1です");

				return api.createVehicleNotification(obj, this);
			}
		};
		assertEquals(SyncCall.SUCCEED, sc.getCallback());
		
		// オブジェクトをひとつ生成
		sc = new SyncCall<VehicleNotification>() {
			@Override
			public int run() throws Exception {
				VehicleNotification obj = new VehicleNotification();
				obj.setInVehicleDeviceId(1);
				obj.setOperator(operator);
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
