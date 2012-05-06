package com.kogasoftware.odt.invehicledevice.test.unit.ui.modalview;

import android.view.View;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.event.CommonLogicLoadCompleteEvent;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.ui.modalview.ReturnPathModalView;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.User;

public class ReturnPathModalViewTestCase extends
		EmptyActivityInstrumentationTestCase2 {
	CommonLogic cl;
	ReturnPathModalView mv;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cl = newCommonLogic();
		mv = (ReturnPathModalView) inflateAndAddTestLayout(com.kogasoftware.odt.invehicledevice.test.R.layout.test_return_path_modal_view);
		cl.registerEventListener(mv);
		mv.setCommonLogic(new CommonLogicLoadCompleteEvent(cl));
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if (cl != null) {
			cl.dispose();
		}
	}

	public void testEventBusに自動で登録される() throws Exception {
		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				getActivity().setContentView(R.layout.in_vehicle_device);
			}
		});
		CommonLogic cl2 = newCommonLogic();
		try {
			assertEquals(cl2.countRegisteredClass(ReturnPathModalView.class)
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
		assertNotSame(mv.getVisibility(), View.VISIBLE);
		String firstName = "first name !";
		Reservation r = new Reservation();
		User u = new User();
		u.setFirstName(firstName);
		r.setUser(u);
		cl.postEvent(new ReturnPathModalView.ShowEvent(r));
		getInstrumentation().waitForIdleSync();

		assertTrue(mv.isShown());
		assertEquals(mv.getVisibility(), View.VISIBLE);
		assertTrue(solo.searchText(firstName));
	}

	public void test下ボタンを押すと下へスクロール() throws Exception {
		test予約候補検索ボタンを押すと予約候補が入る();
		solo.clickOnView(solo
				.getView(R.id.reservation_candidate_scroll_down_button));
		getInstrumentation().waitForIdleSync();
		fail("stub!");
	}

	/**
	 * ShowEventを受け取ると表示される
	 */
	public void test既に表示されている状態で再度ShowEventしても古いほうが表示されたまま()
			throws InterruptedException {
		Reservation r1 = new Reservation();
		User u1 = new User();
		u1.setFirstName("firstName1");
		r1.setUser(u1);
		cl.postEvent(new ReturnPathModalView.ShowEvent(r1));
		getInstrumentation().waitForIdleSync();

		assertTrue(mv.isShown());
		assertEquals(mv.getVisibility(), View.VISIBLE);
		assertTrue(solo.searchText(u1.getFirstName()));

		Reservation r2 = new Reservation();
		User u2 = new User();
		u2.setFirstName("firstName2");
		r2.setUser(u2);
		cl.postEvent(new ReturnPathModalView.ShowEvent(r2));
		getInstrumentation().waitForIdleSync();

		assertTrue(mv.isShown());
		assertEquals(mv.getVisibility(), View.VISIBLE);
		assertTrue(solo.searchText(u1.getFirstName()));
		assertFalse(solo.searchText(u2.getFirstName()));
	}

	public void test上ボタンを押すと上へスクロール() throws Exception {
		test予約候補検索ボタンを押すと予約候補が入る();
		solo.clickOnView(solo
				.getView(R.id.reservation_candidate_scroll_up_button));
		getInstrumentation().waitForIdleSync();
		fail("stub!");
	}

	public void test戻るボタンを押すと消える() throws Exception {
		testShowEvent();
		solo.clickOnView(solo.getView(R.id.return_path_close_button));
		getInstrumentation().waitForIdleSync();
		assertFalse(mv.isShown());
	}

	public void test予約ボタンを押すと予約が起きて閉じる() throws Exception {
		test予約候補検索ボタンを押すと予約候補が入る();
		solo.clickOnView(solo.getView(R.id.do_reservation_button));
		getInstrumentation().waitForIdleSync();
		assertFalse(mv.isShown());
		fail("stub!");
	}

	public void test予約候補検索ボタンを押すと予約候補が入る() throws Exception {
		testShowEvent();
		solo.clickOnView(solo.getView(R.id.search_return_path_button));
		getInstrumentation().waitForIdleSync();
		fail("stub!");
	}

}
