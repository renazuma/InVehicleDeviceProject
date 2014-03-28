package com.kogasoftware.odt.invehicledevice.integrationtest;

import junit.framework.AssertionFailedError;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

import com.jayway.android.robotium.solo.Solo;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.testutil.TestUtil;
import com.kogasoftware.odt.invehicledevice.testutil.apiclient.DummyApiClient;
import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;

public class DriveTestCase extends
		ActivityInstrumentationTestCase2<InVehicleDeviceActivity> {

	private Solo solo;

	@SuppressWarnings("deprecation")
	public DriveTestCase() {
		super("com.kogasoftware.odt.invehicledevice.ui.activity",
				InVehicleDeviceActivity.class);
		TestUtil.setApiClient(new DummyApiClient());
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		TestUtil.clearLocalStorage(getInstrumentation());

		solo = new Solo(getInstrumentation(), getActivity());
		assertTrue(TestUtil.waitForStartUI(getActivity()));
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
	}

	public void xtest01_起動時は走行中表示() {
		assertTrue(solo.searchText("走行中")); // TODO 画像ファイル名assertに書き換わる予定
	}

	public void xtest02_起動時は出発ダイアログは非表示() {
		try {
			// solo.getView(DepartureCheckModalView.class, 0);
		} catch (AssertionFailedError e) {
			return;
		}
		fail();
	}

	public void xtest03_到着しましたボタンを押すと停車中表示() {
		xtest01_起動時は走行中表示();
		solo.clickOnView(solo.getView(R.id.change_phase_button));
		solo.clickOnView(solo.getView(R.id.arrival_button));
		assertTrue(solo.searchText("停車中")); // TODO 画像ファイル名assertに書き換わる予定
	}
	
	public void xtest04_停車中から出発しますボタンを押すと出発確認画面表示() {
		solo.clickOnView(solo.getView(R.id.change_phase_button));
		solo.clickOnView(solo.getView(R.id.arrival_button));
		assertTrue(solo.searchText("停車中"));
		solo.clickOnView(solo.getView(R.id.change_phase_button));
		// assertNotNull(solo.getView(DepartureCheckModalView.class, 0));
	}

	public void xtest05_出発確認画面でやめるボタンを押すと停車中画面表示() {
		xtest04_停車中から出発しますボタンを押すと出発確認画面表示();

		solo.clickOnView(solo.getView(R.id.departure_check_close_button));
		// assertNotNull(solo.getView(PlatformPhaseFragment.class, 0));
		assertTrue(solo.searchText("停車中", true));
		assertFalse(solo.searchText("走行中", true));
	}

	public void xtest06_出発確認画面で出発するボタンを押すと運転中画面表示() {
		xtest04_停車中から出発しますボタンを押すと出発確認画面表示();

		solo.clickOnView(solo.getView(R.id.departure_button));

		// assertNotNull(solo.getView(DrivePhaseFragment.class, 0));
		assertFalse(solo.searchText("停車中", true));
		assertTrue(solo.searchText("走行中", true));
	}

	public void xtest07_乗降場の表示が遷移する() {

		xtest01_起動時は走行中表示();

		TextView next1 = (TextView) solo.getView(R.id.platform_name_1_beyond_text_view);
		String s = "▼ ";
		assertTrue(solo.searchText("乗降場A", true));
		assertEquals(s + "乗降場B", next1.getText());
		
		xtest06_出発確認画面で出発するボタンを押すと運転中画面表示();

		assertFalse(solo.searchText("乗降場A", true));
		assertTrue(solo.searchText("乗降場B", true));
		assertEquals(s + "乗降場C", next1.getText());
	}

	public void xtest08_最終乗降場についた時の挙動() throws Exception {
		for (Integer i = 0; i < 5; ++i) {
			xtest06_出発確認画面で出発するボタンを押すと運転中画面表示();
			if (solo.searchText("最終乗降場", true)) {
				xtest06_出発確認画面で出発するボタンを押すと運転中画面表示();
				break;
			}
			Thread.sleep(2000);
		}

		solo.clickOnView(solo.getView(R.id.change_phase_button));
		solo.clickOnView(solo.getView(R.id.arrival_button));
		solo.clickOnView(solo.getView(R.id.change_phase_button));
		// assertNotNull(solo.getView(DepartureCheckModalView.class, 0));

		solo.clickOnView(solo.getView(R.id.departure_button));

		// assertNotNull(solo.getView(FinishPhaseFragment.class, 0));
		TextView v = (TextView) solo.getView(R.id.phase_text_view);
		assertEquals("", v.getText());
	}
}