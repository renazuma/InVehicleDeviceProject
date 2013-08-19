package com.kogasoftware.odt.invehicledevice.preference.test;

import java.util.Locale;

import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;

import com.jayway.android.robotium.solo.Solo;
import com.kogasoftware.odt.invehicledevice.preference.InVehicleDevicePreferenceActivity;
import com.kogasoftware.odt.invehicledevice.preference.R;

public class InVehicleDevicePreferenceTestCase extends
		ActivityInstrumentationTestCase2<InVehicleDevicePreferenceActivity> {
	private final String validUrl = "http://" + MockServer.getLocalServerHost()
			+ ":12345"; // staticにすると、Androidエミュレーターでなぜか内容がnullになることがある。
	private static final String NO_CONNECTABLE_URL = "http://127.0.0.1:12346";
	private static final String INVALID_URL = "%%%%%";
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

	private void assertText(boolean expected, int resourceId) throws Exception {
		assertText(expected, solo.getString(resourceId));
	}

	private void assertText(boolean expected, String s)
			throws InterruptedException {
		assertEquals(expected, solo.waitForText(s, 1, 10000, true));
	}

	public void testInvalidUrl() throws Exception {
		setConnectionUrl(INVALID_URL);
		setLogin(VALID_LOGIN);
		setPassword(VALID_PASSWORD);

		solo.clickOnButton(solo.getString(R.string.ok));
		assertText(true, String.format(Locale.US,
				getString(R.string.error_invalid_uri),
				getString(R.string.server_url)));
		assertFalse(getActivity().isFinishing());
	}

	public void testNoConnectableUrl() throws Exception {
		setConnectionUrl(NO_CONNECTABLE_URL);
		setLogin(VALID_LOGIN);
		setPassword(VALID_PASSWORD);

		solo.clickOnButton(solo.getString(R.string.ok));
		assertText(true, String.format(Locale.US,
				getString(R.string.error_connection),
				getString(R.string.server_url)));
		assertFalse(getActivity().isFinishing());
	}

	public void testInvalidLogin() throws Exception {
		setConnectionUrl(validUrl);
		setLogin(INVALID_LOGIN);
		setPassword(VALID_PASSWORD);

		solo.clickOnButton(solo.getString(R.string.ok));
		assertText(true, String.format(Locale.US,
				getString(R.string.error_invalid_login_or_password),
				getString(R.string.server_url)));
		assertFalse(getActivity().isFinishing());
	}

	public void testInvalidPassword() throws Exception {
		setConnectionUrl(validUrl);
		setLogin(VALID_LOGIN);
		setPassword(INVALID_PASSWORD);

		solo.clickOnButton(solo.getString(R.string.ok));
		assertText(true, String.format(Locale.US,
				getString(R.string.error_invalid_login_or_password),
				getString(R.string.server_url)));
		solo.clickOnButton(solo.getString(R.string.ok));
		assertFalse(getActivity().isFinishing());
	}

	public void testEmptyUrl() throws Exception {
		setConnectionUrl("");
		setLogin(VALID_LOGIN);
		setPassword(VALID_PASSWORD);

		solo.clickOnButton(solo.getString(R.string.ok));
		assertText(true, String.format(Locale.US,
				getString(R.string.error_null_or_empty),
				getString(R.string.server_url)));
		assertFalse(getActivity().isFinishing());
	}

	public void testEmptyLogin() throws Exception {
		setConnectionUrl(validUrl);
		setLogin("");
		setPassword(VALID_PASSWORD);

		solo.clickOnButton(solo.getString(R.string.ok));
		assertText(true, String.format(Locale.US,
				getString(R.string.error_null_or_empty),
				getString(R.string.login)));
		assertFalse(getActivity().isFinishing());
	}

	public void testEmptyPassword() throws Exception {
		setConnectionUrl(validUrl);
		setLogin(VALID_LOGIN);
		setPassword("");

		solo.clickOnButton(solo.getString(R.string.ok));
		assertText(true, String.format(Locale.US,
				getString(R.string.error_null_or_empty),
				getString(R.string.password)));
		assertFalse(getActivity().isFinishing());
	}

	public void testNonAsciiUrl() throws Exception {
		setConnectionUrl("http://日本語");
		setLogin(VALID_LOGIN);
		setPassword(VALID_PASSWORD);

		solo.clickOnButton(solo.getString(R.string.ok));
		assertText(true, String.format(Locale.US,
				getString(R.string.error_non_ascii),
				getString(R.string.server_url)));
		assertFalse(getActivity().isFinishing());
	}

	public void testNonAsciiLogin() throws Exception {
		setConnectionUrl(validUrl);
		setLogin("日本語");
		setPassword(VALID_PASSWORD);

		solo.clickOnButton(solo.getString(R.string.ok));
		assertText(true, String.format(Locale.US,
				getString(R.string.error_non_ascii), getString(R.string.login)));
		assertFalse(getActivity().isFinishing());
	}

	public void testNonAsciiPassword() throws Exception {
		setConnectionUrl(validUrl);
		setLogin(VALID_LOGIN);
		setPassword("日本語");

		solo.clickOnButton(solo.getString(R.string.ok));
		assertText(true, String.format(Locale.US,
				getString(R.string.error_non_ascii),
				getString(R.string.password)));
		assertFalse(getActivity().isFinishing());
	}

	private String getString(int id) {
		return getActivity().getString(id);
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
		setConnectionUrl(validUrl);
		setLogin(VALID_LOGIN);
		setPassword(VALID_PASSWORD);

		solo.clickOnButton(solo.getString(R.string.ok));
		assertText(false, R.string.an_error_occurred);
		assertTrue(getActivity().isFinishing());
	}

	public void testSendBroadcast() throws Exception {
		// testExitIfSucceed();
		// TODO:Broadcastなどを使ってテスト可能なように再実装
	}

	@Override
	public void tearDown() throws Exception {
		try {
			try {
				mockServer.interrupt();
			} finally {
				solo.finishOpenedActivities();
			}
		} finally {
			super.tearDown();
		}
	}
}
