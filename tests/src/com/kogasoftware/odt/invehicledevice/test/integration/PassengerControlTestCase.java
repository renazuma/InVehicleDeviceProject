package com.kogasoftware.odt.invehicledevice.test.integration;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.TextView;

import com.jayway.android.robotium.solo.Solo;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;
import com.kogasoftware.odt.invehicledevice.test.util.apiclient.MockDataSource;
import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;

public class PassengerControlTestCase extends
		ActivityInstrumentationTestCase2<InVehicleDeviceActivity> {

	private Solo solo;

	public PassengerControlTestCase() {
		super("com.kogasoftware.odt.invehicledevice.ui.activity",
				InVehicleDeviceActivity.class);
	}

	public void dataset(Integer i) {

		MockDataSource mds = new MockDataSource();

		mds.setReservation(i);
		mds.setReservationCandidate(6, 1, 1, 1);

		TestUtil.setDataSource(mds);
		TestUtil.setDate("2012-01-01T09:00:00+09:00");
	}

	@Override
	public void setUp() throws Exception {

		super.setUp();

		dataset(6);

		TestUtil.clearStatus();

		solo = new Solo(getInstrumentation(), getActivity());
		assertTrue(TestUtil.waitForStartUI(getActivity()));

		// デフォルトで停車中にする
		if (solo.searchButton("到着し\nました", true)) {
			solo.clickOnButton("到着し\nました");
			solo.clickOnButton("到着する");
		}
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
	}

	public boolean searchDrive(String sSearch) {


		for (int i = 0 ; i < 3 ; i++) {

			getInstrumentation().waitForIdleSync();

			if (solo.searchText(sSearch,0,true)) return true;

			System.out.println("searchDrive:" + i );

		}
		return false;
	}

	public void test00_データ初期設定() {
		dataset(6);

	}

	public void test01_乗車人数_全員乗車() throws Exception {
		Thread.sleep(2 * 1000);

		solo.clickOnText("名字a", 0, true);
		solo.clickOnText("名字b", 0, true);
		solo.clickOnText("名字c", 0, true);
		solo.clickOnText("名字d", 0, true);
		solo.clickOnText("名字e", 0, true);
		solo.clickOnText("名字f", 0, true);

		solo.clickOnButton(" 出発 \n する ");

		assertEquals(View.VISIBLE, solo.getView(DepartureCheckModalView.class, 0)
				.getVisibility());

		Thread.sleep(2 * 1000);
	
		solo.clickOnButton("出発する");
		getInstrumentation().waitForIdleSync();
		TextView v = (TextView) solo.getView(R.id.phase_text_view);
		assertEquals("走行中", v.getText());

		assertTrue(searchDrive("21名乗車中"));

	}

	public void test02_乗車人数_１予約のみ乗車() throws Exception {
		Thread.sleep(2 * 1000);
		
		solo.clickOnText("名字a", 0, true);

		solo.clickOnButton(" 出発 \n する ");

		Thread.sleep(2 * 1000);

		assertEquals(View.VISIBLE, solo.getView(DepartureCheckModalView.class, 0)
				.getVisibility());

		assertTrue(solo.searchText("名字b", 0, true));
		assertTrue(solo.searchText("名字c", 0, true));
		assertTrue(solo.searchText("名字d", 0, true));
		assertTrue(solo.searchText("名字e", 0, true));
		assertTrue(solo.searchText("名字f", 0, true));

		solo.clickOnButton("出発する");
		Thread.sleep(2 * 1000);
		TextView v = (TextView) solo.getView(R.id.phase_text_view);
		assertEquals("走行中", v.getText());

		assertTrue(searchDrive("1名乗車中"));

	}

	public void xtest03_降車した人数が反映されている() throws Exception {

		test01_乗車人数_全員乗車();

		Thread.sleep(2 * 1000);

		solo.clickOnView(solo.getView(R.id.change_phase_button));

		Thread.sleep(2 * 1000);

		solo.clickOnView(solo.getView(R.id.arrival_button));

		Thread.sleep(2 * 1000);

		assertTrue(solo.searchText("停車中")); // TODO 画像ファイル名assertに書き換わる予定

		solo.clickOnText("名字d", 0, true);

		solo.clickOnButton(" 出発 \n する ");

		assertEquals(View.VISIBLE, solo.getView(DepartureCheckModalView.class, 0)
				.getVisibility());

		solo.clickOnView(solo.getView(R.id.departure_button));
		Thread.sleep(2 * 1000);
		TextView v = (TextView) solo.getView(R.id.phase_text_view);
		assertEquals("走行中", v.getText());

		solo.searchText("17名乗車中");

	}

	public void xtest04_手前の乗降場で乗車() throws Exception {

		test01_乗車人数_全員乗車();

		getInstrumentation().waitForIdleSync();

		solo.clickOnView(solo.getView(R.id.change_phase_button));

		getInstrumentation().waitForIdleSync();
		
		solo.clickOnView(solo.getView(R.id.arrival_button));

		getInstrumentation().waitForIdleSync();

		assertTrue(solo.searchText("停車中")); // TODO 画像ファイル名assertに書き換わる予定

		assertTrue(solo.searchToggleButton("未来の乗客を表示"));
		solo.clickOnToggleButton("未来の乗客を表示");

		solo.clickOnText("名字i", 0, true);

		solo.clickOnButton(" 出発 \n する ");

		assertEquals(View.VISIBLE, solo.getView(DepartureCheckModalView.class, 0)
				.getVisibility());

		solo.clickOnView(solo.getView(R.id.departure_button));
		getInstrumentation().waitForIdleSync();
		TextView v = (TextView) solo.getView(R.id.phase_text_view);
		assertEquals("走行中", v.getText());

		solo.searchText("22名乗車中");

	}

	public void xtest05_手前の乗降場で降車() throws Exception {

		test01_乗車人数_全員乗車();

		getInstrumentation().waitForIdleSync();

		solo.clickOnView(solo.getView(R.id.change_phase_button));

		getInstrumentation().waitForIdleSync();

		solo.clickOnView(solo.getView(R.id.arrival_button));

		getInstrumentation().waitForIdleSync();

		assertTrue(solo.searchText("停車中")); // TODO 画像ファイル名assertに書き換わる予定

		assertTrue(solo.searchToggleButton("乗車中の乗客全員を表示"));
		solo.clickOnToggleButton("乗車中の乗客全員を表示");
		
		solo.scrollUpList(0);
		solo.scrollUpList(0);
		solo.scrollUpList(0);
		
		assertTrue(solo.searchText("名字b", 0, true));
		solo.clickOnText("名字b", 0, true);

		solo.clickOnButton(" 出発 \n する ");

		assertEquals(View.VISIBLE, solo.getView(DepartureCheckModalView.class, 0)
				.getVisibility());

		solo.clickOnView(solo.getView(R.id.departure_button));
		getInstrumentation().waitForIdleSync();
		TextView v = (TextView) solo.getView(R.id.phase_text_view);
		assertEquals("走行中", v.getText());

		solo.searchText("19名乗車中");

	}

	public void xtest06_後の乗降場から乗車() throws Exception {

		test02_乗車人数_１予約のみ乗車();

		getInstrumentation().waitForIdleSync();

		solo.clickOnView(solo.getView(R.id.change_phase_button));

		getInstrumentation().waitForIdleSync();
		
		solo.clickOnView(solo.getView(R.id.arrival_button));

		getInstrumentation().waitForIdleSync();

		assertTrue(solo.searchText("停車中")); // TODO 画像ファイル名assertに書き換わる予定

		assertTrue(solo.searchToggleButton("過去の乗降場で未乗車の乗客を表示"));
		solo.clickOnToggleButton("過去の乗降場で未乗車の乗客を表示");
		
		for (Integer i = 0; i < 20; ++i) {
			solo.scrollUp();
		}

		assertTrue(solo.searchText("名字c", 0, true));
		getInstrumentation().waitForIdleSync();
		solo.clickOnText("名字c", 0, true);

		solo.clickOnButton(" 出発 \n する ");

		assertEquals(View.VISIBLE, solo.getView(DepartureCheckModalView.class, 0)
				.getVisibility());

		solo.clickOnView(solo.getView(R.id.departure_button));
		getInstrumentation().waitForIdleSync();
		TextView v = (TextView) solo.getView(R.id.phase_text_view);
		assertEquals("走行中", v.getText());

		solo.searchText("3名乗車中");


	}



}