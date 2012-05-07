package com.kogasoftware.odt.invehicledevice.test.unit.ui.arrayadapter;

import java.util.LinkedList;

import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.ui.arrayadapter.ReservationCandidateArrayAdapter;
import com.kogasoftware.odt.webapi.model.ReservationCandidate;

public class ReservationCandidateArrayAdapterTestCase extends
		EmptyActivityInstrumentationTestCase2 {

	ReservationCandidateArrayAdapter rcaa;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		rcaa = new ReservationCandidateArrayAdapter(getInstrumentation()
				.getContext(), new LinkedList<ReservationCandidate>());
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void xtestReservationCandidateが表示される() throws Exception {
		fail("stub!");
	}
}
