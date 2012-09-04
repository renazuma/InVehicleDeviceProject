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
import com.google.common.base.Strings;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.SharedPreferencesKeys;

public class UploadThread extends Thread {
	private static final String TAG = UploadThread.class.getSimpleName();
	public static final String SHARED_PREFERENCES_NAME = UploadThread.class
			.getSimpleName() + ".sharedpreferences";
	public static final String ACTION_UPDATE_CREDENTIALS = UploadThread.class
			.getSimpleName() + ".ACTION_UPDATE_CREDENTIALS";
	private final Context context;
	private final BlockingQueue<File> uploadFiles;
	private final String deviceId;
	private final String bucket = "odt-android";
	private final BroadcastReceiver updateCredentialsBroadcastReceiver = new UpdateCredentialsBroadcastReceiver();

	public UploadThread(Context context, File dataDirectory,
			BlockingQueue<File> uploadFiles) {
		this.context = context;
		this.uploadFiles = uploadFiles;
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		deviceId = Strings.nullToEmpty(telephonyManager.getDeviceId());
	}

	private AWSCredentials getAWSCredentials() throws InterruptedException {
		while (true) {
			Thread.sleep(5000);
			SharedPreferences sharedPreferences = context.getSharedPreferences(
					SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
			String accessKeyId = sharedPreferences.getString(
					SharedPreferencesKeys.AWS_ACCESS_KEY_ID, "");
			String secretAccessKey = sharedPreferences.getString(
					SharedPreferencesKeys.AWS_SECRET_ACCESS_KEY, "");
			if (accessKeyId.isEmpty() || secretAccessKey.isEmpty()) {
				continue;
			}
			return new BasicAWSCredentials(accessKeyId, secretAccessKey);
		}
	}

	private void uploadOneFile(AmazonS3Client s3Client)
			throws InterruptedException {
		Thread.sleep(5000);
		File compressedLogFile = uploadFiles.take();
		if (!compressedLogFile.exists()) {
			Log.w(TAG, "compressedLogFile(" + compressedLogFile + ") not found");
			return;
		}
		Boolean succeed = false;
		try {
			PutObjectRequest putObjectRequest = new PutObjectRequest(bucket,
					"log/" + deviceId + "_" + compressedLogFile.getName(),
					compressedLogFile);
			s3Client.putObject(putObjectRequest);
			succeed = true;
		} finally {
			if (succeed) {
				if (!compressedLogFile.delete()) {
					Log.w(TAG, "!\"" + compressedLogFile + "\".delete()");
				}
			} else {
				uploadFiles.add(compressedLogFile);
			}
		}
	}

	@Override
	public void run() {
		Log.i(TAG, "start");
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION_UPDATE_CREDENTIALS);
		context.registerReceiver(updateCredentialsBroadcastReceiver,
				intentFilter);
		try {
			while (true) {
				try {
					Thread.sleep(5000);
					AmazonS3Client s3Client = new AmazonS3Client(
							getAWSCredentials());
					try {
						while (true) {
							uploadOneFile(s3Client);
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
}
