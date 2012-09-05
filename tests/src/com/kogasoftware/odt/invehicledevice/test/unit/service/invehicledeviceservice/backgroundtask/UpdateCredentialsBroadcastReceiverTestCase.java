package com.kogasoftware.odt.invehicledevice.test.unit.service.invehicledeviceservice.backgroundtask;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.SharedPreferencesKeys;
import com.kogasoftware.odt.invehicledevice.service.logservice.UpdateCredentialsBroadcastReceiver;
import com.kogasoftware.odt.invehicledevice.service.logservice.UploadThread;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.test.AndroidTestCase;

public class UpdateCredentialsBroadcastReceiverTestCase extends AndroidTestCase {
	UpdateCredentialsBroadcastReceiver br = new UpdateCredentialsBroadcastReceiver();

	public void testOnReceive() throws Exception {
		String id = "アイディ";
		String key = "キー";
		Intent i = new Intent();
		i.putExtra(SharedPreferencesKeys.AWS_ACCESS_KEY_ID, id);
		i.putExtra(SharedPreferencesKeys.AWS_SECRET_ACCESS_KEY, key);
		br.onReceive(getContext(), i);

		Thread.sleep(500); // 別スレッドで書き込みを行うので少し待つ

		SharedPreferences sp = getContext().getSharedPreferences(
				UploadThread.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
		assertEquals(
				id,
				sp.getString(SharedPreferencesKeys.AWS_ACCESS_KEY_ID, id
						+ " not found"));
		assertEquals(
				key,
				sp.getString(SharedPreferencesKeys.AWS_SECRET_ACCESS_KEY, key
						+ " not found"));
	}
}
