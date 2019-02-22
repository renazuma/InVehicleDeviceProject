package com.kogasoftware.odt.invehicledevice.model.contentprovider;

import java.util.List;
import java.util.concurrent.TimeUnit;

import junitx.framework.ComparableAssert;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.model.contentprovider.json.PlatformJson;
import com.kogasoftware.odt.invehicledevice.model.contentprovider.json.UserJson;
import com.kogasoftware.odt.invehicledevice.model.contentprovider.table.InVehicleDevice;
import com.kogasoftware.odt.invehicledevice.model.contentprovider.table.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.model.contentprovider.task.SignInErrorBroadcastIntent;
import com.kogasoftware.odt.invehicledevice.mockserver.MockServer;
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.TestUtils;

public class InVehicleDeviceContentProviderTestCase
		extends
			ProviderTestCase2<InVehicleDeviceContentProvider> {

	MockServer server;

	public InVehicleDeviceContentProviderTestCase() {
		super(InVehicleDeviceContentProvider.class,
				InVehicleDeviceContentProvider.AUTHORITY);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		TestUtils.clear(getProvider().getDatabase());
		server = new MockServer(12345);
		server.start();
	}

	@Override
	public void tearDown() throws Exception {
		try {
			TestUtils.dispose(server);
		} finally {
			super.tearDown();
		}
	}

	/**
	 * 正しいurl,login,passwordを入力すると、内部で通信が行われauthentication_tokenが取得される
	 */
	public void testInVehicleDeviceAuthentication() throws InterruptedException {
		insertInVehicleDevice(server.getUrl(), "valid_login", "valid_password");

		Stopwatch stopwatch = new Stopwatch().start();
		while (true) {
			ComparableAssert.assertLesser(5L,
					stopwatch.elapsed(TimeUnit.SECONDS));
			Cursor cursor = getMockContentResolver().query(
					InVehicleDevice.CONTENT.URI, null, null, null, null);
			try { // MockContentResolver.notifyChangeが使えないので、ポーリングする
				if (cursor.moveToFirst()) {
					String token = cursor
							.getString(cursor
									.getColumnIndexOrThrow(InVehicleDevice.Columns.AUTHENTICATION_TOKEN));
					if (server.authenticationToken.equals(token)) {
						break;
					}
				}
			} finally {
				cursor.close();
			}
			Thread.sleep(100);
		}
	}

	/**
	 * まちがったurl,login,passwordを入力すると、ブロードキャストが飛ぶ
	 */
	public void testInVehicleDeviceAuthenticationFailure()
			throws InterruptedException {
		assertInVehicleDeviceAuthenticationFailure("f'oo://bar://baz", "login",
				"password");
		assertInVehicleDeviceAuthenticationFailure(server.getUrl(),
				"invalid_login", "valid_password");
		assertInVehicleDeviceAuthenticationFailure(server.getUrl(),
				"valid_login", "invalid_password");
	}

	void assertInVehicleDeviceAuthenticationFailure(final String url,
			final String login, final String password)
			throws InterruptedException {
		getMockContext().getAndClearBroadcastIntents();
		insertInVehicleDevice(url, login, password);
		Stopwatch stopwatch = new Stopwatch().start();
		while (true) {
			ComparableAssert.assertLesser(20L,
					stopwatch.elapsed(TimeUnit.SECONDS));
			for (Intent intent : getMockContext().getAndClearBroadcastIntents()) {
				if (intent.getAction()
						.equals(SignInErrorBroadcastIntent.ACTION)) {
					return; // OK
				}
			}
			Thread.sleep(200);
		}
	}

	void insertInVehicleDevice(String url, String login, String password) {
		ContentValues values = new ContentValues();
		values.put(InVehicleDevice.Columns.URL, url);
		values.put(InVehicleDevice.Columns.LOGIN, login);
		values.put(InVehicleDevice.Columns.PASSWORD, password);
		Uri uri = InVehicleDevice.CONTENT.URI;
		ContentResolver resolver = getMockContentResolver();
		uri = resolver.insert(uri, values);
	}

	public void testQueryOperationSchedules() throws InterruptedException {
		List<UserJson> users1 = Lists.newArrayList(server.addUser("マイクロ 次郎"));
		List<UserJson> users2 = Lists.newArrayList(server.addUser("まつもと ゆきひろ"),
				server.addUser("はしもと ゆきなり"));
		PlatformJson p1 = server.addPlatform("南浦和");
		PlatformJson p2 = server.addPlatform("東川口");
		PlatformJson p3 = server.addPlatform("東府中");
		server.addOperationSchedule(p1, p2, users1, "09:00:00", "09:00:02", 50);
		server.addOperationSchedule(p2, p3, users2, "10:00:00", "10:00:02", 50);
		server.reservations.get(0).memo = "よやくメモ";

		insertInVehicleDevice(server.getUrl(), "valid_login", "valid_password");

		Stopwatch stopwatch = new Stopwatch().start();
		List<OperationSchedule> oss;
		while (true) {
			ComparableAssert.assertLesser(20L,
					stopwatch.elapsed(TimeUnit.SECONDS));
			Cursor cursor = getMockContentResolver().query(
					OperationSchedule.CONTENT.URI, null, null, null, null);
			try { // MockContentResolver.notifyChangeが使えないので、ポーリングする
				if (cursor.moveToFirst()) {
					oss = OperationSchedule.getAll(cursor);
					break;
				}
			} finally {
				cursor.close();
			}
			Thread.sleep(100);
		}

		assertEquals(3, oss.size());

		// Cursor osCursor = getContext().getContentResolver().query(
		// OperationSchedules.Columns, projection, selection, selectionArgs,
		// sortOrder);
		// try {
		// DatabaseUtils.dumpCursor(osCursor);
		// } finally {
		// osCursor.close();
		// }
	}
}
