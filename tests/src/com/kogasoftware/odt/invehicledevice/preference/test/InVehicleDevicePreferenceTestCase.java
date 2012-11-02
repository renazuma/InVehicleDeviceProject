package com.kogasoftware.odt.invehicledevice.preference.test;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
import android.widget.Toast;

import com.jayway.android.robotium.solo.Solo;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.SharedPreferencesKeys;
import com.kogasoftware.odt.invehicledevice.preference.InVehicleDevicePreferenceActivity;
import com.kogasoftware.odt.invehicledevice.preference.R;

public class InVehicleDevicePreferenceTestCase extends
		ActivityInstrumentationTestCase2<InVehicleDevicePreferenceActivity> {

	private static final String URL = "http://10.1.10.161";
	private static final String LOGIN = "test_login";
	private static final String PASSWORD = "test_password";

	private Solo solo;

	public InVehicleDevicePreferenceTestCase() {
		super(InVehicleDevicePreferenceActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		Instrumentation i = getInstrumentation();

		// すでに車載器Activityが起動していることがあるので、BACK,HOMEキーを送信して終了
		try {
			i.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
		} catch (SecurityException e) {
		}
		try {
			i.sendKeyDownUpSync(KeyEvent.KEYCODE_HOME);
		} catch (SecurityException e) {
		}

		solo = new Solo(i, getActivity());
	}
	
	private void setConnectionUrl(String url) throws Exception {
		solo.scrollToTop();
		getInstrumentation().waitForIdleSync();
		solo.clickOnText(getInstrumentation().getTargetContext().getString(
				R.string.server_url));
		solo.clearEditText(0);
		solo.enterText(0, url);
		solo.sendKey(KeyEvent.KEYCODE_ENTER);
		solo.sendKey(KeyEvent.KEYCODE_ENTER);
		getInstrumentation().waitForIdleSync();
	}

	private void setLogin(String login) throws Exception {
		solo.scrollToTop();
		getInstrumentation().waitForIdleSync();
		solo.clickOnText(getInstrumentation().getTargetContext().getResources()
				.getString(R.string.login));
		solo.clearEditText(0);
		solo.enterText(0, login);
		solo.sendKey(KeyEvent.KEYCODE_ENTER);
		solo.sendKey(KeyEvent.KEYCODE_ENTER);
		getInstrumentation().waitForIdleSync();
	}

	private void setPassword(String password) throws Exception {
		solo.scrollToTop();
		getInstrumentation().waitForIdleSync();
		solo.clickOnText(getInstrumentation().getTargetContext().getResources()
				.getString(R.string.password));
		solo.clearEditText(0);
		solo.enterText(0, password);
		solo.sendKey(KeyEvent.KEYCODE_ENTER);
		solo.sendKey(KeyEvent.KEYCODE_ENTER);
		getInstrumentation().waitForIdleSync();
	}

	private void assertToast(boolean expected, int resourceId) throws Exception {
		assertToast(expected, getInstrumentation().getTargetContext()
				.getResources().getString(resourceId));
	}

	private void assertToast(boolean show, String s) throws InterruptedException {
		getInstrumentation().waitForIdleSync();
		boolean actual = false;
		int retry = 3;
		
		// searchTextの結果がshowと合うまで待つ
		for (int i = 0; i < retry; ++i) {
			actual = solo.searchText(s, true);
			if (actual == show) {
				break;
			}
		}
		
		if (actual) {
			// searchTextの結果がfalseになるまで待つ
			for (int i = 0; i < retry; ++i) {
				if (!solo.searchText(s, true)) {
					break;
				}
			}	
		}
		
		assertEquals(show, actual);
		if (!show) {
			return;
		}
	}
	
	public void callTestAssertToast(final Context context, final int duration) throws Exception {
		final String s1 = "あいうえお";
		final String s2 = "かきくけこ";

		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(context, s1, duration).show();
			}
		});
		assertToast(true, s1);
		assertToast(false, s1);
		assertToast(false, s2);

		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(context, "asdf" + s2 + "ASDF", duration).show();
			}
		});
		assertToast(true, s2);
		assertToast(false, s2);
		assertToast(false, s1);
	}
	
	public void testAssertToast() throws Exception {
		callTestAssertToast(getInstrumentation().getTargetContext(), Toast.LENGTH_SHORT);
		callTestAssertToast(getInstrumentation().getTargetContext(), Toast.LENGTH_LONG);
	}

	public void test不正なサーバーを入力() throws Exception {
		setConnectionUrl("https://localhost:43123");
		setLogin(LOGIN);
		setPassword(PASSWORD);

		solo.clickOnText(getInstrumentation().getTargetContext().getResources()
				.getString(R.string.ok));
		assertToast(true, R.string.an_error_occurred);
		assertFalse(getActivity().isFinishing());
	}

	public void test不正なログイン名を入力() throws Exception {
		setConnectionUrl(URL);
		setLogin("asdf");
		setPassword(PASSWORD);

		solo.clickOnText(getInstrumentation().getTargetContext().getResources()
				.getString(R.string.ok));
		assertToast(true, R.string.an_error_occurred);
		assertFalse(getActivity().isFinishing());
	}

	public void test不正なパスワードを入力() throws Exception {
		setConnectionUrl(URL);
		setLogin(LOGIN);
		setPassword("hjkl");

		solo.clickOnText(getInstrumentation().getTargetContext().getResources()
				.getString(R.string.ok));
		assertToast(true, R.string.an_error_occurred);
		while (solo.searchText(getInstrumentation().getTargetContext()
				.getResources().getString(R.string.an_error_occurred), true)) {
			// Toastが消えるのを待つ
			Thread.sleep(1000);
		}
		assertFalse(getActivity().isFinishing());
	}

	public void test接続先URLの入力内容が表示される() throws Exception {
		String url1 = "http://localhost:12345";
		String url2 = "http://localhost:54321";

		setConnectionUrl(url1);
		assertTrue(solo.searchText(url1, true));
		assertFalse(solo.searchText(url2, true));

		setConnectionUrl(url2);
		assertTrue(solo.searchText(url2, true));
		assertFalse(solo.searchText(url1, true));
	}

	public void testログイン名の入力内容が表示される() throws Exception {
		String login1 = "Hello";
		String login2 = "World";

		setLogin(login1);
		assertTrue(solo.searchText(login1, true));
		assertFalse(solo.searchText(login2, true));

		setLogin(login2);
		assertTrue(solo.searchText(login2, true));
		assertFalse(solo.searchText(login1, true));
	}

	public void testパスワードの入力内容は表示されない() throws Exception {
		String password1 = "qawsedrftg";
		String password2 = "abcde";

		setPassword(password1);
		assertFalse(solo.searchText(password1, true));
		assertFalse(solo.searchText(password2, true));

		setPassword(password2);
		assertFalse(solo.searchText(password1, true));
		assertFalse(solo.searchText(password2, true));
	}

	public void test正しい設定を行うと終了する() throws Exception {
		setConnectionUrl(URL);
		setLogin(LOGIN);
		setPassword(PASSWORD);

		solo.clickOnText(getInstrumentation().getTargetContext().getResources()
				.getString(R.string.ok));

		assertToast(false, R.string.an_error_occurred);

		Thread.sleep(1000);
		assertTrue(getActivity().isFinishing());
	}

	public void xtest正しい設定を行うとSavePreferencesActivityへIntentを渡す()
			throws Exception {

		test正しい設定を行うと終了する();

		for (Activity a : solo.getAllOpenedActivities()) {
			if (a.getClass()
					.getName()
					.equals("com.kogasoftware.odt.invehicledevice.ui.activity.SavePreferencesActivity")) {
				Intent intent = a.getIntent();
				assertEquals(
						intent.getStringExtra(SharedPreferencesKeys.SERVER_URL),
						URL);
				assertTrue(intent.getStringExtra(
						SharedPreferencesKeys.SERVER_IN_VEHICLE_DEVICE_TOKEN)
						.length() > 0);
				return;
			}
		}
		fail();
	}

	public void testエラー内容が表示される() throws Exception {
		setConnectionUrl("%%%%");
		setLogin("foo");
		setPassword("bar");
		solo.clickOnText(getInstrumentation().getTargetContext().getResources()
				.getString(R.string.ok));
		assertToast(true, "Target host must not be null");
		
		setConnectionUrl("http://127.0.0.1:5432");
		solo.clickOnText(getInstrumentation().getTargetContext().getResources()
				.getString(R.string.ok));
		assertToast(true, "refused");
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
	}
}
