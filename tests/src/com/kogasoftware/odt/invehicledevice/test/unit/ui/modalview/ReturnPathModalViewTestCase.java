package com.kogasoftware.odt.invehicledevice.test.unit.ui.modalview;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import android.view.View;
import android.widget.Spinner;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.datasource.DataSource;
import com.kogasoftware.odt.invehicledevice.logic.datasource.EmptyDataSource;
import com.kogasoftware.odt.invehicledevice.logic.event.CommonLogicLoadCompleteEvent;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;
import com.kogasoftware.odt.invehicledevice.ui.modalview.ReturnPathModalView;
import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;
import com.kogasoftware.odt.webapi.model.Demand;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.ReservationCandidate;
import com.kogasoftware.odt.webapi.model.User;

public class ReturnPathModalViewTestCase extends
		EmptyActivityInstrumentationTestCase2 {
	CommonLogic cl;
	ReturnPathModalView mv;

	protected void createView() throws Exception {
		assertNull(cl);
		assertNull(mv);
		
		cl = newCommonLogic();
		mv = (ReturnPathModalView) inflateAndAddTestLayout(com.kogasoftware.odt.invehicledevice.test.R.layout.test_return_path_modal_view);
		cl.registerEventListener(mv);
		mv.setCommonLogic(new CommonLogicLoadCompleteEvent(cl));
	}
	
	BlockingQueue<Demand> searchRequests = new LinkedBlockingQueue<Demand>();
	BlockingQueue<List<ReservationCandidate>> searchResponses = new LinkedBlockingQueue<List<ReservationCandidate>>();
	BlockingQueue<ReservationCandidate> createRequests = new LinkedBlockingQueue<ReservationCandidate>();
	BlockingQueue<Reservation> createResponses = new LinkedBlockingQueue<Reservation>();
	
	DataSource dataSource = new EmptyDataSource() {
		@Override
		public int searchReservationCandidate(Demand demand,
				WebAPICallback<List<ReservationCandidate>> callback) {
			searchRequests.add(demand);
			List<ReservationCandidate> l = new LinkedList<ReservationCandidate>();
			callback.onSucceed(0, 200, l);
			return 0;
		}

		@Override
		public int createReservation(ReservationCandidate reservationCandidate,
				WebAPICallback<List<ReservationCandidate>> callback) {
			createRequests.add(reservationCandidate);
			return 0;
		}
	};

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
		cl = newCommonLogic();
		assertEquals(cl.countRegisteredClass(ReturnPathModalView.class)
				.intValue(), 1);
	}

	/**
	 * ShowEventを受け取ると表示される
	 */
	public void xtestShowEvent() throws Exception {
		createView();
		
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

	public void xtest下ボタンを押すと下へスクロール() throws Exception {
		xtest予約候補検索ボタンを押すと予約候補が入る();
		solo.clickOnView(solo
				.getView(R.id.reservation_candidate_scroll_down_button));
		getInstrumentation().waitForIdleSync();
		fail("stub!");
	}

	/**
	 * ShowEventを受け取ると表示される
	 */
	public void xtest既に表示されている状態で再度ShowEventしても古いほうが表示されたまま()
			throws Exception {
		createView();
		
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

	public void xtest上ボタンを押すと上へスクロール() throws Exception {
		xtest予約候補検索ボタンを押すと予約候補が入る();
		solo.clickOnView(solo
				.getView(R.id.reservation_candidate_scroll_up_button));
		getInstrumentation().waitForIdleSync();
		fail("stub!");
	}

	public void xtest戻るボタンを押すと消える() throws Exception {
		xtestShowEvent();
		solo.clickOnView(solo.getView(R.id.return_path_close_button));
		getInstrumentation().waitForIdleSync();
		assertFalse(mv.isShown());
	}

	public void callTest現在時刻が表示される(DateTime now) throws Exception { // TODO:日付の変わり目で失敗する
		TestUtil.setDate(now);

		xtestShowEvent();
		Spinner h = (Spinner) solo
				.getView(R.id.reservation_candidate_hour_spinner);
		assertTrue(h.getSelectedItem() instanceof String);
		Integer hi = Integer.parseInt((String) h.getSelectedItem());
		Spinner m = (Spinner) solo
				.getView(R.id.reservation_candidate_minute_spinner);
		assertTrue(m.getSelectedItem() instanceof String);
		Integer mi = Integer.parseInt((String) m.getSelectedItem());

		DateTime uiTime = (new DateTime()).withHourOfDay(hi)
				.withMinuteOfHour(mi).withSecondOfMinute(0);

		// ±1分まで誤差を許す
		Interval i = new Interval(now.minusMinutes(1), now.plusMinutes(1));
		assertTrue(i.contains(uiTime));
	}
	
	public void xtest現在時刻が表示されるnow() throws Exception {
		callTest現在時刻が表示される(DateTime.now());
	}

	public void xtest現在時刻が表示される00_00() throws Exception {
		callTest現在時刻が表示される(DateTime.now().withHourOfDay(0).withMinuteOfHour(0));
	}

	public void xtest現在時刻が表示される23_59() throws Exception {
		callTest現在時刻が表示される(DateTime.now().withHourOfDay(23).withMinuteOfHour(59));
	}

	public void xtest現在時刻が表示される11_59() throws Exception {
		callTest現在時刻が表示される(DateTime.now().withHourOfDay(11).withMinuteOfHour(59));
	}
	
	public void testUIで入力したデータが送信される() throws Exception {
		TestUtil.setDate(DateTime.now().withHourOfDay(10).withMinuteOfHour(3));
		xtestShowEvent();

		DateTime date = DateTime.now().withHourOfDay(12).withMinuteOfHour(34);
		
		solo.clickOnView(solo.getView(R.id.reservation_candidate_hour_spinner));
		solo.clickOnText("" + date.getHourOfDay());
		
		solo.clickOnView(solo.getView(R.id.reservation_candidate_minute_spinner));
		solo.clickOnText("" + date.getMinuteOfHour());
		
		solo.clickOnView(solo.getView(R.id.reservation_candidate_in_or_out_spinner));
		solo.clickOnText("乗車");
		
		solo.clickOnButton(solo.getString(R.string.search_reservation_candidate));
		
	}

	public void callTest人数はReservationの人数と同じ(Integer count) {
		Reservation r = new Reservation();
		User u = new User();
		u.setFirstName("ファースト");
		u.setLastName("ラスト");
		r.setPassengerCount(count);
		r.setUser(u);

		cl.postEvent(new ReturnPathModalView.ShowEvent(r));
		getInstrumentation().waitForIdleSync();

	}

	public void xtest人数はReservationの人数と同じ1() {
		callTest人数はReservationの人数と同じ(1);
	}

	public void xtest人数はReservationの人数と同じ50() {
		callTest人数はReservationの人数と同じ(50);
	}

	public void xtest予約ボタンを押すと予約が起きて閉じる() throws Exception {
		xtest予約候補検索ボタンを押すと予約候補が入る();
		solo.clickOnView(solo.getView(R.id.do_reservation_button));
		getInstrumentation().waitForIdleSync();
		assertFalse(mv.isShown());
		fail("stub!");
	}

	public void xtest予約候補検索ボタンを押すと予約候補が入る() throws Exception {
		xtestShowEvent();
		solo.clickOnView(solo.getView(R.id.search_return_path_button));
		getInstrumentation().waitForIdleSync();
		fail("stub!");
	}

}
