package com.kogasoftware.odt.invehicledevice.ui.fragment;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import junit.framework.AssertionFailedError;
import junitx.framework.ComparableAssert;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;

import com.google.common.base.Stopwatch;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.contentprovider.DatabaseHelper;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.InVehicleDevice;
import com.kogasoftware.odt.invehicledevice.mockserver.MockServer;
import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;
import com.kogasoftware.odt.invehicledevice.utils.TestUtils;
import com.robotium.solo.Condition;
import com.robotium.solo.Solo;

public class SignInFragmentTestCase
		extends
			ActivityInstrumentationTestCase2<InVehicleDeviceActivity> {
	static final String NO_CONNECTABLE_URL = "http://127.0.0.1:12347";
	static final String INVALID_URL = "%%%%%";
	static final String VALID_LOGIN = "valid_login";
	static final String INVALID_LOGIN = "invalid_login";
	static final String VALID_PASSWORD = "valid_password";
	static final String INVALID_PASSWORD = "invalid_password";

	MockServer server;
	SQLiteDatabase database;
	Solo solo;
	String validUrl;

	public SignInFragmentTestCase() {
		super(InVehicleDeviceActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		Context tc = getInstrumentation().getTargetContext();
		DatabaseHelper databaseHelper = new DatabaseHelper(tc);
		database = databaseHelper.getWritableDatabase();
		TestUtils.clear(PreferenceManager.getDefaultSharedPreferences(tc));
		TestUtils.clear(database);
		server = new MockServer(12346);
		server.start();
		validUrl = server.getUrl();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		try {
			TestUtils.dispose(server, database, solo);
		} finally {
			super.tearDown();
		}
	}

	void setConnectionUrl(String url) throws Exception {
		solo.scrollToTop();
		getInstrumentation().waitForIdleSync();
		solo.clickOnText(solo.getString(R.string.server_url));
		assertTrue(solo.waitForCondition(new Condition() {
			@Override
			public boolean isSatisfied() {
				try {
					solo.clearEditText(0);
					return true;
				} catch (AssertionFailedError e) {
					return false;
				}
			}
		}, 30 * 1000));
		solo.enterText(0, url);
		solo.clickOnButton(solo.getString(R.string.ok));
		getInstrumentation().waitForIdleSync();
	}

	void setLogin(String login) throws Exception {
		solo.scrollToTop();
		getInstrumentation().waitForIdleSync();
		solo.clickOnText(solo.getString(R.string.login));
		solo.clearEditText(0);
		solo.enterText(0, login);
		solo.clickOnButton(solo.getString(R.string.ok));
		getInstrumentation().waitForIdleSync();
	}

	void setPassword(String password) throws Exception {
		solo.scrollToTop();
		getInstrumentation().waitForIdleSync();
		solo.clickOnText(solo.getString(R.string.password));
		solo.clearEditText(0);
		solo.enterText(0, password);
		solo.clickOnButton(solo.getString(R.string.ok));
		getInstrumentation().waitForIdleSync();
	}

	void assertText(boolean expected, int resourceId) throws Exception {
		assertText(expected, solo.getString(resourceId));
	}

	void assertText(boolean expected, String s) throws InterruptedException {
		assertEquals(expected, solo.waitForText(s, 1, 10000, true, true));
	}

	public void testInvalidUrl() throws Exception {
		setConnectionUrl(INVALID_URL);
		setLogin(VALID_LOGIN);
		setPassword(VALID_PASSWORD);

		solo.clickOnButton(solo.getString(R.string.ok));
		assertText(true, String.format(Locale.US,
				getString(R.string.error_invalid_uri),
				getString(R.string.server_url)));
		solo.clickOnButton(solo.getString(R.string.ok));
	}

	public void testNoConnectableUrl() throws Exception {
		setConnectionUrl(NO_CONNECTABLE_URL);
		setLogin(VALID_LOGIN);
		setPassword(VALID_PASSWORD);

		solo.clickOnButton(solo.getString(R.string.ok));
		assertText(true, String.format(Locale.US,
				getString(R.string.error_connection),
				getString(R.string.server_url)));
		solo.clickOnButton(solo.getString(R.string.ok));
	}

	public void testInvalidLogin() throws Exception {
		setConnectionUrl(validUrl);
		setLogin(INVALID_LOGIN);
		setPassword(VALID_PASSWORD);

		solo.clickOnButton(solo.getString(R.string.ok));
		assertText(true, String.format(Locale.US,
				getString(R.string.error_invalid_login_or_password),
				getString(R.string.server_url)));
		solo.clickOnButton(solo.getString(R.string.ok));
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
	}

	public void testEmptyUrl() throws Exception {
		setConnectionUrl("");
		setLogin(VALID_LOGIN);
		setPassword(VALID_PASSWORD);

		solo.clickOnButton(solo.getString(R.string.ok));
		assertText(true, String.format(Locale.US,
				getString(R.string.error_null_or_empty),
				getString(R.string.server_url)));
		solo.clickOnButton(solo.getString(R.string.ok));
	}

	public void testEmptyLogin() throws Exception {
		setConnectionUrl(validUrl);
		setLogin("");
		setPassword(VALID_PASSWORD);

		solo.clickOnButton(solo.getString(R.string.ok));
		assertText(true, String.format(Locale.US,
				getString(R.string.error_null_or_empty),
				getString(R.string.login)));
		solo.clickOnButton(solo.getString(R.string.ok));
	}

	public void testEmptyPassword() throws Exception {
		setConnectionUrl(validUrl);
		setLogin(VALID_LOGIN);
		setPassword("");

		solo.clickOnButton(solo.getString(R.string.ok));
		assertText(true, String.format(Locale.US,
				getString(R.string.error_null_or_empty),
				getString(R.string.password)));
		solo.clickOnButton(solo.getString(R.string.ok));
	}

	public void testNonAsciiUrl() throws Exception {
		setConnectionUrl("http://日本語");
		setLogin(VALID_LOGIN);
		setPassword(VALID_PASSWORD);

		solo.clickOnButton(solo.getString(R.string.ok));
		assertText(true, String.format(Locale.US,
				getString(R.string.error_non_ascii),
				getString(R.string.server_url)));
		solo.clickOnButton(solo.getString(R.string.ok));
	}

	public void testNonAsciiLogin() throws Exception {
		setConnectionUrl(validUrl);
		setLogin("日本語");
		setPassword(VALID_PASSWORD);

		solo.clickOnButton(solo.getString(R.string.ok));
		assertText(true, String.format(Locale.US,
				getString(R.string.error_non_ascii), getString(R.string.login)));
		solo.clickOnButton(solo.getString(R.string.ok));
	}

	public void testNonAsciiPassword() throws Exception {
		setConnectionUrl(validUrl);
		setLogin(VALID_LOGIN);
		setPassword("日本語");

		solo.clickOnButton(solo.getString(R.string.ok));
		assertText(true, String.format(Locale.US,
				getString(R.string.error_non_ascii),
				getString(R.string.password)));
		solo.clickOnButton(solo.getString(R.string.ok));
	}

	String getString(int id) {
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

	public void testAuthenticationTokenExistsIfSucceed() throws Exception {
		setConnectionUrl(validUrl);
		setLogin(VALID_LOGIN);
		setPassword(VALID_PASSWORD);

		solo.clickOnButton(solo.getString(R.string.ok));
		assertText(false, R.string.an_error_occurred);
		Cursor cursor = database.query(InVehicleDevice.TABLE_NAME, null, null,
				null, null, null, null);
		try {
			Stopwatch stopwatch = new Stopwatch().start();
			while (1 != cursor.getCount()) {
				ComparableAssert.assertLesser(20,
						stopwatch.elapsed(TimeUnit.SECONDS));
				Thread.sleep(500);
			}
			cursor.moveToFirst();
			assertNotNull(cursor
					.getString(cursor
							.getColumnIndexOrThrow(InVehicleDevice.Columns.AUTHENTICATION_TOKEN)));
		} finally {
			cursor.close();
		}
	}
}
