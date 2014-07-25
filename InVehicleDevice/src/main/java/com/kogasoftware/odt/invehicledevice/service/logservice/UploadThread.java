package com.kogasoftware.odt.invehicledevice.service.logservice;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.Loader.OnLoadCompleteListener;
import android.database.Cursor;
import android.os.Looper;
import android.os.MessageQueue.IdleHandler;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.kogasoftware.odt.invehicledevice.contentprovider.model.ServiceProvider;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.ServiceProviders;

public class UploadThread extends Thread
		implements
			OnLoadCompleteListener<Cursor>,
			IdleHandler {
	private static final String TAG = UploadThread.class.getSimpleName();
	public static final String SHARED_PREFERENCES_NAME = UploadThread.class
			.getSimpleName() + ".sharedpreferences";
	public static final Integer SHARED_PREFERENCES_CHECK_INTERVAL_MILLIS = 5000;
	public static final Integer UPLOAD_DELAY_MILLIS = 5000;
	private static final int LOADER_ID = 0;
	private final Context context;
	private final BlockingQueue<File> uploadFiles;
	private final String bucket = "odt-android";
	private final String deviceId;
	private final Object awsCredentialsLock = new Object();
	private AWSCredentials awsCredentials;

	public UploadThread(Context context, BlockingQueue<File> uploadFiles) {
		this.context = context;
		this.uploadFiles = uploadFiles;
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		this.deviceId = Strings.nullToEmpty(telephonyManager.getDeviceId());
	}

	@VisibleForTesting
	public void uploadOneFile(AmazonS3Client s3Client, String deviceId)
			throws InterruptedException, IOException {
		Thread.sleep(UPLOAD_DELAY_MILLIS);
		File uploadFile = uploadFiles.poll();
		if (uploadFile == null) {
			return;
		}
		if (!uploadFile.exists()) {
			Log.w(TAG, "\"" + uploadFile + "\" not found");
			return;
		}
		Boolean succeed = false;
		try {
			PutObjectRequest putObjectRequest = new PutObjectRequest(bucket,
					"log/" + deviceId + "_" + uploadFile.getName(), uploadFile);
			try {
				s3Client.putObject(putObjectRequest);
			} catch (IllegalStateException e) {
				// java.lang.IllegalStateException: Content has been consumed
				// at
				// org.apache.http.entity.BasicHttpEntity.getContent(BasicHttpEntity.java:84)
				// at com.amazonaws.http.AmazonHttpClient.executeHelper(Unknown
				// Source)
				// at com.amazonaws.http.AmazonHttpClient.execute(Unknown
				// Source)
				// at com.amazonaws.services.s3.AmazonS3Client.invoke(Unknown
				// Source)
				// at com.amazonaws.services.s3.AmazonS3Client.putObject(Unknown
				// Source)
				// at
				// com.kogasoftware.odt.invehicledevice.service.logservice.UploadThread.uploadOneFile(UploadThread.java:75)
				// at
				// com.kogasoftware.odt.invehicledevice.service.logservice.UploadThread.run(UploadThread.java:106)
				throw new IOException(e);
			}
			succeed = true;
		} finally {
			if (succeed) {
				Log.i(TAG, "\"" + uploadFile + "\" uploaded");
				if (!uploadFile.delete()) {
					Log.w(TAG, "!\"" + uploadFile + "\".delete()");
				}
			} else {
				uploadFiles.add(uploadFile);
			}
		}
	}

	@Override
	public void run() {
		Log.i(TAG, "start");
		Looper.prepare();
		CursorLoader cursorLoader = new CursorLoader(context,
				ServiceProviders.CONTENT.URI, null, null, null, null);
		cursorLoader.registerListener(LOADER_ID, this);
		cursorLoader.startLoading();
		try {
			Looper.myQueue().addIdleHandler(this);
			Looper.loop();
		} finally {
			cursorLoader.unregisterListener(this);
			cursorLoader.cancelLoad();
			cursorLoader.stopLoading();
		}
		Log.i(TAG, "exit");
	}

	private AmazonS3Client getAmazonS3Client() throws IOException {
		synchronized (awsCredentialsLock) {
			if (awsCredentials == null) {
				throw new IOException("AWSCredentials not found");
			}
			return new AmazonS3Client(awsCredentials);
		}
	}

	@Override
	public void onLoadComplete(Loader<Cursor> loader, Cursor cursor) {
		if (!cursor.moveToFirst()) {
			synchronized (awsCredentialsLock) {
				awsCredentials = null;
			}
			return;
		}
		ServiceProvider serviceProvider = new ServiceProvider(cursor);
		if (serviceProvider.logAccessKeyIdAws == null
				|| serviceProvider.logSecretAccessKeyAws == null) {
			return;
		}
		synchronized (awsCredentialsLock) {
			awsCredentials = new BasicAWSCredentials(
					serviceProvider.logAccessKeyIdAws,
					serviceProvider.logSecretAccessKeyAws);
		}
	}

	@Override
	public boolean queueIdle() {
		try {
			AmazonS3Client s3Client = getAmazonS3Client();
			try {
				return queueIdle(s3Client);
			} finally {
				s3Client.shutdown();
			}
		} catch (IOException e) {
			return true;
		}
	}

	private boolean queueIdle(AmazonS3Client s3Client) {
		try {
			Thread.sleep(5000);
			uploadOneFile(s3Client, deviceId);
		} catch (InterruptedException e) {
			Looper.myLooper().quit();
			return false;
		} catch (AmazonServiceException e) {
			Log.w(TAG, e);
		} catch (AmazonClientException e) {
			Log.w(TAG, e);
		} catch (IOException e) {
			Log.e(TAG, e.toString(), e);
		}
		return true;
	}
}
