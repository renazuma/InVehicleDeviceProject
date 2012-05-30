package com.kogasoftware.odt.invehicledevice.test.unit.ui.modalview;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.event.CommonLogicLoadCompleteEvent;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;
import com.kogasoftware.odt.invehicledevice.ui.modalview.MemoModalView;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.User;

public class MemoModalViewTestCase extends
		EmptyActivityInstrumentationTestCase2 {
	CommonLogic cl;
	MemoModalView mv;
	Reservation r;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cl = newCommonLogic();
		mv = (MemoModalView) inflateAndAddTestLayout(com.kogasoftware.odt.invehicledevice.test.R.layout.test_memo_modal_view);
		cl.registerEventListener(mv);
		mv.setCommonLogic(new CommonLogicLoadCompleteEvent(cl));
		r = new Reservation();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if (cl != null) {
			cl.dispose();
		}
	}

	public void xtestEventBusに自動で登録される() throws Exception {
		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				getActivity().setContentView(R.layout.in_vehicle_device);
			}
		});
		CommonLogic cl2 = newCommonLogic();
		try {
			assertEquals(cl2.countRegisteredClass(MemoModalView.class)
					.intValue(), 1);
		} finally {
			cl2.dispose();
		}
	}

	/**
	 * ShowEventを受け取ると表示される
	 */
	public void testShowEvent() throws InterruptedException {
		assertFalse(mv.isShown());
		TestUtil.willHide(mv);
		
		cl.postEvent(new MemoModalView.ShowEvent(r));

		TestUtil.willShow(solo, mv);
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
		
		testShowEvent();
		
		assertFalse(solo.searchText("要介護"));
		assertFalse(solo.searchText("要車椅子"));
		assertTrue(solo.searchText(memo));
	}

	public void testUserMemo1() throws Exception {
		User u = new User();
		u.setHandicapped(true);
		r.setUser(u);
		
		testShowEvent();
		
		assertTrue(solo.searchText("要介護"));
		assertFalse(solo.searchText("要車椅子"));
	}

	public void testUserMemo2() throws Exception {
		User u = new User();
		u.setWheelchair(true);
		r.setUser(u);
		
		testShowEvent();
		
		assertFalse(solo.searchText("要介護"));
		assertTrue(solo.searchText("要車椅子"));
	}

	public void testUserMemo3() throws Exception {
		User u = new User();
		u.setWheelchair(true);
		u.setHandicapped(true);
		u.setRememberMe("覚書");
		r.setUser(u);
		
		testShowEvent();
		
		assertTrue(solo.searchText("覚書"));
		assertTrue(solo.searchText("要介護"));
		assertTrue(solo.searchText("要車椅子"));
	}
	
	public void test戻るボタンを押すと消える() throws Exception {
		testShowEvent();
		solo.clickOnView(solo.getView(R.id.memo_close_button));
		getInstrumentation().waitForIdleSync();
		assertFalse(mv.isShown());
	}
}
