package com.kogasoftware.odt.invehicledevice.test.unit.ui.fragment;

import static org.mockito.Mockito.*;

import android.support.v4.app.FragmentManager;
import android.widget.FrameLayout;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.EventDispatcher;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;
import com.kogasoftware.odt.invehicledevice.ui.fragment.PassengerRecordMemoFragment;
import com.kogasoftware.odt.invehicledevice.apiclient.InVehicleDeviceApiClient;
import com.kogasoftware.odt.invehicledevice.apiclient.model.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Reservation;
import com.kogasoftware.odt.invehicledevice.apiclient.model.User;

public class PassengerRecordMemoFragmentTestCase extends
		EmptyActivityInstrumentationTestCase2 {
	InVehicleDeviceService s;
	InVehicleDeviceApiClient ac;
	LocalData ld;
	EventDispatcher ed;
	PassengerRecordMemoFragment f;
	PassengerRecord pr;
	Reservation r;
	User u;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ld = new LocalData();
		ed = mock(EventDispatcher.class);
		ac = mock(InVehicleDeviceApiClient.class);
		when(ac.withSaveOnClose()).thenReturn(ac);
		s = mock(InVehicleDeviceService.class);
		when(s.getLocalStorage()).thenReturn(new LocalStorage(ld));
		when(s.getEventDispatcher()).thenReturn(ed);
		when(s.getApiClient()).thenReturn(ac);
		a.setService(s);

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

	public void testShow() throws Throwable {
		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				int id = 12345;
				FrameLayout fl = new FrameLayout(a);
				fl.setId(id);
				a.setContentView(fl);
				f = PassengerRecordMemoFragment.newInstance(pr);
				FragmentManager fm = a.getSupportFragmentManager();
				fm.beginTransaction().add(id, f).commit();
			}
		});
		TestUtil.assertShow(f);

		assertTrue(solo.searchText(r.getId().toString()));

		for (User user : pr.getUser().asSet()) {
			if (!user.getFirstName().or("").isEmpty()) {
				assertTrue(solo.searchText(user.getFirstName().get()));
			}
			if (!user.getLastName().or("").isEmpty()) {
				assertTrue(solo.searchText(user.getLastName().get()));
			}
			return;
		}
		fail();
	}

	public void testReservationMemo0() throws Throwable {
		String memo = "こんにちは";
		r.setMemo(memo);

		testShow();

		assertTrue(solo.searchText(memo));
	}

	public void testReservationMemo1() throws Throwable {
		String memo = "Hello reservation memo";
		r.setMemo(memo);

		testShow();

		assertTrue(solo.searchText(memo));
	}

	public void testUserMemo0() throws Throwable {
		String memo = "こんにちは";
		u.setMemo(memo);
		r.setId(5678);

		testShow();

		assertFalse(solo.searchText("要介護"));
		assertFalse(solo.searchText("要車椅子"));
		assertFalse(solo.searchText("身体障害者"));
		assertTrue(solo.searchText(memo));
	}

	public void testUserMemo1() throws Throwable {
		u.setFirstName("ふぁーすとねーむ");
		u.setHandicapped(true);

		testShow();

		assertFalse(solo.searchText("要介護"));
		assertTrue(solo.searchText("身体障害者"));
		assertFalse(solo.searchText("要車椅子"));
	}

	public void testUserMemo2() throws Throwable {
		u.setLastName("らすとねーむ");
		u.setWheelchair(true);

		testShow();

		assertFalse(solo.searchText("要介護"));
		assertFalse(solo.searchText("身体障害者"));
		assertTrue(solo.searchText("要車椅子"));
	}

	public void testUserMemo3() throws Throwable {
		u.setNeededCare(true);

		testShow();

		assertTrue(solo.searchText("要介護"));
		assertFalse(solo.searchText("身体障害者"));
		assertFalse(solo.searchText("要車椅子"));
	}

	public void testUserMemo4() throws Throwable {
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

	public void test戻るボタンを押すと消える() throws Throwable {
		testShow();
		solo.clickOnView(solo.getView(R.id.passenger_record_memo_close_button));
		TestUtil.assertHide(f);
	}
}
