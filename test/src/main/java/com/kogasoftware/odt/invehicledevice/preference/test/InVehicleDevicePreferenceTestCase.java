package com.kogasoftware.odt.invehicledevice.preference.test;

import android.app.Instrumentation;
import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
import android.widget.Toast;

import com.jayway.android.robotium.solo.Solo;
import com.kogasoftware.odt.invehicledevice.preference.InVehicleDevicePreferenceActivity;
import com.kogasoftware.odt.invehicledevice.preference.R;

public class InVehicleDevicePreferenceTestCase extends
		ActivityInstrumentationTestCase2<InVehicleDevicePreferenceActivity> {
	private static final String VALID_URL = "http://" + MockServer.getLocalServerHost() + ":12345";
	private static final String INVALID_URL = "http://127.0.0.1:12346";
	private static final String VALID_LOGIN = "valid_login";
	private static final String INVALID_LOGIN = "invalid_login";
	private static final String VALID_PASSWORD = "valid_password";
	private static final String INVALID_PASSWORD = "invalid_password";

	private MockServer mockServer;
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

		mockServer = new MockServer();
		mockServer.startAndWaitForBind();
	}

	private void setConnectionUrl(String url) throws Exception {
		solo.scrollToTop();
		getInstrumentation().waitForIdleSync();
		solo.clickOnText(solo.getString(R.string.server_url));
		solo.clearEditText(0);
		solo.enterText(0, url);
		solo.clickOnButton(solo.getString(R.string.ok));
		getInstrumentation().waitForIdleSync();
	}

	private void setLogin(String login) throws Exception {
		solo.scrollToTop();
		getInstrumentation().waitForIdleSync();
		solo.clickOnText(solo.getString(R.string.login));
		solo.clearEditText(0);
		solo.enterText(0, login);
		solo.clickOnButton(solo.getString(R.string.ok));
		getInstrumentation().waitForIdleSync();
	}

	private void setPassword(String password) throws Exception {
		solo.scrollToTop();
		getInstrumentation().waitForIdleSync();
		solo.clickOnText(solo.getString(R.string.password));
		solo.clearEditText(0);
		solo.enterText(0, password);
		solo.clickOnButton(solo.getString(R.string.ok));
		getInstrumentation().waitForIdleSync();
	}

	private void assertToast(boolean expected, int resourceId) throws Exception {
		assertToast(expected, solo.getString(resourceId));
	}

	private void assertToast(boolean show, String s)
			throws InterruptedException {
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

	public void callTestAssertToast(final Context context, final int duration)
			throws Exception {
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
		callTestAssertToast(getInstrumentation().getTargetContext(),
				Toast.LENGTH_SHORT);
		callTestAssertToast(getInstrumentation().getTargetContext(),
				Toast.LENGTH_LONG);
	}

	public void testInvalidUrl() throws Exception {
		setConnectionUrl(INVALID_URL);
		setLogin(VALID_LOGIN);
		setPassword(VALID_PASSWORD);

		solo.clickOnButton(solo.getString(R.string.ok));
		assertToast(true, R.string.an_error_occurred);
		assertFalse(getActivity().isFinishing());
	}

	public void testInvalidLogin() throws Exception {
		setConnectionUrl(VALID_URL);
		setLogin(INVALID_LOGIN);
		setPassword(VALID_PASSWORD);

		solo.clickOnButton(solo.getString(R.string.ok));
		assertToast(true, R.string.an_error_occurred);
		assertFalse(getActivity().isFinishing());
	}

	public void testInvalidPassword() throws Exception {
		setConnectionUrl(VALID_URL);
		setLogin(VALID_LOGIN);
		setPassword(INVALID_PASSWORD);

		solo.clickOnButton(solo.getString(R.string.ok));
		assertToast(true, R.string.an_error_occurred);
		while (solo
				.searchText(solo.getString(R.string.an_error_occurred), true)) {
			// Toastが消えるのを待つ
			Thread.sleep(1000);
		}
		assertFalse(getActivity().isFinishing());
	}

	public void testShowUrl() throws Exception {
		String url1 = "http://localhost:12345";
		String url2 = "http://localhost:54321";

		setConnectionUrl(url1);
		assertTrue(solo.searchText(url1, true));
		assertFalse(solo.searchText(url2, true));

		setConnectionUrl(url2);
		assertTrue(solo.searchText(url2, true));
		assertFalse(solo.searchText(url1, true));
	}

	public void testShowLogin() throws Exception {
		String login1 = "Hello";
		String login2 = "World";

		setLogin(login1);
		assertTrue(solo.searchText(login1, true));
		assertFalse(solo.searchText(login2, true));

		setLogin(login2);
		assertTrue(solo.searchText(login2, true));
		assertFalse(solo.searchText(login1, true));
	}

	public void testPasswordNotShown() throws Exception {
		String password1 = "qawsedrftg";
		String password2 = "abcde";

		setPassword(password1);
		assertFalse(solo.searchText(password1, true));
		assertFalse(solo.searchText(password2, true));

		setPassword(password2);
		assertFalse(solo.searchText(password1, true));
		assertFalse(solo.searchText(password2, true));
	}

	public void testExitIfSucceed() throws Exception {
		setConnectionUrl(VALID_URL);
		setLogin(VALID_LOGIN);
		setPassword(VALID_PASSWORD);

		solo.clickOnButton(solo.getString(R.string.ok));
		assertToast(false, R.string.an_error_occurred);
		assertTrue(getActivity().isFinishing());
	}

	public void testSendBroadcast() throws Exception {
		// testExitIfSucceed();
		// TODO:Broadcastなどを使ってテスト可能なように再実装
	}

	public void testShowErrorDetail() throws Exception {
		setConnectionUrl("%%%%");
		setLogin("foo");
		setPassword("bar");
		solo.clickOnButton(solo.getString(R.string.ok));
		assertToast(true, "Target host must not be null");

		setConnectionUrl("http://127.0.0.1:5432");
		solo.clickOnButton(solo.getString(R.string.ok));
		assertToast(true, "refused");
	}

	@Override
	public void tearDown() throws Exception {
		mockServer.interrupt();
		solo.finishOpenedActivities();
		super.tearDown();
	}
}
