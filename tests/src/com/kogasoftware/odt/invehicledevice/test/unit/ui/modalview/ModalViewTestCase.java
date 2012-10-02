package com.kogasoftware.odt.invehicledevice.test.unit.ui.modalview;

import static org.mockito.Mockito.mock;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.test.util.TestModalView;
import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;

public class ModalViewTestCase extends EmptyActivityInstrumentationTestCase2 {
	InVehicleDeviceService s;
	TestModalView mv;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		s = mock(InVehicleDeviceService.class);
		mv = new TestModalView(a, s);
		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				a.setContentView(mv);
			}
		});
	}

	public void testShow() throws Exception {
		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				mv.show();
			}
		});
		TestUtil.assertShow(mv);

		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				mv.hide();
			}
		});
		TestUtil.assertHide(mv);

		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				mv.show();
			}
		});
		TestUtil.assertShow(mv);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
