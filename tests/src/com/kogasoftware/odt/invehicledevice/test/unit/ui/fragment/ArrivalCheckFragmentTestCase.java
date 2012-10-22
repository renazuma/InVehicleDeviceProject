package com.kogasoftware.odt.invehicledevice.test.unit.ui.fragment;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Platform;

public class ArrivalCheckFragmentTestCase extends
		EmptyActivityInstrumentationTestCase2 {
	InVehicleDeviceService s;
	OperationSchedule os;
	ArrivalCheckModalView mv;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		os = new OperationSchedule();
		Platform p = new Platform();
		p.setName("乗降場X");
		os.setPlatform(p);

		s = mock(InVehicleDeviceService.class);
		when(s.getCurrentOperationSchedule()).thenReturn(Optional.of(os));

		mv = new ArrivalCheckModalView(a, s);

		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				a.setContentView(mv);
			}
		});
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testShow1() throws Exception {
		assertShow("乗降場K");
	}

	public void testShow2() throws Exception {
		assertShow("乗降場L");
	}

	public void testShow3() throws Exception {
		assertShow("乗降場M");
	}

	protected void assertShow(String platformName) throws Exception {
		for (Platform p : os.getPlatform().asSet()) {
			p.setName(platformName);
		}

		assertFalse(mv.isShown());

		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				mv.show();
			}
		});

		TestUtil.assertShow(mv);
		assertTrue(solo.searchText(platformName, true));
	}

	public void testArrive() throws Exception {
		assertShow("のりおりば");
		solo.clickOnView(solo.getView(R.id.arrival_button));
		TestUtil.assertHide(mv);
		verify(s, times(1)).enterPlatformPhase();
	}

	public void testBack() throws Exception {
		assertShow("のりおりば");
		solo.clickOnView(solo.getView(R.id.arrival_check_close_button));
		TestUtil.assertHide(mv);
		verify(s, never()).enterPlatformPhase();
	}
}
