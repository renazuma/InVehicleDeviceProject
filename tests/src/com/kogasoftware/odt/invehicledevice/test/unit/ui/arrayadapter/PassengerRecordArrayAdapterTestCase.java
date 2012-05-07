package com.kogasoftware.odt.invehicledevice.test.unit.ui.arrayadapter;

import java.util.ArrayList;
import java.util.List;

import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.ui.arrayadapter.OperationScheduleArrayAdapter;
import com.kogasoftware.odt.invehicledevice.ui.arrayadapter.PassengerRecordArrayAdapter;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.Platform;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.User;

public class PassengerRecordArrayAdapterTestCase extends
		EmptyActivityInstrumentationTestCase2 {

	CommonLogic cl;
	PassengerRecordArrayAdapter praa;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cl = newCommonLogic();
		praa = new PassengerRecordArrayAdapter(getInstrumentation()
				.getTargetContext(), cl);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if (cl != null) {
			cl.dispose();
		}
	}

	public void testPassengerRecordが表示される() throws Exception {
		praa = new PassengerRecordArrayAdapter(getInstrumentation()
				.getTargetContext(), cl);

		String userName0 = "上野駅前";
		String userName1 = "御徒町駅前";
		{
			PassengerRecord pr = new PassengerRecord();
			Reservation r = new Reservation();
			User u = new User();
			u.setLastName(userName0);
			r.setUser(u);
			pr.setReservation(r);
			praa.add(pr);
		}

		{
			PassengerRecord pr = new PassengerRecord();
			Reservation r = new Reservation();
			User u = new User();
			u.setLastName(userName1);
			r.setUser(u);
			pr.setReservation(r);
			praa.add(pr);
		}

		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				getActivity().setContentView(praa.getView(0, null, null));
			}
		});
		solo.searchText(userName0);

		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				getActivity().setContentView(praa.getView(1, null, null));
			}
		});
		solo.searchText(userName1);
	}
}
