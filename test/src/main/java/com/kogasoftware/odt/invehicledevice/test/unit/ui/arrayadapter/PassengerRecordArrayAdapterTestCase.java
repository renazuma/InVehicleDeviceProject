package com.kogasoftware.odt.invehicledevice.test.unit.ui.arrayadapter;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.widget.ListView;

import com.kogasoftware.odt.invehicledevice.apiclient.InVehicleDeviceApiClient;
import com.kogasoftware.odt.invehicledevice.apiclient.EmptyInVehicleDeviceApiClient;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage.Writer;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.logic.OperationScheduleLogic;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;
import com.kogasoftware.odt.invehicledevice.ui.arrayadapter.PassengerRecordArrayAdapter;
import com.kogasoftware.odt.apiclient.ApiClientCallback;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationRecord;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Reservation;
import com.kogasoftware.odt.invehicledevice.apiclient.model.User;
import com.kogasoftware.odt.invehicledevice.empty.EmptyRunnable;

public class PassengerRecordArrayAdapterTestCase extends
		EmptyActivityInstrumentationTestCase2 {

	private static final String TAG = PassengerRecordArrayAdapterTestCase.class
			.getSimpleName();
	InVehicleDeviceService s;
	OperationScheduleLogic osl;
	PassengerRecordArrayAdapter aa;
	LocalStorage sa;
	CountDownLatch sccdl;
	ServiceConnection sc = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder binder) {
			s = ((InVehicleDeviceService.LocalBinder) binder).getService();
			sccdl.countDown();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
		}
	};

	BlockingQueue<PassengerRecord> getOnPassengerRecords = new LinkedBlockingQueue<PassengerRecord>();
	BlockingQueue<PassengerRecord> getOffPassengerRecords = new LinkedBlockingQueue<PassengerRecord>();
	BlockingQueue<PassengerRecord> cancelGetOnPassengerRecords = new LinkedBlockingQueue<PassengerRecord>();
	BlockingQueue<PassengerRecord> cancelGetOffPassengerRecords = new LinkedBlockingQueue<PassengerRecord>();

	InVehicleDeviceApiClient apiClient = new EmptyInVehicleDeviceApiClient() {
		@Override
		public int getOffPassenger(OperationSchedule operationSchedule,
				Reservation reservation, User user,
				PassengerRecord passengerRecord,
				ApiClientCallback<Void> callback) {
			getOffPassengerRecords.add(passengerRecord);
			return 0;
		}

		@Override
		public int getOnPassenger(OperationSchedule operationSchedule,
				Reservation reservation, User user,
				PassengerRecord passengerRecord,
				ApiClientCallback<Void> callback) {
			getOnPassengerRecords.add(passengerRecord);
			return 0;
		}

		@Override
		public int cancelGetOffPassenger(OperationSchedule operationSchedule,
				Reservation reservation, User user,
				ApiClientCallback<Void> callback) {
			cancelGetOffPassengerRecords.add(reservation.getPassengerRecords()
					.get(0));
			return 0;
		}

		@Override
		public int cancelGetOnPassenger(OperationSchedule operationSchedule,
				Reservation reservation, User user,
				ApiClientCallback<Void> callback) {
			cancelGetOnPassengerRecords.add(reservation.getPassengerRecords()
					.get(0));
			return 0;
		}
	};

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		TestUtil.setApiClient(apiClient);
		a = getActivity();
		TestUtil.disableAutoStart(a);
		sccdl = new CountDownLatch(1);
		a.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				a.bindService(new Intent(a, InVehicleDeviceService.class), sc,
						Context.BIND_AUTO_CREATE);
			}
		});
		sccdl.await(20, TimeUnit.SECONDS);
		for (Integer i = 0; i < 500; ++i) {
			Thread.sleep(1000);
			if (s.isOperationInitialized()) {
				break;
			}
		}
		if (!s.isOperationInitialized()) {
			fail();
		}

		sa = s.getLocalStorage();

		getOnPassengerRecords.clear();
		getOffPassengerRecords.clear();
		cancelGetOnPassengerRecords.clear();
		cancelGetOffPassengerRecords.clear();
		osl = new OperationScheduleLogic(s);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if (s != null) {
			s.exit();
		}
		if (a != null) {
			a.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					a.unbindService(sc);
				}
			});
		}
	}

	protected void sync() throws Exception {
		Log.i(TAG, "sync start");
		getInstrumentation().waitForIdleSync();
		Log.i(TAG, "sync complete");
	}

	public void testPassengerRecordGetOnAndOff() throws Exception {
		final String userName0 = "上野駅前";
		final String userName1 = "御徒町駅前";
		final Integer T = 2000;
		final List<OperationSchedule> oss = new LinkedList<OperationSchedule>();
		final List<PassengerRecord> prs = new LinkedList<PassengerRecord>();

		OperationRecord finished = new OperationRecord();
		finished.setArrivedAt(new Date());
		finished.setDepartedAt(new Date());
		OperationRecord remaining = new OperationRecord();
		final OperationSchedule os0 = new OperationSchedule();
		os0.setId(200);
		os0.setOperationRecord(finished);
		final OperationSchedule os1 = new OperationSchedule();
		os1.setId(50);
		os1.setOperationRecord(remaining);
		final OperationSchedule os2 = new OperationSchedule();
		os2.setId(100);
		os2.setOperationRecord(remaining);

		sa.withWriteLock(new Writer() {
			@Override
			public void write(LocalData status) {
				status.operation.passengerRecords.clear();
				status.operation.operationSchedules.clear();
				status.operation.operationSchedules.add(os0);
				status.operation.operationSchedules.add(os1);
				status.operation.operationSchedules.add(os2);
				{
					PassengerRecord pr = new PassengerRecord();
					Reservation r = new Reservation();
					User u = new User();
					u.setLastName(userName0);
					r.setDepartureScheduleId(os1.getId());
					r.setArrivalScheduleId(os2.getId());
					pr.setUser(u);
					pr.setReservation(r);
					status.operation.passengerRecords.add(pr);
				}

				{
					PassengerRecord pr = new PassengerRecord();
					pr.setGetOnTime(new Date());
					pr.setDepartureOperationScheduleId(os0.getId());
					Reservation r = new Reservation();
					User u = new User();
					u.setLastName(userName1);
					pr.setUser(u);
					pr.setReservation(r);
					r.setDepartureScheduleId(os0.getId());
					r.setArrivalScheduleId(os1.getId());
					status.operation.passengerRecords.add(pr);
				}

				oss.addAll(status.operation.operationSchedules);
				prs.addAll(status.operation.passengerRecords);
			}
		});
		osl.arrive(os0, new EmptyRunnable());

		aa = new PassengerRecordArrayAdapter(getInstrumentation().getContext(),
				s, a.getSupportFragmentManager(), os0, prs);

		sync();
		assertEquals(aa.getCount(), 2);
		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				ListView lv = new ListView(getInstrumentation()
						.getTargetContext());
				lv.setAdapter(aa);
				a.setContentView(lv);
			}
		});
		assertTrue(solo.searchText(userName0, true));

		PassengerRecord r;
		solo.clickOnText(userName0);
		r = getOnPassengerRecords.poll(T, TimeUnit.SECONDS);
		assertNotNull(r);
		assertEquals(userName0, r.getUser().get().getLastName());

		solo.clickOnText(userName0);
		r = cancelGetOnPassengerRecords.poll(T, TimeUnit.SECONDS);
		assertNotNull(r);
		assertEquals(userName0, r.getUser().get().getLastName());

		assertTrue(solo.searchText(userName1, true));

		solo.clickOnText(userName1);
		r = getOffPassengerRecords.poll(T, TimeUnit.SECONDS);
		assertNotNull(r);
		assertEquals(userName1, r.getUser().get().getLastName());

		solo.clickOnText(userName1);
		r = cancelGetOffPassengerRecords.poll(T, TimeUnit.SECONDS);
		assertNotNull(r);
		assertEquals(userName1, r.getUser().get().getLastName());

		solo.clickOnText(userName0);
		r = getOnPassengerRecords.poll(T, TimeUnit.SECONDS);
		assertNotNull(r);
		assertEquals(userName0, r.getUser().get().getLastName());

		solo.clickOnText(userName1);
		r = getOffPassengerRecords.poll(T, TimeUnit.SECONDS);
		assertNotNull(r);
		assertEquals(userName1, r.getUser().get().getLastName());

		solo.clickOnText(userName0);
		r = cancelGetOnPassengerRecords.poll(T, TimeUnit.SECONDS);
		assertNotNull(r);
		assertEquals(userName0, r.getUser().get().getLastName());

		solo.clickOnText(userName1);
		r = cancelGetOffPassengerRecords.poll(T, TimeUnit.SECONDS);
		assertNotNull(r);
		assertEquals(userName1, r.getUser().get().getLastName());

		// 次の乗降場へ移動
		osl.depart(os0, new EmptyRunnable());
		osl.arrive(os1, new EmptyRunnable());

		Thread.sleep(2000);
		aa = new PassengerRecordArrayAdapter(getInstrumentation().getContext(),
				s, a.getSupportFragmentManager(), os1, prs);
		sync();
		assertEquals(1, aa.getCount());
		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				ListView lv = new ListView(getInstrumentation()
						.getTargetContext());
				lv.setAdapter(aa);
				a.setContentView(lv);
			}
		});
		assertTrue(solo.searchText(userName0, true));
		assertFalse(solo.searchText(userName1, true));

		solo.clickOnText(userName0);
		r = getOffPassengerRecords.poll(T, TimeUnit.SECONDS);
		assertNotNull(r);
		assertEquals(userName0, r.getUser().get().getLastName());

		solo.clickOnText(userName0);
		r = cancelGetOffPassengerRecords.poll(T, TimeUnit.SECONDS);
		assertNotNull(r);
		assertEquals(userName0, r.getUser().get().getLastName());

	}
}
