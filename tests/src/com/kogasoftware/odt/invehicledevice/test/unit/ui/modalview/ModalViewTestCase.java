package com.kogasoftware.odt.invehicledevice.test.unit.ui.modalview;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.test.R;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.test.util.TestModalView;
import static org.mockito.Mockito.*;

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

		LayoutInflater li = (LayoutInflater) getInstrumentation().getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mv = (TestModalView) li.inflate(R.layout.test_modal_view, null);
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
