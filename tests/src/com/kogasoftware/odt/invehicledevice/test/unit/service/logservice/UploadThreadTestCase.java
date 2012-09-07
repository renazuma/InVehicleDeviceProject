package com.kogasoftware.odt.invehicledevice.test.unit.service.logservice;

import java.io.File;
import java.nio.charset.Charset;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.common.base.Charsets;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.SharedPreferencesKeys;
import com.kogasoftware.odt.invehicledevice.service.logservice.UploadThread;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.test.AndroidTestCase;

import static org.mockito.Mockito.*;

public class UploadThreadTestCase extends AndroidTestCase {
	Charset c = Charsets.UTF_16BE;
	
	public void setUp() throws Exception {
		super.setUp();
		getContext().getSharedPreferences(
				UploadThread.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
				.edit().clear().apply();
	}

	public void testStart() throws Exception {
		BlockingQueue<File> files = new LinkedBlockingQueue<File>();
		final CountDownLatch cdl = new CountDownLatch(1);
		UploadThread ut = new UploadThread(getContext(), files) {
			Integer counter = 0;
			@Override
			public void uploadOneFile(AmazonS3Client s3Client, String deviceId) throws InterruptedException {
				cdl.countDown();
				// InterruptedException以外の例外が飛んでも再び呼ばれるかのチェック
				counter++;
				switch (counter) {
				case 0:
					break;
				case 1:
					throw new AmazonServiceException("foo");
				case 2:
					throw new AmazonServiceException("bar");
				case 3:
					break;
				case 4:
					throw new InterruptedException("test ok !");
				}
			}
		};

		ut.start();
		assertFalse(cdl
				.await((int) (UploadThread.SHARED_PREFERENCES_CHECK_INTERVAL_MILLIS * 1.2),
						TimeUnit.MILLISECONDS));
		
		SharedPreferences.Editor e = getContext().getSharedPreferences(
				UploadThread.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
				.edit();
		String id = "ididid";
		String key = "keykeykey";
		e.putString(SharedPreferencesKeys.AWS_ACCESS_KEY_ID, id);
		e.putString(SharedPreferencesKeys.AWS_SECRET_ACCESS_KEY, key);
		e.apply();
		
		assertTrue(cdl
				.await((int) (UploadThread.SHARED_PREFERENCES_CHECK_INTERVAL_MILLIS * 1.2),
						TimeUnit.MILLISECONDS));

		ut.join(UploadThread.SHARED_PREFERENCES_CHECK_INTERVAL_MILLIS * 10);
		assertFalse(ut.isAlive());
	}
	
	public void testUploadOneFile() throws Exception {
		BlockingQueue<File> files = new LinkedBlockingQueue<File>();
		UploadThread ut = new UploadThread(getContext(), files);

		StringBuilder d1 = new StringBuilder("compress data");
		for (int i = 0; i < 500; ++i) {
			d1.append("data" + i);
		}

		ByteArrayOutputStream d2 = new ByteArrayOutputStream();
		for (int i = 0; i < 16 * 1024; ++i) {
			d2.write((byte) i);
		}
		d2.close();

		File f1 = new File(Environment.getExternalStorageDirectory(), "foo");
		File fe = getContext().getFileStreamPath("empty");
		File f2 = getContext().getFileStreamPath("foobar");

		FileUtils.writeByteArrayToFile(f1, d1.toString().getBytes(c));
		FileUtils.touch(fe);
		FileUtils.writeByteArrayToFile(f2, d2.toByteArray());

		files.add(f1);
		files.add(fe);
		files.add(f2);

		// 成功させる
		AmazonS3Client s3Client = mock(AmazonS3Client.class);
		ut.uploadOneFile(s3Client, "");
		ArgumentCaptor<PutObjectRequest> argument = ArgumentCaptor
				.forClass(PutObjectRequest.class);
		verify(s3Client).putObject(argument.capture());
		assertEquals(f1, argument.getValue().getFile());
		assertFalse(files.contains(f1));
		assertTrue(files.contains(fe));
		assertTrue(files.contains(f2));
		
		// 空ファイルが無視される
		s3Client = mock(AmazonS3Client.class);
		ut.uploadOneFile(s3Client, "");
		verifyZeroInteractions(s3Client);
		assertFalse(files.contains(fe));
		assertTrue(files.contains(f2));
		
		// 失敗させる1
		s3Client = mock(AmazonS3Client.class);
		when(s3Client.putObject(Mockito.<PutObjectRequest> any())).thenThrow(
				new AmazonClientException("error"));
		try {
			ut.uploadOneFile(s3Client, "");
			fail();
		} catch (AmazonClientException e) {
		}
		verify(s3Client).putObject(argument.capture());
		assertEquals(f2, argument.getValue().getFile());
		assertTrue(files.contains(f2));
		
		// 失敗させる2
		s3Client = mock(AmazonS3Client.class);
		when(s3Client.putObject(Mockito.<PutObjectRequest> any())).thenThrow(
				new AmazonServiceException("error"));
		try {
			ut.uploadOneFile(s3Client, "");
			fail();
		} catch (AmazonServiceException e) {
		}
		verify(s3Client).putObject(argument.capture());
		assertEquals(f2, argument.getValue().getFile());
		assertTrue(files.contains(f2));
		
		// 成功させる
		s3Client = mock(AmazonS3Client.class);
		ut.uploadOneFile(s3Client, "");
		verify(s3Client).putObject(argument.capture());
		assertEquals(f2, argument.getValue().getFile());
		assertFalse(files.contains(f2));
	}

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
