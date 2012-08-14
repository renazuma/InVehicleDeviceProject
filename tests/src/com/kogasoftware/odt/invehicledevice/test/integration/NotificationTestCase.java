package com.kogasoftware.odt.invehicledevice.test.integration;

import java.util.List;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;

import com.jayway.android.robotium.solo.Solo;
import com.kogasoftware.odt.invehicledevice.test.unit.datasource.MockDataSource;
import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;
import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;
import com.kogasoftware.odt.invehicledevice.ui.modalview.NotificationModalView;
import com.kogasoftware.odt.invehicledevice.ui.phaseview.DrivePhaseView;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class NotificationTestCase extends
		ActivityInstrumentationTestCase2<InVehicleDeviceActivity> {

	private Solo solo;

	MockDataSource mdst = new MockDataSource();

	final Integer WAIT_MILLIS = 7000;

	public NotificationTestCase() {
		super("com.kogasoftware.odt.invehicledevice.ui.activity",
				InVehicleDeviceActivity.class);

	}

	@Override
	public void setUp() throws Exception {
		super.setUp();

		mdst.setNotificationFlag(true);
		mdst.setReservation(6);
		mdst.setReservationCandidate(6,1,1,1);

		TestUtil.setDate("2012-01-01T09:00:00+09:00");
		TestUtil.setDataSource(mdst);
		TestUtil.clearStatus();

		solo = new Solo(getInstrumentation(), getActivity());
		assertTrue(TestUtil.waitForStartUI(getActivity()));
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
	}

	public void test01_起動時は非表示() {

		assertEquals(View.VISIBLE, solo.getView(DrivePhaseView.class, 0)
				.getVisibility());

		assertEquals(View.GONE, solo.getView(NotificationModalView.class, 0)
				.getVisibility());


	}

	public void test02_走行中に管理者から連絡が来たら表示() {

		test01_起動時は非表示();

		List<VehicleNotification> vehicleNotifications = null;
		try {
			vehicleNotifications = mdst.getVehicleNotifications();
		} catch (WebAPIException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		if (vehicleNotifications.isEmpty()) {
			return;
		}

		getInstrumentation().waitForIdleSync();

		solo.sleep(WAIT_MILLIS);

		assertEquals(View.VISIBLE, solo.getView(NotificationModalView.class, 0)
				.getVisibility());

	}
	public void test03_はいを押下して閉じ走行中に戻る() {

		test02_走行中に管理者から連絡が来たら表示();

		getInstrumentation().waitForIdleSync();

		solo.clickOnButton("はい");

		solo.sleep(WAIT_MILLIS);

		getInstrumentation().waitForIdleSync();

		assertEquals(View.GONE, solo.getView(NotificationModalView.class, 0)
				.getVisibility());

		assertEquals(View.VISIBLE, solo.getView(DrivePhaseView.class, 0)
				.getVisibility());

	}
/*
	public void test04_いいえを押下して閉じ走行中に戻る() {
		test02_走行中に管理者から連絡が来たら表示();

		solo.clickOnButton("いいえ");

		getInstrumentation().waitForIdleSync();

		solo.sleep(WAIT_MILLIS);

		assertEquals(View.GONE, solo.getView(R.id.notification_modal_view)
				.getVisibility());

		assertEquals(View.VISIBLE, solo.getView(R.id.drive_phase_view)
				.getVisibility());
	}

	public void test05_停車中に管理者から連絡が来たら表示() {
		test01_起動時は非表示();

		solo.clickOnButton("到着しました");
		solo.clickOnButton("到着する");

		List<VehicleNotification> vehicleNotifications = null;
		try {
			vehicleNotifications = mdst.getVehicleNotifications();
		} catch (WebAPIException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		if (vehicleNotifications.isEmpty()) {
			return;
		}

		getInstrumentation().waitForIdleSync();

		solo.sleep(WAIT_MILLIS);

		assertEquals(View.VISIBLE, solo.getView(R.id.notification_modal_view)
				.getVisibility());

	}

	public void test06_はいを押下して閉じ停車中に戻る() {
		test05_停車中に管理者から連絡が来たら表示();

		solo.clickOnButton("はい");

		getInstrumentation().waitForIdleSync();

		solo.sleep(WAIT_MILLIS);

		assertEquals(View.GONE, solo.getView(R.id.notification_modal_view)
				.getVisibility());
		assertEquals(View.VISIBLE, solo.getView(R.id.platform_phase_view)
				.getVisibility());

	}

	public void test07_いいえを押下して閉じ停車中に戻る() {
		test05_停車中に管理者から連絡が来たら表示();

		solo.clickOnButton("いいえ");

		getInstrumentation().waitForIdleSync();

		assertEquals(View.GONE, solo.getView(R.id.notification_modal_view)
				.getVisibility());

		assertEquals(View.VISIBLE, solo.getView(R.id.platform_phase_view)
				.getVisibility());
	}

	public void test11_地図画面中に管理者から連絡が来たら表示() {
		test01_起動時は非表示();

		solo.clickOnButton("地図");

		List<VehicleNotification> vehicleNotifications = null;
		try {
			vehicleNotifications = mdst.getVehicleNotifications();
		} catch (WebAPIException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		if (vehicleNotifications.isEmpty()) {
			return;
		}

		getInstrumentation().waitForIdleSync();

		solo.sleep(WAIT_MILLIS);

		assertEquals(View.VISIBLE, solo.getView(R.id.notification_modal_view)
				.getVisibility());
	}

	public void test12_いいえを押下して閉じ地図画面に戻る() {
		test11_地図画面中に管理者から連絡が来たら表示();

		solo.clickOnButton("いいえ");

		getInstrumentation().waitForIdleSync();

		assertEquals(View.GONE, solo.getView(R.id.notification_modal_view)
				.getVisibility());

		assertEquals(View.VISIBLE, solo.getView(R.id.navigation_modal_view)
				.getVisibility());
	}

	public void test13_はいを押下して閉じ地図画面に戻る() {
		test11_地図画面中に管理者から連絡が来たら表示();

		solo.clickOnButton("はい");

		getInstrumentation().waitForIdleSync();

		assertEquals(View.GONE, solo.getView(R.id.notification_modal_view)
				.getVisibility());
		assertEquals(View.VISIBLE, solo.getView(R.id.navigation_modal_view)
				.getVisibility());

	}
	*/
}