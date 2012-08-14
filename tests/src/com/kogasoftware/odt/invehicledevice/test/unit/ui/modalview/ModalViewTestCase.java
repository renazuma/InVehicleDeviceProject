package com.kogasoftware.odt.invehicledevice.test.unit.ui.modalview;

import static org.mockito.Mockito.mock;
import android.app.Activity;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.test.util.TestModalView;

public class ModalViewTestCase extends EmptyActivityInstrumentationTestCase2 {
	InVehicleDeviceService s;
	TestModalView mv;
	Activity a;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		a = getActivity();
		s = mock(InVehicleDeviceService.class);
		fail("TestModalViewのinflateにAnimationResourceが必要のため失敗する");

		mv = new TestModalView(a, s);
		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				getActivity().setContentView(mv);
			}
		});
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
