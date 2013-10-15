package com.kogasoftware.odt.invehicledevice.testutil;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;

import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;

public class TestUtilAutoStartTestCase extends
		ActivityInstrumentationTestCase2<InVehicleDeviceActivity> {
	Context c;

	public TestUtilAutoStartTestCase() {
		super(InVehicleDeviceActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		c = getInstrumentation().getContext();
	}

	@Override
	public void tearDown() throws Exception {
		super.tearDown();
	}

	public void callTestDisableAutoStart() throws Exception {
		// TODO: テスト可能な自動起動方式を作って再実装

		// 自動起動をOFFにし、Activityを終了
		//TestUtil.disableAutoStart(c);
		//sendKeys(KeyEvent.KEYCODE_HOME, KeyEvent.KEYCODE_BACK);
		//
		// 非表示になることを確認
		//TestUtil.assertHide(c, InVehicleDeviceActivity.class);
		//
		// 自動起動をON
		//TestUtil.enableAutoStart(c);
		//
		// 表示されることを確認
		//TestUtil.assertShow(c, InVehicleDeviceActivity.class);
	}

	public void testDisableAutoStart1() throws Exception {
		callTestDisableAutoStart();
	}

	/**
	 * 二度目以降の実行に失敗することがあるので、複数回実行する
	 */
	public void testDisableAutoStart2() throws Exception {
		callTestDisableAutoStart();
	}

	/**
	 * 二度目以降の実行に失敗することがあるので、複数回実行する
	 */
	public void testDisableAutoStart3() throws Exception {
		callTestDisableAutoStart();
	}
}
