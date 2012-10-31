package com.kogasoftware.odt.invehicledevice.test.unit.service.invehicledeviceservice;

import java.io.File;
import java.util.List;

import junitx.framework.ComparableAssert;

import org.apache.commons.lang3.SerializationUtils;
import android.test.AndroidTestCase;
import android.util.Log;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData.VehicleNotificationStatus;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Reservation;
import com.kogasoftware.odt.invehicledevice.apiclient.model.ServiceProvider;
import com.kogasoftware.odt.invehicledevice.apiclient.model.User;
import com.kogasoftware.odt.invehicledevice.apiclient.model.VehicleNotification;

public class LocalDataTestCase extends AndroidTestCase {
	private static final String TAG = LocalDataTestCase.class.getSimpleName();

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * 本当にシリアライズすることができるかのチェック
	 */
	public void testSerializable() throws Exception {
		LocalData ld1 = new LocalData();
		ld1.url = "http://example.com/" + Math.random();
		ld1.file = new File("foo" + Math.random());
		ld1.serviceProvider = new ServiceProvider();
		ld1.serviceProvider.setName("テストサービスプロバイダー");
		LocalData ld2 = SerializationUtils.clone(ld1);
		assertEquals(ld1.url, ld2.url);
		assertEquals(ld1.file, ld2.file);
		assertFalse(ld1.file == ld2.file);
		assertEquals(ld1.serviceProvider.getName(),
				ld2.serviceProvider.getName());
	}

	/**
	 * シリアライズ速度が常識的な範囲で収まるかのチェック
	 */
	public void xtestSerializeSpeed() throws Exception {
		for (Integer i = 0; i < 5; ++i) {
			assertSerializeSpeed();
			Thread.sleep(1000);
			System.gc();
			Thread.sleep(1000);
		}
	}

	public void assertSerializeSpeed() {
		Integer numVehicleNotifications = 5;
		Integer numOperationSchedules = 5;
		Integer numReservations = 5;
		Integer numUsers = 5;
		Integer numPassengerRecords = 5;

		LocalData ld = new LocalData();
		for (VehicleNotificationStatus vns : new VehicleNotificationStatus[] {
				VehicleNotificationStatus.UNHANDLED,
				VehicleNotificationStatus.OPERATION_SCHEDULE_RECEIVED,
				VehicleNotificationStatus.REPLIED, }) {
			for (Integer i = 0; i < numVehicleNotifications; ++i) {
				VehicleNotification vn = new VehicleNotification();
				vn.setId(i + 100);
				ld.vehicleNotifications.put(vns, vn);
			}
		}
		for (Integer l = 0; l < numOperationSchedules; ++l) {
			OperationSchedule os = new OperationSchedule();
			os.setId(l);
			for (Integer n = 0; n < numReservations; ++n) {
				Reservation r = new Reservation();
				r.setId(n);

				List<Reservation> ra = os.getReservationsAsArrival();
				ra.add(r);
				os.setReservationsAsArrival(ra);

				List<Reservation> rd = os.getReservationsAsDeparture();
				rd.add(r);
				os.setReservationsAsDeparture(rd);

				for (Integer o = 0; o < numUsers; ++o) {
					User u = new User();
					u.setId(o - 100);
					r.setUser(u);

					for (Integer p = 0; p < numPassengerRecords; ++p) {
						// arrival
						PassengerRecord a = new PassengerRecord();
						a.setId(p);
						List<PassengerRecord> as = Lists.newArrayList(a);
						as.add(a);
						u.setPassengerRecords(as);
						ld.passengerRecords.add(a);

						// departure
						PassengerRecord d = new PassengerRecord();
						d.setId(p + 100000);
						List<PassengerRecord> ds = Lists.newArrayList(d);
						ds.add(d);
						u.setPassengerRecords(ds);
						ld.passengerRecords.add(d);
					}
				}
			}
			ld.operationSchedules.add(os);
		}

		Stopwatch sw = new Stopwatch().start();
		byte[] ba = SerializationUtils.serialize(ld);
		sw.stop();
		
		Long elapsedMillis = sw.elapsedMillis();
		Log.i(TAG, "elapsed=" + elapsedMillis + "ms bytes=" + ba.length);
		ComparableAssert.assertLesser(60 * 1000L, elapsedMillis);
	}
}
