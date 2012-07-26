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
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.SharedPreferencesKey;

public class UploadThread extends Thread {
	private static final String TAG = UploadThread.class.getSimpleName();
	private final Context context;
	private final BlockingQueue<File> compressedLogFiles;
	private final String deviceId;

	public UploadThread(Context context, File dataDirectory,
			BlockingQueue<File> compressedLogFiles) {
		this.context = context;
		this.compressedLogFiles = compressedLogFiles;
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		deviceId = telephonyManager.getDeviceId(); // TODO
	}

	@Override
	public void run() {
		Log.i(TAG, "start");
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		String accessKeyId = preferences.getString(
				SharedPreferencesKey.AWS_ACCESS_KEY_ID, "");
		String secretAccessKey = preferences.getString(
				SharedPreferencesKey.AWS_SECRET_ACCESS_KEY, "");
		String bucket = "odt-android";
		
		AmazonS3Client s3Client = new AmazonS3Client(new BasicAWSCredentials(
				accessKeyId, secretAccessKey));
		try {
			while (true) {
				Thread.sleep(5000);
				File compressedLogFile = compressedLogFiles.take();
				if (!compressedLogFile.exists()) {
					Log.w(TAG, "compressedLogFile(" + compressedLogFile + ") not found" );
					continue;
				}
				Boolean succeed = false;
				try {
					PutObjectRequest putObjectRequest = new PutObjectRequest(
							bucket, "log/" + deviceId + "_" + compressedLogFile.getName(),
							compressedLogFile);
					s3Client.putObject(putObjectRequest);
					succeed = true;
				} catch (AmazonServiceException e) {
					Log.w(TAG, e);
				} catch (AmazonClientException e) {
					Log.w(TAG, e);
				}
				if (succeed) {
					compressedLogFile.delete();
				} else {
					compressedLogFiles.add(compressedLogFile);
				}
			}
		} catch (InterruptedException e) {
		}
		Log.i(TAG, "exit");
	}
}
