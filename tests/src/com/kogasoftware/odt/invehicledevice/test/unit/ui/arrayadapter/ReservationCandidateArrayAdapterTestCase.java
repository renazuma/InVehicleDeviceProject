package com.kogasoftware.odt.invehicledevice.test.unit.ui.arrayadapter;

import java.util.LinkedList;

import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.ui.arrayadapter.ReservationCandidateArrayAdapter;
import com.kogasoftware.odt.webapi.model.ReservationCandidate;

public class ReservationCandidateArrayAdapterTestCase extends
		EmptyActivityInstrumentationTestCase2 {
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testReservationCandidateArrayAdapter_1() throws Exception {
		new ReservationCandidateArrayAdapter(getInstrumentation().getContext(),
				new LinkedList<ReservationCandidate>());
	}
}