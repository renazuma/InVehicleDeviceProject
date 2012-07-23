package com.kogasoftware.odt.invehicledevice.test.unit.ui.phaseview;

import android.app.Activity;
import android.view.View;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.ui.phaseview.FinishPhaseView;
import static org.mockito.Mockito.*;

public class FinishPhaseViewTestCase extends
		EmptyActivityInstrumentationTestCase2 {

	Activity a;
	InVehicleDeviceService s;
	FinishPhaseView pv;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		a = getActivity();
		s = mock(InVehicleDeviceService.class);
		pv = new FinishPhaseView(a, s);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testEnterDrivePhaseEventで非表示() {
		testEnterFinishPhaseEventで表示();

		s.enterDrivePhase();
		getInstrumentation().waitForIdleSync();

		assertFalse(pv.isShown());
		assertNotSame(pv.getVisibility(), View.VISIBLE);
	}

	public void testEnterFinishPhaseEventで表示() {
		pv.setVisibility(View.GONE);

		s.enterFinishPhase();
		getInstrumentation().waitForIdleSync();

		assertTrue(pv.isShown());
		assertEquals(pv.getVisibility(), View.VISIBLE);
	}

	public void testEnterPlatformPhaseEventで非表示() {
		testEnterFinishPhaseEventで表示();

		s.enterPlatformPhase();
		getInstrumentation().waitForIdleSync();

		assertFalse(pv.isShown());
		assertNotSame(pv.getVisibility(), View.VISIBLE);
	}
}
