package com.kogasoftware.odt.invehicledevice.test.unit.ui.modalview;

import android.app.Activity;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;
import com.kogasoftware.odt.invehicledevice.ui.modalview.MemoModalView;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.User;
import static org.mockito.Mockito.*;

public class MemoModalViewTestCase extends
		EmptyActivityInstrumentationTestCase2 {
	InVehicleDeviceService s;
	MemoModalView mv;
	Reservation r;
	Activity a;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		a = getActivity();
		s = mock(InVehicleDeviceService.class);
		mv = new MemoModalView(a, s);
		r = new Reservation();
		r.setId(12345);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void xtestEventBusに自動で登録される() throws Exception {
		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				getActivity().setContentView(R.layout.in_vehicle_device);
			}
		});
	}

	/**
	 * ShowEventを受け取ると表示される
	 */
	public void testShowEvent() throws InterruptedException {
		assertFalse(mv.isShown());
		TestUtil.assertHide(mv);

		TestUtil.assertShow(mv);

		assertTrue(solo.searchText(r.getId().toString()));

		for (User user : r.getUser().asSet()) {
			if (!user.getFirstName().isEmpty()) {
				assertTrue(solo.searchText(user.getFirstName()));
			}
			if (!user.getLastName().isEmpty()) {
				assertTrue(solo.searchText(user.getLastName()));
			}
		}
	}

	public void testReservationMemo0() throws Exception {
		String memo = "こんにちは";
		r.setMemo(memo);

		testShowEvent();

		assertTrue(solo.searchText(memo));
	}

	public void testReservationMemo1() throws Exception {
		String memo = "Hello reservation memo";
		r.setMemo(memo);

		testShowEvent();

		assertTrue(solo.searchText(memo));
	}

	public void testUserMemo0() throws Exception {
		String memo = "こんにちは";
		User u = new User();
		u.setRememberMe(memo);
		r.setUser(u);
		r.setId(5678);

		testShowEvent();

		assertFalse(solo.searchText("要介護"));
		assertFalse(solo.searchText("要車椅子"));
		assertFalse(solo.searchText("身体障害者"));
		assertTrue(solo.searchText(memo));
	}

	public void testUserMemo1() throws Exception {
		User u = new User();
		u.setFirstName("ふぁーすとねーむ");
		u.setHandicapped(true);
		r.setUser(u);

		testShowEvent();

		assertFalse(solo.searchText("要介護"));
		assertTrue(solo.searchText("身体障害者"));
		assertFalse(solo.searchText("要車椅子"));
	}

	public void testUserMemo2() throws Exception {
		User u = new User();
		u.setLastName("らすとねーむ");
		u.setWheelchair(true);
		r.setUser(u);

		testShowEvent();

		assertFalse(solo.searchText("要介護"));
		assertFalse(solo.searchText("身体障害者"));
		assertTrue(solo.searchText("要車椅子"));
	}

	public void testUserMemo3() throws Exception {
		User u = new User();
		u.setNeededCare(true);
		r.setUser(u);

		testShowEvent();

		assertTrue(solo.searchText("要介護"));
		assertFalse(solo.searchText("身体障害者"));
		assertFalse(solo.searchText("要車椅子"));
	}

	public void testUserMemo4() throws Exception {
		User u = new User();
		u.setWheelchair(true);
		u.setHandicapped(true);
		u.setNeededCare(true);
		u.setRememberMe("覚書");
		r.setUser(u);

		testShowEvent();

		assertTrue(solo.searchText("覚書"));
		assertTrue(solo.searchText("要介護"));
		assertTrue(solo.searchText("身体障害者"));
		assertTrue(solo.searchText("要車椅子"));
	}

	public void test戻るボタンを押すと消える() throws Exception {
		testShowEvent();
		solo.clickOnView(solo.getView(R.id.memo_close_button));
		getInstrumentation().waitForIdleSync();
		assertFalse(mv.isShown());
	}
}
