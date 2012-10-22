package com.kogasoftware.odt.invehicledevice.test.unit.ui.fragment;

import static org.mockito.Mockito.mock;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.User;

public class PassengerRecordMemoFragmentTestCase extends
		EmptyActivityInstrumentationTestCase2 {
	InVehicleDeviceService s;
	MemoModalView mv;
	PassengerRecord pr;
	Reservation r;
	User u;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		s = mock(InVehicleDeviceService.class);
		mv = new MemoModalView(a, s);
		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				a.setContentView(mv);
			}
		});
		pr = new PassengerRecord();
		r = new Reservation();
		u = new User();
		pr.setReservation(r);
		pr.setUser(u);
		r.setId(12345);
		u.setId(56789);
		u.setFirstName("first name");
		u.setLastName("last name");
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testShow() throws InterruptedException {
		TestUtil.assertHide(mv);
		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				mv.show(pr);
			}
		});
		TestUtil.assertShow(mv);

		assertTrue(solo.searchText(r.getId().toString()));

		for (User user : pr.getUser().asSet()) {
			if (!user.getFirstName().isEmpty()) {
				assertTrue(solo.searchText(user.getFirstName()));
			}
			if (!user.getLastName().isEmpty()) {
				assertTrue(solo.searchText(user.getLastName()));
			}
			return;
		}
		fail();
	}

	public void testReservationMemo0() throws Exception {
		String memo = "こんにちは";
		r.setMemo(memo);

		testShow();

		assertTrue(solo.searchText(memo));
	}

	public void testReservationMemo1() throws Exception {
		String memo = "Hello reservation memo";
		r.setMemo(memo);

		testShow();

		assertTrue(solo.searchText(memo));
	}

	public void testUserMemo0() throws Exception {
		String memo = "こんにちは";
		u.setMemo(memo);
		r.setId(5678);

		testShow();

		assertFalse(solo.searchText("要介護"));
		assertFalse(solo.searchText("要車椅子"));
		assertFalse(solo.searchText("身体障害者"));
		assertTrue(solo.searchText(memo));
	}

	public void testUserMemo1() throws Exception {
		u.setFirstName("ふぁーすとねーむ");
		u.setHandicapped(true);

		testShow();

		assertFalse(solo.searchText("要介護"));
		assertTrue(solo.searchText("身体障害者"));
		assertFalse(solo.searchText("要車椅子"));
	}

	public void testUserMemo2() throws Exception {
		u.setLastName("らすとねーむ");
		u.setWheelchair(true);

		testShow();

		assertFalse(solo.searchText("要介護"));
		assertFalse(solo.searchText("身体障害者"));
		assertTrue(solo.searchText("要車椅子"));
	}

	public void testUserMemo3() throws Exception {
		u.setNeededCare(true);

		testShow();

		assertTrue(solo.searchText("要介護"));
		assertFalse(solo.searchText("身体障害者"));
		assertFalse(solo.searchText("要車椅子"));
	}

	public void testUserMemo4() throws Exception {
		u.setWheelchair(true);
		u.setHandicapped(true);
		u.setNeededCare(true);
		u.setMemo("覚書");

		testShow();

		assertTrue(solo.searchText("覚書"));
		assertTrue(solo.searchText("要介護"));
		assertTrue(solo.searchText("身体障害者"));
		assertTrue(solo.searchText("要車椅子"));
	}

	public void test戻るボタンを押すと消える() throws Exception {
		testShow();
		solo.clickOnView(solo.getView(R.id.memo_close_button));

		TestUtil.assertHide(mv);
	}
}
