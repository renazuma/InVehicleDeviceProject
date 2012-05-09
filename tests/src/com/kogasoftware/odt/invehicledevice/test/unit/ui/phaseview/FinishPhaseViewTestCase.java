package com.kogasoftware.odt.invehicledevice.test.unit.ui.phaseview;

import android.view.View;

import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.event.CommonLogicLoadCompleteEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.EnterDrivePhaseEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.EnterFinishPhaseEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.EnterPlatformPhaseEvent;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.ui.phaseview.FinishPhaseView;

public class FinishPhaseViewTestCase extends
		EmptyActivityInstrumentationTestCase2 {

	CommonLogic cl;
	FinishPhaseView pv;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cl = newCommonLogic();
		pv = (FinishPhaseView) inflateAndAddTestLayout(com.kogasoftware.odt.invehicledevice.test.R.layout.test_finish_phase_view);
		cl.registerEventListener(pv);
		pv.setCommonLogic(new CommonLogicLoadCompleteEvent(cl));
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if (cl != null) {
			cl.dispose();
		}
	}

	public void testEnterDrivePhaseEventで非表示() {
		testEnterFinishPhaseEventで表示();

		cl.postEvent(new EnterDrivePhaseEvent());
		getInstrumentation().waitForIdleSync();

		assertFalse(pv.isShown());
		assertNotSame(pv.getVisibility(), View.VISIBLE);
	}

	public void testEnterFinishPhaseEventで表示() {
		pv.setVisibility(View.GONE);

		cl.postEvent(new EnterFinishPhaseEvent());
		getInstrumentation().waitForIdleSync();

		assertTrue(pv.isShown());
		assertEquals(pv.getVisibility(), View.VISIBLE);
	}

	public void testEnterPlatformPhaseEventで非表示() {
		testEnterFinishPhaseEventで表示();

		cl.postEvent(new EnterPlatformPhaseEvent());
		getInstrumentation().waitForIdleSync();

		assertFalse(pv.isShown());
		assertNotSame(pv.getVisibility(), View.VISIBLE);
	}
}
