package com.kogasoftware.odt.invehicledevice.test.integration;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import android.test.ActivityInstrumentationTestCase2;

import com.google.common.collect.Lists;
import com.jayway.android.robotium.solo.Solo;
import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;
import com.kogasoftware.odt.invehicledevice.test.util.apiclient.MockApiClient;
import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;
import com.kogasoftware.odt.apiclient.ApiClientCallback;
import com.kogasoftware.odt.apiclient.ApiClientException;
import com.kogasoftware.odt.invehicledevice.apiclient.model.VehicleNotification;

public class NotificationTestCase extends
		ActivityInstrumentationTestCase2<InVehicleDeviceActivity> {

	private Solo solo;

	MockApiClient mdst = new MockApiClient();

	final Integer WAIT_MILLIS = 7000;

	@SuppressWarnings("deprecation")
	public NotificationTestCase() {
		super("com.kogasoftware.odt.invehicledevice.ui.activity",
				InVehicleDeviceActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();

		mdst.setNotificationFlag(true);
		mdst.setReservation(6);
		// mdst.setReservationCandidate(6, 1, 1, 1);

		// TestUtil.setDate("2012-01-01T09:00:00+09:00");
		TestUtil.setApiClient(mdst);
		TestUtil.clearStatus();

		solo = new Solo(getInstrumentation(), getActivity());
		assertTrue(TestUtil.waitForStartUI(getActivity()));
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
	}

	public void xtest01_起動時は非表示() {

		// assertEquals(View.VISIBLE, solo.getView(DrivePhaseFragment.class, 0)
		// 		.getVisibility());

		// assertEquals(View.GONE, solo.getView(NotificationModalView.class, 0)
		// 		.getVisibility());

	}

	public void xtest02_走行中に管理者から連絡が来たら表示() throws Exception {

		xtest01_起動時は非表示();

		final List<VehicleNotification> vehicleNotifications = Lists
				.newLinkedList();
		final CountDownLatch cdl = new CountDownLatch(1);
		mdst.getVehicleNotifications(new ApiClientCallback<List<VehicleNotification>>() {
			@Override
			public void onException(int reqkey, ApiClientException ex) {
				cdl.countDown();
			}

			@Override
			public void onFailed(int reqkey, int statusCode, String response) {
				cdl.countDown();
			}

			@Override
			public void onSucceed(int reqkey, int statusCode,
					List<VehicleNotification> result) {
				vehicleNotifications.addAll(result);
				cdl.countDown();
			}
		});
		cdl.await();

		if (vehicleNotifications.isEmpty()) {
			fail();
		}

		getInstrumentation().waitForIdleSync();

		solo.sleep(WAIT_MILLIS);

		// assertEquals(View.VISIBLE, solo.getView(NotificationModalView.class, 0)
		// 		.getVisibility());

	}

	public void xtest03_はいを押下して閉じ走行中に戻る() throws Exception {

		xtest02_走行中に管理者から連絡が来たら表示();

		getInstrumentation().waitForIdleSync();

		solo.clickOnButton("はい");

		solo.sleep(WAIT_MILLIS);

		getInstrumentation().waitForIdleSync();

		// assertEquals(View.GONE, solo.getView(NotificationModalView.class, 0)
		// 		.getVisibility());

		// assertEquals(View.VISIBLE, solo.getView(DrivePhaseFragment.class, 0)
		// 		.getVisibility());

	}
	/*
	 * public void test04_いいえを押下して閉じ走行中に戻る() { test02_走行中に管理者から連絡が来たら表示();
	 * 
	 * solo.clickOnButton("いいえ");
	 * 
	 * getInstrumentation().waitForIdleSync();
	 * 
	 * solo.sleep(WAIT_MILLIS);
	 * 
	 * assertEquals(View.GONE, solo.getView(R.id.notification_modal_view)
	 * .getVisibility());
	 * 
	 * assertEquals(View.VISIBLE, solo.getView(R.id.drive_phase_view)
	 * .getVisibility()); }
	 * 
	 * public void test05_停車中に管理者から連絡が来たら表示() { test01_起動時は非表示();
	 * 
	 * solo.clickOnButton("到着しました"); solo.clickOnButton("到着する");
	 * 
	 * List<VehicleNotification> vehicleNotifications = null; try {
	 * vehicleNotifications = mdst.getVehicleNotifications(); } catch
	 * (ApiClientException e) { // TODO 自動生成された catch ブロック e.printStackTrace(); }
	 * 
	 * if (vehicleNotifications.isEmpty()) { return; }
	 * 
	 * getInstrumentation().waitForIdleSync();
	 * 
	 * solo.sleep(WAIT_MILLIS);
	 * 
	 * assertEquals(View.VISIBLE, solo.getView(R.id.notification_modal_view)
	 * .getVisibility());
	 * 
	 * }
	 * 
	 * public void test06_はいを押下して閉じ停車中に戻る() { test05_停車中に管理者から連絡が来たら表示();
	 * 
	 * solo.clickOnButton("はい");
	 * 
	 * getInstrumentation().waitForIdleSync();
	 * 
	 * solo.sleep(WAIT_MILLIS);
	 * 
	 * assertEquals(View.GONE, solo.getView(R.id.notification_modal_view)
	 * .getVisibility()); assertEquals(View.VISIBLE,
	 * solo.getView(R.id.platform_phase_view) .getVisibility());
	 * 
	 * }
	 * 
	 * public void test07_いいえを押下して閉じ停車中に戻る() { test05_停車中に管理者から連絡が来たら表示();
	 * 
	 * solo.clickOnButton("いいえ");
	 * 
	 * getInstrumentation().waitForIdleSync();
	 * 
	 * assertEquals(View.GONE, solo.getView(R.id.notification_modal_view)
	 * .getVisibility());
	 * 
	 * assertEquals(View.VISIBLE, solo.getView(R.id.platform_phase_view)
	 * .getVisibility()); }
	 * 
	 * public void test11_地図画面中に管理者から連絡が来たら表示() { test01_起動時は非表示();
	 * 
	 * solo.clickOnButton("地図");
	 * 
	 * List<VehicleNotification> vehicleNotifications = null; try {
	 * vehicleNotifications = mdst.getVehicleNotifications(); } catch
	 * (ApiClientException e) { // TODO 自動生成された catch ブロック e.printStackTrace(); }
	 * 
	 * if (vehicleNotifications.isEmpty()) { return; }
	 * 
	 * getInstrumentation().waitForIdleSync();
	 * 
	 * solo.sleep(WAIT_MILLIS);
	 * 
	 * assertEquals(View.VISIBLE, solo.getView(R.id.notification_modal_view)
	 * .getVisibility()); }
	 * 
	 * public void test12_いいえを押下して閉じ地図画面に戻る() { test11_地図画面中に管理者から連絡が来たら表示();
	 * 
	 * solo.clickOnButton("いいえ");
	 * 
	 * getInstrumentation().waitForIdleSync();
	 * 
	 * assertEquals(View.GONE, solo.getView(R.id.notification_modal_view)
	 * .getVisibility());
	 * 
	 * assertEquals(View.VISIBLE, solo.getView(R.id.navigation_modal_view)
	 * .getVisibility()); }
	 * 
	 * public void test13_はいを押下して閉じ地図画面に戻る() { test11_地図画面中に管理者から連絡が来たら表示();
	 * 
	 * solo.clickOnButton("はい");
	 * 
	 * getInstrumentation().waitForIdleSync();
	 * 
	 * assertEquals(View.GONE, solo.getView(R.id.notification_modal_view)
	 * .getVisibility()); assertEquals(View.VISIBLE,
	 * solo.getView(R.id.navigation_modal_view) .getVisibility());
	 * 
	 * }
	 */
}