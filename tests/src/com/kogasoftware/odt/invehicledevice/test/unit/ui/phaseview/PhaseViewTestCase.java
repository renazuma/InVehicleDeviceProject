package com.kogasoftware.odt.invehicledevice.test.unit.ui.phaseview;

import android.content.Context;
import android.view.LayoutInflater;

import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.event.CommonLogicLoadCompleteEvent;
import com.kogasoftware.odt.invehicledevice.test.R;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.test.util.TestPhaseView;

public class PhaseViewTestCase extends EmptyActivityInstrumentationTestCase2 {
	CommonLogic cl;
	TestPhaseView pv;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cl = newCommonLogic();

		LayoutInflater li = (LayoutInflater) getInstrumentation().getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		pv = (TestPhaseView) li.inflate(R.layout.test_phase_view, null);
		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				getActivity().setContentView(pv);
			}
		});
		cl.registerEventListener(pv);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if (cl != null) {
			cl.dispose();
		}
	}

	public void testCommonLogicLoadCompleteEventでCommonLogicを取得() {
		assertNotSame(cl, pv.getCommonLogic());
		cl.postEvent(new CommonLogicLoadCompleteEvent(cl));
		getInstrumentation().waitForIdleSync();
		assertEquals(cl, pv.getCommonLogic());
	}
}
