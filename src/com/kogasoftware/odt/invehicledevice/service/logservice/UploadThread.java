package com.kogasoftware.odt.invehicledevice.service.logservice;

import java.io.File;
import java.util.concurrent.BlockingQueue;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.SharedPreferencesKey;

public class UploadThread extends Thread {
	private static final String TAG = UploadThread.class.getSimpleName();
	private final Context context;
	private final BlockingQueue<File> compressedLogFiles;
	private final String deviceId;
	private final String bucket = "odt-android";

	public UploadThread(Context context, File dataDirectory,
			BlockingQueue<File> compressedLogFiles) {
		this.context = context;
		this.compressedLogFiles = compressedLogFiles;
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		deviceId = telephonyManager.getDeviceId(); // TODO
	}

	private AWSCredentials getAWSCredentials() throws InterruptedException {
		while (true) {
			Thread.sleep(5000);
			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(context);
			String accessKeyId = preferences.getString(
					SharedPreferencesKey.AWS_ACCESS_KEY_ID, "");
			String secretAccessKey = preferences.getString(
					SharedPreferencesKey.AWS_SECRET_ACCESS_KEY, "");
			if (accessKeyId.isEmpty() || secretAccessKey.isEmpty()) {
				continue;
			}
			return new BasicAWSCredentials(accessKeyId, secretAccessKey);
		}
	}

	private void uploadOneFile(AmazonS3Client s3Client)
			throws InterruptedException {
		Thread.sleep(5000);
		File compressedLogFile = compressedLogFiles.take();
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
				compressedLogFile.delete();
			} else {
				compressedLogFiles.add(compressedLogFile);
			}
		}
	}

	@Override
	public void run() {
		Log.i(TAG, "start");
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
		Log.i(TAG, "exit");
	}
}
