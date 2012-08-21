package com.kogasoftware.odt.invehicledevice.test.unit.ui.modalview;

import static org.mockito.Mockito.mock;
import android.app.Activity;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.ui.modalview.PlatformMemoModalView;

public class PlatformMemoModalViewTestCase extends EmptyActivityInstrumentationTestCase2 {
	InVehicleDeviceService s;
	PlatformMemoModalView mv;
	Activity a;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		a = getActivity();
		s = mock(InVehicleDeviceService.class);
		mv = new PlatformMemoModalView(a, s);
		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				getActivity().setContentView(mv);
			}
		});
	}
	
	public void test1() {
		fail("stub!");
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
