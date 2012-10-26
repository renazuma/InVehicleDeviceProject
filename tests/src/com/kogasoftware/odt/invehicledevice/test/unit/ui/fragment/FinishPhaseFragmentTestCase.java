package com.kogasoftware.odt.invehicledevice.test.unit.ui.fragment;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import android.app.Activity;
import android.view.View;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.EventDispatcher;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.logic.OperationScheduleLogic;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;

public class FinishPhaseFragmentTestCase extends
		EmptyActivityInstrumentationTestCase2 {

	Activity a;
	InVehicleDeviceService s;
	FinishPhaseFragment pv;
	OperationScheduleLogic osl;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		a = getActivity();
		s = mock(InVehicleDeviceService.class);
		when(s.getEventDispatcher()).thenReturn(new EventDispatcher());
		osl = new OperationScheduleLogic(s);
		pv = new FinishPhaseFragment(a, s);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testEnterDrivePhaseEventで非表示() {
		testEnterFinishPhaseEventで表示();

		osl.enterDrivePhase();
		getInstrumentation().waitForIdleSync();

		assertFalse(pv.isShown());
		assertNotSame(pv.getVisibility(), View.VISIBLE);
	}

	public void testEnterFinishPhaseEventで表示() {
		pv.setVisibility(View.GONE);

		osl.enterFinishPhase();
		getInstrumentation().waitForIdleSync();

		assertTrue(pv.isShown());
		assertEquals(pv.getVisibility(), View.VISIBLE);
	}

	public void testEnterPlatformPhaseEventで非表示() {
		testEnterFinishPhaseEventで表示();

		osl.enterPlatformPhase();
		getInstrumentation().waitForIdleSync();

		assertFalse(pv.isShown());
		assertNotSame(pv.getVisibility(), View.VISIBLE);
	}
}
