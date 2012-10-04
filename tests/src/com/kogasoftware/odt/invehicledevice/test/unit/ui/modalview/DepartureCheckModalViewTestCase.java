package com.kogasoftware.odt.invehicledevice.test.unit.ui.modalview;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;
import com.kogasoftware.odt.invehicledevice.ui.modalview.DepartureCheckModalView;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.Platform;
import com.kogasoftware.odt.webapi.model.User;

public class DepartureCheckModalViewTestCase extends
		EmptyActivityInstrumentationTestCase2 {
	InVehicleDeviceService s;
	OperationSchedule os1;
	OperationSchedule os2;
	DepartureCheckModalView mv;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		os1 = new OperationSchedule();
		Platform p = new Platform();
		p.setName("乗降場X");
		os1.setPlatform(p);

		s = mock(InVehicleDeviceService.class);
		when(s.getCurrentOperationSchedule()).thenReturn(Optional.of(os1));
		when(s.getOperationSchedules())
				.thenReturn(Lists.newArrayList(os1, os2));
		when(s.getRemainingOperationSchedules()).thenReturn(
				Lists.newArrayList(os1, os2));

		mv = new DepartureCheckModalView(a, s);

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

	private void assertShow() throws Exception {
		assertFalse(mv.isShown());

		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				mv.show();
			}
		});

		TestUtil.assertShow(mv);
	}

	public void testShow() throws Exception {
		assertShow();
	}

	public void testDepart_NoError() throws Exception {
		assertShow();
		assertFalse(solo.getView(R.id.departure_with_error_button).isEnabled());
		solo.clickOnView(solo.getView(R.id.departure_button));
		TestUtil.assertHide(mv);
		verify(s, times(1)).enterDrivePhase();
		verify(s, never()).enterFinishPhase();
	}

	public void testDepart_HasNoError_Finish() throws Exception {
		when(s.getRemainingOperationSchedules()).thenReturn(
				Lists.newArrayList(os2));

		assertShow();
		assertFalse(solo.getView(R.id.departure_with_error_button).isEnabled());
		solo.clickOnView(solo.getView(R.id.departure_button));
		TestUtil.assertHide(mv);
		verify(s, never()).enterDrivePhase();
		verify(s, times(1)).enterFinishPhase();
	}

	private void assertDepartHasError(
			List<PassengerRecord> noGettingOffPassengerRecords,
			List<PassengerRecord> noGettingOnPassengerRecords,
			List<PassengerRecord> noPaymentPassengerRecords) throws Exception {

		when(s.getNoGettingOffPassengerRecords()).thenReturn(
				noGettingOffPassengerRecords);
		when(s.getNoGettingOnPassengerRecords()).thenReturn(
				noGettingOnPassengerRecords);
		when(s.getNoPaymentPassengerRecords()).thenReturn(
				noPaymentPassengerRecords);

		assertShow();

		for (PassengerRecord pr : Iterables.concat(
				noGettingOffPassengerRecords, noGettingOnPassengerRecords,
				noPaymentPassengerRecords)) {
			for (User u : pr.getUser().asSet()) {
				if (!u.getFirstName().isEmpty()) {
					assertTrue(solo.searchText(u.getFirstName(), true));
				}
				if (!u.getLastName().isEmpty()) {
					assertTrue(solo.searchText(u.getLastName(), true));
				}
			}
		}

		assertFalse(solo.getView(R.id.departure_button).isShown());
		solo.clickOnView(solo.getView(R.id.departure_with_error_button));
		TestUtil.assertHide(mv);
		if (s.getRemainingOperationSchedules().size() > 1) {
			verify(s, times(1)).enterDrivePhase();
			verify(s, never()).enterFinishPhase();
		} else {
			verify(s, times(1)).enterFinishPhase();
			verify(s, never()).enterDrivePhase();
		}
	}

	public void testDepart_HasNoGettingOffError() throws Exception {
		String firstName = "鈴木";
		String lastName = "三郎";

		PassengerRecord pr1 = new PassengerRecord();
		User u1 = new User();
		u1.setFirstName(firstName);
		u1.setLastName(lastName);
		pr1.setUser(u1);

		PassengerRecord pr2 = new PassengerRecord();
		User u2 = new User();
		u2.setFirstName(firstName);
		u2.setLastName(lastName);
		pr2.setUser(u2);

		assertDepartHasError(Lists.newArrayList(pr1, pr2),
				new LinkedList<PassengerRecord>(),
				new LinkedList<PassengerRecord>());
	}

	public void testDepart_HasNoGettingOffError_Finish() throws Exception {
		String firstName = "鈴木";
		String lastName = "三郎";

		PassengerRecord pr1 = new PassengerRecord();
		User u1 = new User();
		u1.setFirstName(firstName);
		u1.setLastName(lastName);
		pr1.setUser(u1);

		PassengerRecord pr2 = new PassengerRecord();
		User u2 = new User();
		u2.setFirstName(firstName);
		u2.setLastName(lastName);
		pr2.setUser(u2);

		when(s.getRemainingOperationSchedules()).thenReturn(
				Lists.newArrayList(os2));

		assertDepartHasError(Lists.newArrayList(pr1, pr2),
				new LinkedList<PassengerRecord>(),
				new LinkedList<PassengerRecord>());
	}

	public void testDepart_HasNoPaymentError() throws Exception {
		String firstName = "鈴木";
		String lastName = "三郎";

		PassengerRecord pr1 = new PassengerRecord();
		User u1 = new User();
		u1.setFirstName(firstName);
		u1.setLastName(lastName);
		pr1.setUser(u1);

		PassengerRecord pr2 = new PassengerRecord();
		User u2 = new User();
		u2.setFirstName(firstName);
		u2.setLastName(lastName);
		pr2.setUser(u2);

		assertDepartHasError(new LinkedList<PassengerRecord>(),
				new LinkedList<PassengerRecord>(), Lists.newArrayList(pr1, pr2));
	}

	public void testDepart_HasNoGettingOnError() throws Exception {
		String firstName = "鈴木";
		String lastName = "三郎";

		PassengerRecord pr1 = new PassengerRecord();
		User u1 = new User();
		u1.setFirstName(firstName);
		u1.setLastName(lastName);
		pr1.setUser(u1);

		PassengerRecord pr2 = new PassengerRecord();
		User u2 = new User();
		u2.setFirstName(firstName);
		u2.setLastName(lastName);
		pr2.setUser(u2);

		assertDepartHasError(new LinkedList<PassengerRecord>(),
				Lists.newArrayList(pr1, pr2), new LinkedList<PassengerRecord>());
	}


	public void testBack() throws Exception {
		assertShow();
		solo.clickOnView(solo.getView(R.id.departure_check_close_button));
		TestUtil.assertHide(mv);
		verify(s, never()).enterDrivePhase();
		verify(s, never()).enterFinishPhase();
	}
}
