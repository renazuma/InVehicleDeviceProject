package com.kogasoftware.odt.invehicledevice.test.unit.ui.phaseview;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.ui.phaseview.PhaseView;
import static org.mockito.Mockito.*;

public class PhaseViewTestCase extends EmptyActivityInstrumentationTestCase2 {
	PhaseView pv;
	InVehicleDeviceService s;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		s = mock(InVehicleDeviceService.class);
		pv = new PhaseView(getActivity(), s);
		
		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				getActivity().setContentView(pv);
			}
		});
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void test1() {
		assertTrue(true);
	}
}
