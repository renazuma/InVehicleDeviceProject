package com.kogasoftware.odt.invehicledevice.test.unit.service.logservice;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import com.amazonaws.auth.AWSCredentials;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.SharedPreferencesKeys;
import com.kogasoftware.odt.invehicledevice.service.logservice.UploadThread;

import android.content.Context;
import android.content.SharedPreferences;
import android.test.AndroidTestCase;

public class UploadThreadTestCase extends AndroidTestCase {
	public void testGetAWSCredentials() throws Exception {
		SharedPreferences.Editor e = getContext().getSharedPreferences(
				UploadThread.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
				.edit();
		String id = "ididid";
		String key = "keykeykey";
		e.putString(SharedPreferencesKeys.AWS_ACCESS_KEY_ID, id);
		e.putString(SharedPreferencesKeys.AWS_SECRET_ACCESS_KEY, key);
		e.apply();
		AWSCredentials ac = UploadThread.getAWSCredentials(getContext());

		assertEquals(id, ac.getAWSAccessKeyId());
		assertEquals(key, ac.getAWSSecretKey());
	}

	public void testGetAWSCredentials_WaitForFillSharedPreferences()
			throws Exception {
		SharedPreferences sp = getContext().getSharedPreferences(
				UploadThread.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
		sp.edit().clear().apply();

		String id = "ididid";
		String key = "keykeykey";

		final CountDownLatch cdl = new CountDownLatch(1);
		final AtomicReference<AWSCredentials> ac = new AtomicReference<AWSCredentials>();
		Thread t = new Thread() {
			@Override
			public void run() {
				try {
					ac.set(UploadThread.getAWSCredentials(getContext()));
				} catch (InterruptedException e) {
				} finally {
					cdl.countDown();
				}
			}
		};
		t.start();

		assertFalse(cdl
				.await((int) (UploadThread.SHARED_PREFERENCES_CHECK_INTERVAL_MILLIS * 1.2),
						TimeUnit.MILLISECONDS));

		SharedPreferences.Editor e = sp.edit();
		e.putString(SharedPreferencesKeys.AWS_ACCESS_KEY_ID, id);
		e.clear();
		e.apply();

		assertFalse(cdl
				.await((int) (UploadThread.SHARED_PREFERENCES_CHECK_INTERVAL_MILLIS * 1.2),
						TimeUnit.MILLISECONDS));

		e = sp.edit();
		e.clear();
		e.putString(SharedPreferencesKeys.AWS_SECRET_ACCESS_KEY, key);
		e.apply();

		assertFalse(cdl
				.await((int) (UploadThread.SHARED_PREFERENCES_CHECK_INTERVAL_MILLIS * 1.2),
						TimeUnit.MILLISECONDS));

		e = sp.edit();
		e.clear();
		e.putString(SharedPreferencesKeys.AWS_ACCESS_KEY_ID, id);
		e.putString(SharedPreferencesKeys.AWS_SECRET_ACCESS_KEY, key);
		e.apply();

		assertTrue(cdl
				.await((int) (UploadThread.SHARED_PREFERENCES_CHECK_INTERVAL_MILLIS * 1.2),
						TimeUnit.MILLISECONDS));
		assertEquals(id, ac.get().getAWSAccessKeyId());
		assertEquals(key, ac.get().getAWSSecretKey());
	}
}
