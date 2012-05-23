package com.kogasoftware.odt.invehicledevice.test.unit.ui.arrayadapter;

import java.util.LinkedList;
import java.util.List;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.Status;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Writer;
import com.kogasoftware.odt.invehicledevice.logic.datasource.DataSource;
import com.kogasoftware.odt.invehicledevice.logic.datasource.EmptyDataSource;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;
import com.kogasoftware.odt.invehicledevice.ui.arrayadapter.ReservationArrayAdapter;
import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.User;

public class ReservationArrayAdapterTestCase extends
		EmptyActivityInstrumentationTestCase2 {

	CommonLogic cl;
	ReservationArrayAdapter raa;
	StatusAccess sa;
	
	List<Reservation> getOnReservations = new LinkedList<Reservation>();
	List<Reservation> getOffReservations = new LinkedList<Reservation>();
	List<Reservation> cancelGetOnReservations = new LinkedList<Reservation>();
	List<Reservation> cancelGetOffReservations = new LinkedList<Reservation>();
	
	DataSource dataSource = new EmptyDataSource() {
		@Override
		public int getOffPassenger(OperationSchedule operationSchedule,
				Reservation reservation, PassengerRecord passengerRecord,
				WebAPICallback<PassengerRecord> callback) {
			getOffReservations.add(reservation);
			return 0;
		}

		@Override
		public int getOnPassenger(OperationSchedule operationSchedule,
				Reservation reservation, PassengerRecord passengerRecord,
				WebAPICallback<PassengerRecord> callback) {
			getOnReservations.add(reservation);
			return 0;
		}

		@Override
		public int cancelGetOffPassenger(OperationSchedule operationSchedule,
				Reservation reservation,
				WebAPICallback<PassengerRecord> callback) {
			cancelGetOffReservations.add(reservation);
			return 0;
		}

		@Override
		public int cancelGetOnPassenger(OperationSchedule operationSchedule,
				Reservation reservation,
				WebAPICallback<PassengerRecord> callback) {
			cancelGetOnReservations.add(reservation);
			return 0;
		}
	};

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		TestUtil.setDataSource(dataSource);
		sa = new StatusAccess(getActivity());
		cl = new CommonLogic(getActivity(), getActivityHandler(), sa);
		getOnReservations.clear();
		getOffReservations.clear();
		cancelGetOnReservations.clear();
		cancelGetOffReservations.clear();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if (cl != null) {
			cl.dispose();
		}
	}

	public void testReservationが表示される() throws Exception {
		final String userName0 = "上野駅前";
		final String userName1 = "御徒町駅前";
		sa.write(new Writer() {
			@Override
			public void write(Status status) {
				status.reservations.clear();
				OperationSchedule os1 = new OperationSchedule();
				os1.setId(0);
				OperationSchedule os2 = new OperationSchedule();
				os2.setId(100);
				status.remainingOperationSchedules.add(os1);
				status.remainingOperationSchedules.add(os2);
				{
					PassengerRecord pr = new PassengerRecord();
					Reservation r = new Reservation();
					User u = new User();
					u.setLastName(userName0);
					r.setUser(u);
					r.setPassengerRecord(pr);
					r.setDepartureScheduleId(os1.getId());
					r.setArrivalScheduleId(os2.getId());
					status.reservations.add(r);
				}

				{
					PassengerRecord pr = new PassengerRecord();
					Reservation r = new Reservation();
					User u = new User();
					u.setLastName(userName1);
					r.setUser(u);
					r.setPassengerRecord(pr);
					r.setDepartureScheduleId(os1.getId());
					r.setArrivalScheduleId(os2.getId());
					status.reservations.add(r);
				}
			}
		});

		raa = new ReservationArrayAdapter(getInstrumentation()
				.getTargetContext(), cl);
		getInstrumentation().waitForIdleSync();
		assertEquals(raa.getCount(), 2);
		final List<View> columns = new LinkedList<View>();
		final LinearLayout ll = new LinearLayout(getInstrumentation().getTargetContext());
		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				View v = raa.getView(0, null, null);
				columns.add(v);
				getActivity().setContentView(ll);
				ll.addView(v);
			}
		});
		solo.searchText(userName0);
		
		solo.clickOnView(columns.get(0));
		getInstrumentation().waitForIdleSync();
		assertEquals(1, getOnReservations.size());
		assertEquals(userName0, getOnReservations.get(0).getUser().get().getLastName());
		
		solo.clickOnView(columns.get(0));
		getInstrumentation().waitForIdleSync();
		assertEquals(1, cancelGetOnReservations.size());
		assertEquals(userName0, cancelGetOnReservations.get(0).getUser().get().getLastName());
		
		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				View v = raa.getView(1, null, null);
				columns.add(v);
				ll.addView(v);
			}
		});
		solo.searchText(userName1);
		
		solo.clickOnView(columns.get(1));
		getInstrumentation().waitForIdleSync();
		assertEquals(2, getOnReservations.size());
		assertEquals(userName1, getOnReservations.get(1).getUser().get().getLastName());
		
		solo.clickOnView(columns.get(1));
		getInstrumentation().waitForIdleSync();
		assertEquals(2, cancelGetOnReservations.size());
		assertEquals(userName1, cancelGetOnReservations.get(1).getUser().get().getLastName());

		
		solo.clickOnView(columns.get(0));
		getInstrumentation().waitForIdleSync();
		assertEquals(3, getOnReservations.size());
		assertEquals(userName0, getOnReservations.get(0).getUser().get().getLastName());
		
		solo.clickOnView(columns.get(1));
		getInstrumentation().waitForIdleSync();
		assertEquals(4, getOnReservations.size());
		assertEquals(userName1, getOnReservations.get(1).getUser().get().getLastName());
		
		solo.clickOnView(columns.get(0));
		getInstrumentation().waitForIdleSync();
		assertEquals(3, cancelGetOnReservations.size());
		assertEquals(userName0, cancelGetOnReservations.get(0).getUser().get().getLastName());
		
		solo.clickOnView(columns.get(1));
		getInstrumentation().waitForIdleSync();
		assertEquals(4, cancelGetOnReservations.size());
		assertEquals(userName1, cancelGetOnReservations.get(1).getUser().get().getLastName());
	}
}
