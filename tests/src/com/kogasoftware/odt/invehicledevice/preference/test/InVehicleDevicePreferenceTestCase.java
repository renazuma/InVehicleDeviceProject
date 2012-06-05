package com.kogasoftware.odt.invehicledevice.preference.test;

import android.app.Activity;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;

import com.jayway.android.robotium.solo.Solo;
import com.kogasoftware.odt.invehicledevice.logic.SharedPreferencesKey;
import com.kogasoftware.odt.invehicledevice.preference.InVehicleDevicePreferenceActivity;
import com.kogasoftware.odt.invehicledevice.preference.R;

public class InVehicleDevicePreferenceTestCase extends
		ActivityInstrumentationTestCase2<InVehicleDevicePreferenceActivity> {

	private static final String URL = "http://10.1.10.161";
	private static final String LOGIN = "test_login";
	private static final String PASSWORD = "test_password";

	private Solo solo;

	public InVehicleDevicePreferenceTestCase() {
		super("com.kogasoftware.odt.invehicledevice.preference",
				InVehicleDevicePreferenceActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();

		solo = new Solo(getInstrumentation(), getActivity());
	}

	private void setConnectionUrl(String url) throws Exception {
		getInstrumentation().waitForIdleSync();
		solo.clickOnText(getInstrumentation().getTargetContext().getResources()
				.getString(R.string.connection_url));
		solo.clearEditText(0);
		solo.enterText(0, url);
		solo.sendKey(KeyEvent.KEYCODE_ENTER);
		solo.sendKey(KeyEvent.KEYCODE_ENTER);
		getInstrumentation().waitForIdleSync();
	}

	private void setLogin(String login) throws Exception {
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
		getInstrumentation().waitForIdleSync();
		solo.clickOnText(getInstrumentation().getTargetContext().getResources()
				.getString(R.string.password));
		solo.clearEditText(0);
		solo.enterText(0, password);
		solo.sendKey(KeyEvent.KEYCODE_ENTER);
		solo.sendKey(KeyEvent.KEYCODE_ENTER);
		getInstrumentation().waitForIdleSync();
	}

	private void assertToast(boolean expected, int resourceId) {
		getInstrumentation().waitForIdleSync();
		String s = getInstrumentation().getTargetContext().getResources()
				.getString(resourceId);
		boolean actual = false;
		for (int i = 0; i < 5; ++i) {
			if (solo.searchText(s, true)) {
				actual = true;
				break;
			}
		}
		for (int i = 0; i < 5; ++i) {
			if (!solo.searchText(s, true)) {
				break;
			}
		}
		assertEquals(expected, actual);
	}

	public void test不正なサーバーを入力() throws Exception {
		setConnectionUrl("https://localhost:43123");
		setLogin(LOGIN);
		setPassword(PASSWORD);

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

	public void test不正なログイン名を入力() throws Exception {
		setConnectionUrl(URL);
		setLogin("asdf");
		setPassword(PASSWORD);

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
						intent.getStringExtra(SharedPreferencesKey.SERVER_URL),
						URL);
				assertTrue(intent.getStringExtra(
						SharedPreferencesKey.SERVER_IN_VEHICLE_DEVICE_TOKEN)
						.length() > 0);
				return;
			}
		}
		fail();
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
	}
}
