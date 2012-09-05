package com.kogasoftware.odt.invehicledevice.service.logservice;

import java.io.File;
import java.util.concurrent.BlockingQueue;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.kogasoftware.odt.invehicledevice.BuildConfig;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.SharedPreferencesKeys;

public class UploadThread extends Thread {
	private static final String TAG = UploadThread.class.getSimpleName();
	public static final String SHARED_PREFERENCES_NAME = UploadThread.class
			.getSimpleName() + ".sharedpreferences";
	public static final Integer SHARED_PREFERENCES_CHECK_INTERVAL_MILLIS = 5000;
	public static final Integer UPLOAD_DELAY_MILLIS = 5000;
	public static final String ACTION_UPDATE_CREDENTIALS = UploadThread.class
			.getSimpleName() + ".ACTION_UPDATE_CREDENTIALS";
	private final Context context;
	private final BlockingQueue<File> uploadFiles;
	private final String bucket = "odt-android";
	private final BroadcastReceiver updateCredentialsBroadcastReceiver = new UpdateCredentialsBroadcastReceiver();
	private Optional<AmazonS3Client> mockAmazonS3Client = Optional.absent();

	public UploadThread(Context context, BlockingQueue<File> uploadFiles) {
		this.context = context;
		this.uploadFiles = uploadFiles;
	}

	@VisibleForTesting
	public static AWSCredentials getAWSCredentials(Context context)
			throws InterruptedException {
		while (true) {
			SharedPreferences sharedPreferences = context.getSharedPreferences(
					SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
			String accessKeyId = sharedPreferences.getString(
					SharedPreferencesKeys.AWS_ACCESS_KEY_ID, "");
			String secretAccessKey = sharedPreferences.getString(
					SharedPreferencesKeys.AWS_SECRET_ACCESS_KEY, "");
			if (accessKeyId.isEmpty() || secretAccessKey.isEmpty()) {
				Thread.sleep(SHARED_PREFERENCES_CHECK_INTERVAL_MILLIS);
				continue;
			}
			return new BasicAWSCredentials(accessKeyId, secretAccessKey);
		}
	}

	@VisibleForTesting
	public void uploadOneFile(AmazonS3Client s3Client, String deviceId)
			throws InterruptedException {
		Thread.sleep(UPLOAD_DELAY_MILLIS);
		File uploadFile = uploadFiles.take();
		if (!uploadFile.exists()) {
			Log.w(TAG, "uploadFile(" + uploadFile + ") not found");
			return;
		}
		Boolean succeed = false;
		try {
			PutObjectRequest putObjectRequest = new PutObjectRequest(bucket,
					"log/" + deviceId + "_" + uploadFile.getName(), uploadFile);
			s3Client.putObject(putObjectRequest);
			succeed = true;
		} finally {
			if (succeed) {
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
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String deviceId = Strings.nullToEmpty(telephonyManager.getDeviceId());

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION_UPDATE_CREDENTIALS);
		context.registerReceiver(updateCredentialsBroadcastReceiver,
				intentFilter);
		try {
			while (true) {
				try {
					Thread.sleep(5000);
					AmazonS3Client s3Client = getAmazonS3Client();
					try {
						while (true) {
							uploadOneFile(s3Client, deviceId);
						}
					} finally {
						s3Client.shutdown();
					}
				} catch (InterruptedException e) {
					break;
				} catch (AmazonServiceException e) {
					Log.w(TAG, e);
				} catch (AmazonClientException e) {
					Log.w(TAG, e);
				}
			}
		} finally {
			context.unregisterReceiver(updateCredentialsBroadcastReceiver);
		}
		Log.i(TAG, "exit");
	}

	private AmazonS3Client getAmazonS3Client() throws InterruptedException {
		if (!BuildConfig.DEBUG) {
			return new AmazonS3Client(getAWSCredentials(context));
		}
		if (mockAmazonS3Client.isPresent()) {
			return mockAmazonS3Client.get();
		}
		return new AmazonS3Client(getAWSCredentials(context));
	}
	
	@VisibleForTesting
	public void setMockAmazonS3Client(AmazonS3Client amazonS3Client) {
		mockAmazonS3Client = Optional.of(amazonS3Client);
	}
}
