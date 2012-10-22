package com.kogasoftware.odt.invehicledevice.test.unit.ui.arrayadapter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.ui.arrayadapter.ReservationCandidateArrayAdapter;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Platform;
import com.kogasoftware.odt.invehicledevice.apiclient.model.ReservationCandidate;

public class ReservationCandidateArrayAdapterTestCase extends
		EmptyActivityInstrumentationTestCase2 {

	ReservationCandidateArrayAdapter rcaa;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		rcaa = new ReservationCandidateArrayAdapter(getInstrumentation()
				.getTargetContext(), new LinkedList<ReservationCandidate>());
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testReservationCandidateのPlatformが表示される() throws Exception {
		String dpn0 = "出発プラットフォームA";
		String apn0 = "到着プラットフォームA";
		String dpn1 = "出発プラットフォームB";
		String apn1 = "到着プラットフォームB";
		List<ReservationCandidate> rcs = new ArrayList<ReservationCandidate>();
		{
			ReservationCandidate rc = new ReservationCandidate();
			Platform dp = new Platform();
			dp.setName(dpn0);
			rc.setDeparturePlatform(dp);
			Platform ap = new Platform();
			ap.setName(apn0);
			rc.setArrivalPlatform(ap);
			rcs.add(rc);
		}
		{
			ReservationCandidate rc = new ReservationCandidate();
			Platform dp = new Platform();
			dp.setName(dpn1);
			rc.setDeparturePlatform(dp);
			Platform ap = new Platform();
			ap.setName(apn1);
			rc.setArrivalPlatform(ap);
			rcs.add(rc);
		}

		rcaa = new ReservationCandidateArrayAdapter(getInstrumentation()
				.getTargetContext(), rcs);

		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				getActivity().setContentView(rcaa.getView(0, null, null));
			}
		});
		assertTrue(solo.searchText(dpn0));
		assertTrue(solo.searchText(apn0));
		
		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				getActivity().setContentView(rcaa.getView(1, null, null));
			}
		});
		assertTrue(solo.searchText(dpn1));
		assertTrue(solo.searchText(apn1));
	}
}
