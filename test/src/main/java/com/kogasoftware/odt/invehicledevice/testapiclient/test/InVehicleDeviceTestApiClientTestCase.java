package com.kogasoftware.odt.invehicledevice.testapiclient.test;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.test.AndroidTestCase;

import com.google.common.io.Closeables;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Demand;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Driver;
import com.kogasoftware.odt.invehicledevice.apiclient.model.InVehicleDevice;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Platform;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Reservation;
import com.kogasoftware.odt.invehicledevice.apiclient.model.UnitAssignment;
import com.kogasoftware.odt.invehicledevice.apiclient.model.User;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Vehicle;
import com.kogasoftware.odt.invehicledevice.apiclient.model.VehicleNotification;
import com.kogasoftware.odt.invehicledevice.testapiclient.GenerateMaster;
import com.kogasoftware.odt.invehicledevice.testapiclient.GenerateRecord;
import com.kogasoftware.odt.invehicledevice.testapiclient.InVehicleDeviceTestApiClient;
import com.kogasoftware.odt.invehicledevice.testapiclient.SyncCall;
import static junit.framework.Assert.*;
class DummyAndroidTestCase {
	protected void setUp() throws Exception {
	}

	protected void tearDown() throws Exception {
	}
	
	protected Context getContext() {
		return null;
	}
}

public class InVehicleDeviceTestApiClientTestCase extends DummyAndroidTestCase {
	public static final String TEST_SERVER_HOST = "http://10.0.2.2:3333";

	InVehicleDeviceTestApiClient api;
	GenerateMaster master;
	GenerateRecord record;
	
	@Override
	public void setUp() {
		master = new GenerateMaster(TEST_SERVER_HOST);
		record = new GenerateRecord(master);
	}
	
	@Override
	public void tearDown() {
		Closeables.closeQuietly(master);
		Closeables.closeQuietly(record);
		Closeables.closeQuietly(api);
	}
	
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
		
		api = new InVehicleDeviceTestApiClient(TEST_SERVER_HOST);

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
		assertEquals("車載器への通知2です", sc.getResult().getBody());

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

		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(2011, 1, 23);
		OperationSchedule s = record.createOperationSchedule(u, p1, cal.getTime());
		
		assertNotNull(s);
		assertEquals(p1.getId(), s.getPlatform().get().getId());
	}

	public void testReservation() throws Exception {
		master.cleanDatabase();
		master.createServiceProvider();
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, 22);
		Date dtDeparture = cal.getTime();

		cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, 40);
		Date dtArrival = cal.getTime();

		User user = master.createUser("login1", "もぎ", "たろう");
		UnitAssignment ua = record.createUnitAssignment("1号車");
		Platform p1 = master.createPlatform("乗降場1", "じょうこうじょう1");
		OperationSchedule os1 = record.createOperationSchedule(ua, p1, new Date());
		Platform p2 = master.createPlatform("乗降場2", "じょうこうじょう2");
		OperationSchedule os2 = record.createOperationSchedule(ua, p2, new Date());
		
		Demand demand = record.createDemand(user, ua, p1, dtDeparture, p2, dtArrival, 0);
		assertNotNull(record.createReservation(user, demand, ua, p1, os1, dtDeparture, p2, os2, dtArrival, 0));
		
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
	
	public void testPassengerRecord() throws Exception {
		master.cleanDatabase();
		master.createServiceProvider();
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, 22);
		Date dtDeparture = cal.getTime();

		cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, 40);
		Date dtArrival = cal.getTime();
		
		Date now = new Date();

		User user = master.createUser("login1", "もぎ", "たろう");
		UnitAssignment ua = record.createUnitAssignment("1号車");
		Platform p1 = master.createPlatform("乗降場1", "じょうこうじょう1");
		OperationSchedule os1 = record.createOperationSchedule(ua, p1, now);
		Platform p2 = master.createPlatform("乗降場2", "じょうこうじょう2");
		OperationSchedule os2 = record.createOperationSchedule(ua, p2, now);
		
		Demand demand = record.createDemand(user, ua, p1, dtDeparture, p2, dtArrival, 0);
		Reservation res = record.createReservation(user, demand, ua, p1, os1, dtDeparture, p2, os2, dtArrival, 0);
		
		assertNotNull(record.createPassengerRecord(res, user, os1, os2, 300));
	}

	public void testOperationRecord() throws Exception {
		master.cleanDatabase();
		master.createServiceProvider();
		
		Platform p1 = master.createPlatform("乗降場1", "じょうこうじょう1");
		UnitAssignment u = record.createUnitAssignment("1号車");

		Date dt = new Date();
		OperationSchedule s = record.createOperationSchedule(u, p1, dt);
		
		assertNotNull(record.createOperationRecord(s, dt, false, dt, true));
	}


}
