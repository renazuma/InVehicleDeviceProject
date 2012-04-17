package com.kogasoftware.odt.webapi.test;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.test.ActivityInstrumentationTestCase2;
import android.text.format.DateUtils;

import com.kogasoftware.odt.webapi.model.Demand;
import com.kogasoftware.odt.webapi.model.Driver;
import com.kogasoftware.odt.webapi.model.InVehicleDevice;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.Operator;
import com.kogasoftware.odt.webapi.model.Platform;
import com.kogasoftware.odt.webapi.model.ServiceProvider;
import com.kogasoftware.odt.webapi.model.UnitAssignment;
import com.kogasoftware.odt.webapi.model.User;
import com.kogasoftware.odt.webapi.model.Vehicle;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class WebTestAPITestCase extends
		ActivityInstrumentationTestCase2<DummyActivity> {

	public WebTestAPITestCase() {
		super("com.kogasoftware.odt.webapi.test", DummyActivity.class);
	}

	GenerateMaster master = new GenerateMaster();
	GenerateRecord record = new GenerateRecord(master);
	
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
		master.createOperator();
		
		final WebTestAPI api = new WebTestAPI();

		// オブジェクトをひとつ生成
		assertNotNull(record.createVehicleNotification("車載器への通知1です"));
		
		// オブジェクトをひとつ生成
		assertNotNull(record.createVehicleNotification("車載器への通知2です"));

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
		SyncCall<VehicleNotification> sc = new SyncCall<VehicleNotification>() {
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

	public void testPlatform() throws Exception {
		master.cleanDatabase();
		master.createServiceProvider();
		Platform p1 = master.createPlatform("乗降場1", "じょうこうじょう1");
		Platform p2 = master.createPlatform("乗降場2", "じょうこうじょう2");
		
		assertNotNull(p1);
		assertEquals("乗降場1", p1.getName());
		assertNotNull(p2);
		assertEquals("乗降場2", p2.getName());
	}
	
	public void testUnitAssingment() throws Exception {
		master.cleanDatabase();
		master.createServiceProvider();
		
		assertNotNull(record.createUnitAssignment("1号車"));
		assertNotNull(record.createUnitAssignment("2号車"));
	}
	
	public void testOperationSchedule() throws Exception {
		master.cleanDatabase();
		master.createServiceProvider();
		
		Platform p1 = master.createPlatform("乗降場1", "じょうこうじょう1");
		UnitAssignment u = record.createUnitAssignment("1号車");

		Date dt = new Date();
		OperationSchedule s = record.createOperationSchedule(u, p1, dt, dt);
		
		assertNotNull(s);
		assertEquals(dt.getTime() / 1000, s.getDepartureEstimate().getTime() / 1000);
	}

	public void testReservation() throws Exception {
		master.cleanDatabase();
		master.createServiceProvider();
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, 20);
		Date dtArrival1 = cal.getTime();

		cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, 22);
		Date dtDeparture1 = cal.getTime();

		cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, 40);
		Date dtArrival2 = cal.getTime();

		cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, 45);
		Date dtDeparture2 = cal.getTime();

		User user = master.createUser("login1", "もぎ", "たろう");
		UnitAssignment ua = record.createUnitAssignment("1号車");
		Platform p1 = master.createPlatform("乗降場1", "じょうこうじょう1");
		OperationSchedule os1 = record.createOperationSchedule(ua, p1, dtArrival1, dtDeparture1);
		Platform p2 = master.createPlatform("乗降場2", "じょうこうじょう2");
		OperationSchedule os2 = record.createOperationSchedule(ua, p2, dtArrival2, dtDeparture2);
		
		Demand demand = record.createDemand(user, ua, p1, dtDeparture1, p2, dtArrival1, 0);
		assertNotNull(record.createReservation(user, demand, ua, p1, os1, dtDeparture2, p2, os2, dtArrival1, 0));
		
	}
	
	public void testDemand() throws Exception {
		master.cleanDatabase();
		master.createServiceProvider();
		
		Date dt = new Date();
		User user = master.createUser("login1", "もぎ", "たろう");
		UnitAssignment ua = record.createUnitAssignment("1号車");
		Platform p1 = master.createPlatform("乗降場1", "じょうこうじょう1");
		Platform p2 = master.createPlatform("乗降場2", "じょうこうじょう2");
		
		assertNotNull(record.createDemand(user, ua, p1, dt, p2, dt, 0));
	}
	
	public void testUser() throws Exception {
		master.cleanDatabase();
		master.createServiceProvider();

		assertNotNull(master.createUser("login1", "モギー", "司郎"));
	}

	public void testDriver() throws Exception {
		master.cleanDatabase();
		master.createServiceProvider();

		Driver d = master.createDriver("もぎぎ", "けんいち", "001");
		assertNotNull(d);
		assertTrue(d.getId() != 0);
		
	}

	public void testVehicle() throws Exception {
		master.cleanDatabase();
		master.createServiceProvider();

		assertNotNull(master.createVehicle("ちば 90 も 99-99", "もぎ号"));
	}
	
	public void testServiceUnit() throws Exception {
		master.cleanDatabase();
		master.createServiceProvider();

		Driver d = master.createDriver("もぎぎ", "けんいち", "001");
		Vehicle v = master.createVehicle("ちば 90 も 99-99", "もぎ号");
		InVehicleDevice i = master.createInVehicleDevice();
		UnitAssignment u = record.createUnitAssignment("星");
		
		assertNotNull(record.createServiceUnit(d, v, i, u, new Date()));
	}
}
