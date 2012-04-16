package com.kogasoftware.odt.webapi.test;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.kogasoftware.odt.webapi.WebAPI;
import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.Demand;
import com.kogasoftware.odt.webapi.model.InVehicleDevice;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.Platform;
import com.kogasoftware.odt.webapi.model.UnitAssignment;
import com.kogasoftware.odt.webapi.model.User;
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
	
	List<OperationSchedule> schedules;
	public void testGetOperationSchedules() throws Exception {
		WebAPI api = new WebAPI(master.getInVehicleDevice().getAuthenticationToken().orNull());
		latch = new CountDownLatch(1);
		schedules = null;
		
		Date dt = new Date();
		User user = master.createUser("login1", "もぎ", "けんた");
		UnitAssignment ua = record.createUnitAssignment("1号車");
		Platform p1 = master.createPlatform("乗降場1", "じょうこうじょう1");
		OperationSchedule os1 = record.createOperationSchedule(ua, p1, dt, dt);
		Platform p2 = master.createPlatform("乗降場2", "じょうこうじょう2");
		OperationSchedule os2 = record.createOperationSchedule(ua, p2, dt, dt);
		
		Demand demand = record.createDemand(user, ua, p1, dt, p2, dt, 0);
		assertNotNull(record.createReservation(user, demand, ua, p1, os1, dt, p2, os2, dt, 0));
		
		api.getOperationSchedules(new WebAPICallback<List<OperationSchedule>>() {
			@Override
			public void onSucceed(int reqkey, int statusCode, List<OperationSchedule> result) {
				schedules = result;
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
		
		assertNotNull(schedules);
		assertEquals(2, schedules.size());
		
	}
}
