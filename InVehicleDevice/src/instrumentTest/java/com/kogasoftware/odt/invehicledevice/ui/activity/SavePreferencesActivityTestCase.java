package com.kogasoftware.odt.invehicledevice.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.SharedPreferencesKeys;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.broadcast.Broadcasts;
import com.kogasoftware.odt.invehicledevice.ui.activity.SavePreferencesActivity;

public class SavePreferencesActivityTestCase extends
		ActivityInstrumentationTestCase2<SavePreferencesActivity> {
	SharedPreferences sp;
	boolean actionExitReceived = false;
	BroadcastReceiver br = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			actionExitReceived = true;
		}
	};

	public SavePreferencesActivityTestCase() {
		super(SavePreferencesActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		Thread.sleep(10 * 1000);

		sp = PreferenceManager.getDefaultSharedPreferences(getInstrumentation()
				.getTargetContext());

		SharedPreferences.Editor editor = sp.edit();
		editor.putString(SharedPreferencesKeys.SERVER_URL, "http://127.0.0.1");
		editor.putString(SharedPreferencesKeys.SERVER_IN_VEHICLE_DEVICE_TOKEN,
				"");
		editor.putBoolean(SharedPreferencesKeys.CLEAR_STATUS_BACKUP, false);
		editor.commit();
		actionExitReceived = false;
		IntentFilter i = new IntentFilter();
		i.addAction(Broadcasts.ACTION_EXIT);
		getInstrumentation().getTargetContext().getApplicationContext()
				.registerReceiver(br, i);
	}

	@Override
	protected void tearDown() throws Exception {
		// 必ず自動でfinishする
		assertTrue(getActivity().isFinishing());
		getInstrumentation().getTargetContext().getApplicationContext()
				.unregisterReceiver(br);
		super.tearDown();
	}

	public void testBundleを渡さなくてもエラーは発生しない() throws Exception {
		getActivity();
		getInstrumentation().waitForIdleSync();
		assertTrue(actionExitReceived);
	}

	public void testSharedPreferenceにデータが保存されACTION_EXITが送信される()
			throws Exception {
		String u = "http://example.com/foo/bar";
		String t = "token12345678";
		Bundle bundle = new Bundle();
		bundle.putString(SharedPreferencesKeys.SERVER_URL, u);
		bundle.putString(SharedPreferencesKeys.SERVER_IN_VEHICLE_DEVICE_TOKEN,
				t);
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.putExtras(bundle);
		setActivityIntent(intent);
		getActivity();
		getInstrumentation().waitForIdleSync();

		assertEquals(sp.getString(SharedPreferencesKeys.SERVER_URL, ""), u);
		assertEquals(sp.getString(
				SharedPreferencesKeys.SERVER_IN_VEHICLE_DEVICE_TOKEN, ""), t);

		assertTrue(sp.getBoolean(SharedPreferencesKeys.CLEAR_STATUS_BACKUP,
				false));
		assertTrue(actionExitReceived);
	}

	public void test空データを渡してもエラーは発生しない() throws Exception {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.putExtras(new Bundle());
		setActivityIntent(intent);
		getActivity();

		assertTrue(sp.getBoolean(SharedPreferencesKeys.CLEAR_STATUS_BACKUP,
				false));
		assertTrue(actionExitReceived);
	}
}
