package com.kogasoftware.odt.invehicledevice.preference.test;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;
import com.kogasoftware.odt.invehicledevice.preference.InVehicleDevicePreferenceActivity;
import com.kogasoftware.odt.invehicledevice.preference.R;

public class InVehicleDevicePreferenceTestCase extends
		ActivityInstrumentationTestCase2<InVehicleDevicePreferenceActivity> {

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

	private void setConnectionUrl(String url) {
		solo.clickOnText(getInstrumentation().getTargetContext().getResources()
				.getString(R.string.connection_url));
		solo.clearEditText(0);
		solo.typeText(0, url);
		solo.clickOnButton(getInstrumentation().getTargetContext()
				.getResources().getString(android.R.string.ok));
	}

	private void setLogin(String login) {
		solo.clickOnText(getInstrumentation().getTargetContext().getResources()
				.getString(R.string.login));
		solo.clearEditText(0);
		solo.typeText(0, login);
		solo.clickOnButton(getInstrumentation().getTargetContext()
				.getResources().getString(android.R.string.ok));
	}

	private void setPassword(String password) {
		solo.clickOnText(getInstrumentation().getTargetContext().getResources()
				.getString(R.string.password));
		solo.clearEditText(0);
		solo.typeText(0, password);
		solo.clickOnButton(getInstrumentation().getTargetContext()
				.getResources().getString(android.R.string.ok));
	}

	public void test不正なサーバーを入力() throws InterruptedException {
		setConnectionUrl("https://localhost:43123");
		setLogin("admin");
		setPassword("admin");

		solo.clickOnText(getInstrumentation().getTargetContext().getResources()
				.getString(R.string.ok));
		assertTrue(solo.searchText(getInstrumentation().getTargetContext()
				.getResources().getString(R.string.an_error_occurred)));
		Thread.sleep(1000);
		assertFalse(getActivity().isFinishing());
	}

	public void test不正なログイン名を入力() throws InterruptedException {
		setConnectionUrl("http://10.0.2.2");
		setLogin("asdf");
		setPassword("admin");

		solo.clickOnText(getInstrumentation().getTargetContext().getResources()
				.getString(R.string.ok));
		assertTrue(solo.searchText(getInstrumentation().getTargetContext()
				.getResources().getString(R.string.an_error_occurred)));
		Thread.sleep(1000);
		assertFalse(getActivity().isFinishing());
	}

	public void test不正なパスワードを入力() throws InterruptedException {
		setConnectionUrl("http://10.0.2.2");
		setLogin("admin");
		setPassword("hjkl");

		solo.clickOnText(getInstrumentation().getTargetContext().getResources()
				.getString(R.string.ok));
		assertTrue(solo.searchText(getInstrumentation().getTargetContext()
				.getResources().getString(R.string.an_error_occurred)));
		Thread.sleep(1000);
		assertFalse(getActivity().isFinishing());
	}

	public void test接続先URLの入力内容が表示される() {
		String url1 = "http://localhost:12345";
		String url2 = "http://localhost:54321";

		setConnectionUrl(url1);
		assertTrue(solo.searchText(url1));
		assertFalse(solo.searchText(url2));

		setConnectionUrl(url2);
		assertFalse(solo.searchText(url1));
		assertTrue(solo.searchText(url2));
	}

	public void testログイン名の入力内容が表示される() {
		String login1 = "Hello";
		String login2 = "World";

		setLogin(login1);
		assertTrue(solo.searchText(login1));
		assertFalse(solo.searchText(login2));

		setLogin(login2);
		assertFalse(solo.searchText(login1));
		assertTrue(solo.searchText(login2));
	}

	public void testパスワードの入力内容は表示されない() {
		String password1 = "qawsedrftg";
		String password2 = "abcde";

		setPassword(password1);
		assertFalse(solo.searchText(password1));
		assertFalse(solo.searchText(password2));

		setPassword(password2);
		assertFalse(solo.searchText(password1));
		assertFalse(solo.searchText(password2));
	}

	public void test正しい設定を行うと終了する() throws InterruptedException {
		setConnectionUrl("http://10.1.10.161");
		setLogin("admin");
		setPassword("admin");

		solo.clickOnText(getInstrumentation().getTargetContext().getResources()
				.getString(R.string.ok));
		
		assertFalse(solo.searchText(getInstrumentation().getTargetContext()
				.getResources().getString(R.string.an_error_occurred)));
		
		Thread.sleep(1000);
		assertTrue(getActivity().isFinishing());
	}

	public void xtest正しい設定を行うとInVehicleDeviceActivityへIntentを渡す()
			throws InterruptedException {
		fail("stub!");
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
	}
}
