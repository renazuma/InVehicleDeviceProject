package com.kogasoftware.odt.invehicledevice.test.unit.ui.arrayadapter;

import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.Status;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Writer;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.ui.arrayadapter.ReservationArrayAdapter;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.User;

public class ReservationArrayAdapterTestCase extends
		EmptyActivityInstrumentationTestCase2 {

	CommonLogic cl;
	ReservationArrayAdapter raa;
	StatusAccess sa;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		sa = new StatusAccess(getActivity());
		cl = new CommonLogic(getActivity(), getActivityHandler(), sa);
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
		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				getActivity().setContentView(raa.getView(0, null, null));
			}
		});
		solo.searchText(userName0);

		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				getActivity().setContentView(raa.getView(1, null, null));
			}
		});
		solo.searchText(userName1);
	}
}
