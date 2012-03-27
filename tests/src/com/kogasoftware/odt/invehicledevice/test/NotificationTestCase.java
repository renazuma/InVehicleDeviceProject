package com.kogasoftware.odt.invehicledevice.test;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;

import com.jayway.android.robotium.solo.Solo;
import com.kogasoftware.odt.invehicledevice.InVehicleDeviceActivity;
import com.kogasoftware.odt.invehicledevice.R;

public class NotificationTestCase extends
		ActivityInstrumentationTestCase2<InVehicleDeviceActivity> {

	private Solo solo;

	public NotificationTestCase() {
		super("com.kogasoftware.odt.invehicledevice",
				InVehicleDeviceActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	public void test01起動時は非表示() {
		assertEquals(View.GONE, solo.getView(R.id.notification_modal)
				.getVisibility());
	}

	public void test02走行中に管理者から連絡が来たら表示() {
		test01起動時は非表示();
		
		//TODO 管理者からの連絡部分が実装されたら置き換える
		solo.clickOnView(solo.getView(R.id.status_text_view));

		getInstrumentation().waitForIdleSync();

		assertEquals(View.VISIBLE, solo.getView(R.id.notification_modal)
				.getVisibility());
	}

	public void test03はいを押下して閉じ走行中に戻る() {
		test02走行中に管理者から連絡が来たら表示();
		
		solo.clickOnButton("はい");

		getInstrumentation().waitForIdleSync();

		assertEquals(View.GONE, solo.getView(R.id.notification_modal)
				.getVisibility());
		assertEquals(View.VISIBLE, solo.getView(R.id.driving_layout)
				.getVisibility());

	}

	public void test04いいえを押下して閉じ走行中に戻る() {
		test02走行中に管理者から連絡が来たら表示();
		
		solo.clickOnButton("いいえ");

		getInstrumentation().waitForIdleSync();

		assertEquals(View.GONE, solo.getView(R.id.notification_modal)
				.getVisibility());

		assertEquals(View.VISIBLE, solo.getView(R.id.driving_layout)
				.getVisibility());
	}

	public void test05停車中に管理者から連絡が来たら表示() {
		test01起動時は非表示();

		solo.clickOnButton("到着しました");
		
		//TODO 管理者からの連絡部分が実装されたら置き換える
		solo.clickOnView(solo.getView(R.id.status_text_view));

		getInstrumentation().waitForIdleSync();

		assertEquals(View.VISIBLE, solo.getView(R.id.notification_modal)
				.getVisibility());
	}

	public void test06はいを押下して閉じ停車中に戻る() {
		test05停車中に管理者から連絡が来たら表示();
		
		solo.clickOnButton("はい");

		getInstrumentation().waitForIdleSync();

		assertEquals(View.GONE, solo.getView(R.id.notification_modal)
				.getVisibility());
		assertEquals(View.VISIBLE, solo.getView(R.id.waiting_layout)
				.getVisibility());

	}

	public void test07いいえを押下して閉じ停車中に戻る() {
		test05停車中に管理者から連絡が来たら表示();
		
		solo.clickOnButton("いいえ");

		getInstrumentation().waitForIdleSync();

		assertEquals(View.GONE, solo.getView(R.id.notification_modal)
				.getVisibility());

		assertEquals(View.VISIBLE, solo.getView(R.id.waiting_layout)
				.getVisibility());
	}

	public void test08管理画面中に管理者から連絡が来たら表示() {
		test01起動時は非表示();

//		solo.clickOnButton("運行管理");
		solo.clickOnButton("管理");
		
		//TODO 管理者からの連絡部分が実装されたら置き換える
		solo.clickOnView(solo.getView(R.id.status_text_view));

		getInstrumentation().waitForIdleSync();

		assertEquals(View.VISIBLE, solo.getView(R.id.notification_modal)
				.getVisibility());
	}

	public void test09はいを押下して閉じ運行管理画面に戻る() {
		test08管理画面中に管理者から連絡が来たら表示();
		
		solo.clickOnButton("はい");

		getInstrumentation().waitForIdleSync();

		assertEquals(View.GONE, solo.getView(R.id.notification_modal)
				.getVisibility());
		assertEquals(View.VISIBLE, solo.getView(R.id.config_modal)
				.getVisibility());

	}

	public void test10いいえを押下して閉じ運行管理画面に戻る() {
		test08管理画面中に管理者から連絡が来たら表示();
		
		solo.clickOnButton("いいえ");

		getInstrumentation().waitForIdleSync();

		assertEquals(View.GONE, solo.getView(R.id.notification_modal)
				.getVisibility());

		assertEquals(View.VISIBLE, solo.getView(R.id.config_modal)
				.getVisibility());
	}
	
	public void test11地図画面中に管理者から連絡が来たら表示() {
		test01起動時は非表示();

		solo.clickOnButton("地図");
		
		//TODO 管理者からの連絡部分が実装されたら置き換える
		solo.clickOnView(solo.getView(R.id.status_text_view));

		getInstrumentation().waitForIdleSync();

		assertEquals(View.VISIBLE, solo.getView(R.id.notification_modal)
				.getVisibility());
	}

	public void test12はいを押下して閉じ地図画面に戻る() {
		test11地図画面中に管理者から連絡が来たら表示();
		
		solo.clickOnButton("はい");

		getInstrumentation().waitForIdleSync();

		assertEquals(View.GONE, solo.getView(R.id.notification_modal)
				.getVisibility());
		assertEquals(View.VISIBLE, solo.getView(R.id.navigation_modal)
				.getVisibility());

	}

	public void test12いいえを押下して閉じ地図画面に戻る() {
		test11地図画面中に管理者から連絡が来たら表示();
		
		solo.clickOnButton("いいえ");

		getInstrumentation().waitForIdleSync();

		assertEquals(View.GONE, solo.getView(R.id.notification_modal)
				.getVisibility());

		assertEquals(View.VISIBLE, solo.getView(R.id.navigation_modal)
				.getVisibility());
	}
	
	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
	}
}