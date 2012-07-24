package com.kogasoftware.odt.invehicledevice.test.unit.ui.arrayadapter;

import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import android.util.Log;
import android.widget.ListView;

import com.kogasoftware.odt.invehicledevice.datasource.DataSource;
import com.kogasoftware.odt.invehicledevice.datasource.EmptyDataSource;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource.Writer;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;
import com.kogasoftware.odt.invehicledevice.ui.arrayadapter.PassengerRecordArrayAdapter;
import com.kogasoftware.odt.invehicledevice.ui.modalview.MemoModalView;
import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.User;
import static org.mockito.Mockito.*;

public class PassengerRecordArrayAdapterTestCase extends
		EmptyActivityInstrumentationTestCase2 {

	private static final String TAG = PassengerRecordArrayAdapterTestCase.class.getSimpleName();
	InVehicleDeviceService s;
	MemoModalView mmv;
	PassengerRecordArrayAdapter raa;
	LocalDataSource sa;
	
	BlockingQueue<PassengerRecord> getOnPassengerRecords = new LinkedBlockingQueue<PassengerRecord>();
	BlockingQueue<PassengerRecord> getOffPassengerRecords = new LinkedBlockingQueue<PassengerRecord>();
	BlockingQueue<PassengerRecord> cancelGetOnPassengerRecords = new LinkedBlockingQueue<PassengerRecord>();
	BlockingQueue<PassengerRecord> cancelGetOffPassengerRecords = new LinkedBlockingQueue<PassengerRecord>();
	
	DataSource dataSource = new EmptyDataSource() {
		@Override
		public int getOffPassenger(OperationSchedule operationSchedule,
				Reservation reservation, PassengerRecord passengerRecord,
				WebAPICallback<PassengerRecord> callback) {
			getOffPassengerRecords.add(passengerRecord);
			return 0;
		}

		@Override
		public int getOnPassenger(OperationSchedule operationSchedule,
				Reservation reservation, PassengerRecord passengerRecord,
				WebAPICallback<PassengerRecord> callback) {
			getOnPassengerRecords.add(passengerRecord);
			return 0;
		}

		public int cancelGetOffPassenger(OperationSchedule operationSchedule,
				PassengerRecord passengerRecord,
				WebAPICallback<PassengerRecord> callback) {
			cancelGetOffPassengerRecords.add(passengerRecord);
			return 0;
		}

		public int cancelGetOnPassenger(OperationSchedule operationSchedule,
				PassengerRecord passengerRecord,
				WebAPICallback<PassengerRecord> callback) {
			cancelGetOnPassengerRecords.add(passengerRecord);
			return 0;
		}
	};

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		TestUtil.setDataSource(dataSource);
		s = mock(InVehicleDeviceService.class);
		mmv = mock(MemoModalView.class);
		sa = new LocalDataSource(getActivity());
		getOnPassengerRecords.clear();
		getOffPassengerRecords.clear();
		cancelGetOnPassengerRecords.clear();
		cancelGetOffPassengerRecords.clear();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	protected void sync() throws Exception {
		Log.i(TAG, "sync start");
		getInstrumentation().waitForIdleSync();
		Log.i(TAG, "sync complete");
	}

	public void testPassengerRecordGetOn() throws Exception {
		final String userName0 = "上野駅前";
		final String userName1 = "御徒町駅前";
		final Integer T = 300;
		sa.withWriteLock(new Writer() {
			@Override
			public void write(LocalData status) {
				status.passengerRecords.clear();
				OperationSchedule os0 = new OperationSchedule();
				os0.setId(200);
				OperationSchedule os1 = new OperationSchedule();
				os1.setId(0);
				OperationSchedule os2 = new OperationSchedule();
				os2.setId(100);
				status.finishedOperationSchedules.clear();
				status.finishedOperationSchedules.add(os0);
				status.remainingOperationSchedules.clear();
				status.remainingOperationSchedules.add(os1);
				status.remainingOperationSchedules.add(os2);
				{
					PassengerRecord pr = new PassengerRecord();
					Reservation r = new Reservation();
					User u = new User();
					u.setLastName(userName0);
					r.setDepartureScheduleId(os1.getId());
					r.setArrivalScheduleId(os2.getId());
					pr.setUser(u);
					pr.setReservation(r);
					status.passengerRecords.add(pr);
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
					status.passengerRecords.add(pr);
				}
			}
		});

		raa = new PassengerRecordArrayAdapter(s, mmv);
		sync();
		assertEquals(raa.getCount(), 2);
		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				ListView lv = new ListView(getInstrumentation().getTargetContext());
				lv.setAdapter(raa);
				getActivity().setContentView(lv);
			}
		});
		assertTrue(solo.searchText(userName0, true));
		
		PassengerRecord r;
		solo.clickOnText(userName0);
		r = getOnPassengerRecords.poll(T, TimeUnit.SECONDS);
		assertEquals(userName0, r.getUser().get().getLastName());
		
		solo.clickOnText(userName0);
		r = cancelGetOnPassengerRecords.poll(T, TimeUnit.SECONDS);
		assertEquals(userName0, r.getUser().get().getLastName());
		
		assertTrue(solo.searchText(userName1, true));
		
		solo.clickOnText(userName1);
		r = getOffPassengerRecords.poll(T, TimeUnit.SECONDS);
		assertEquals(userName1, r.getUser().get().getLastName());
		
		solo.clickOnText(userName1);
		r = cancelGetOffPassengerRecords.poll(T, TimeUnit.SECONDS);
		assertEquals(userName1, r.getUser().get().getLastName());
		
		
		solo.clickOnText(userName0);
		r = getOnPassengerRecords.poll(T, TimeUnit.SECONDS);
		assertEquals(userName0, r.getUser().get().getLastName());
		
		solo.clickOnText(userName1);
		r = getOffPassengerRecords.poll(T, TimeUnit.SECONDS);
		assertEquals(userName1, r.getUser().get().getLastName());
		
		solo.clickOnText(userName0);
		r = cancelGetOnPassengerRecords.poll(T, TimeUnit.SECONDS);
		assertEquals(userName0, r.getUser().get().getLastName());
		
		solo.clickOnText(userName1);
		r = cancelGetOffPassengerRecords.poll(T, TimeUnit.SECONDS);
		assertEquals(userName1, r.getUser().get().getLastName());
	}
}
