package com.kogasoftware.odt.invehicledevice.test.unit.ui.fragment;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.VehicleNotification;

public class OperationScheduleChangedFragmentTestCase extends
		EmptyActivityInstrumentationTestCase2 {
	InVehicleDeviceService s;
	LocalStorage sa;
	ScheduleChangedModalView mv;
	ScheduleModalView smv;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		s = mock(InVehicleDeviceService.class);
		when(s.getCurrentOperationSchedule()).thenReturn(
				Optional.<OperationSchedule> absent());
		smv = new ScheduleModalView(a, s);
		sa = new LocalStorage(a);
		mv = new ScheduleChangedModalView(a, s, smv);

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

	public void testShow() throws Exception {
		VehicleNotification vn = new VehicleNotification();
		String body = "こんにちは";
		vn.setBody(body);
		assertShow(Lists.newArrayList(vn));
		assertTrue(solo.searchText(body));
	}

	public void testAddMessage() throws Exception {
		final VehicleNotification vn0 = new VehicleNotification();
		final VehicleNotification vn1 = new VehicleNotification();
		final VehicleNotification vn2 = new VehicleNotification();
		String body0 = "おはよう";
		String body1 = "こんにちは";
		String body2 = "こんばんは";
		vn0.setBody(body0);
		vn1.setBody(body1);
		vn2.setBody(body2);
		assertShow(Lists.newArrayList(vn0, vn1));

		assertTrue(solo.searchText(body0, true));
		assertTrue(solo.searchText(body1, true));
		assertFalse(solo.searchText(body2, true));

		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				mv.onMergeOperationSchedules(Lists.newArrayList(vn2));
			}
		});

		assertTrue(solo.searchText(body0, true));
		assertTrue(solo.searchText(body1, true));
		assertTrue(solo.searchText(body2, true));
	}

	protected void assertShow(final List<VehicleNotification> vns)
			throws Exception {
		assertFalse(mv.isShown());
		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				mv.onMergeOperationSchedules(vns);
			}
		});
		TestUtil.assertShow(mv);
	}

	public void testBack() throws Exception {
		VehicleNotification vn = new VehicleNotification();
		assertShow(Lists.newArrayList(vn));
		solo.clickOnView(solo.getView(R.id.schedule_changed_close_button));
		TestUtil.assertHide(mv);
	}

	public void testScheduleConfirm() throws Exception {
		VehicleNotification vn = new VehicleNotification();
		assertShow(Lists.newArrayList(vn));
		solo.clickOnView(solo.getView(R.id.schedule_confirm_button));
		TestUtil.assertHide(mv);
		TestUtil.assertShow(smv);
	}
}
