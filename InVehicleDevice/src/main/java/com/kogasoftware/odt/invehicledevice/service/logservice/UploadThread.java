package com.kogasoftware.odt.invehicledevice.service.logservice;

import android.content.Context;
import android.database.Cursor;
import android.os.Looper;
import android.os.MessageQueue.IdleHandler;
import android.util.Log;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.common.annotations.VisibleForTesting;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.InVehicleDevice;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.ServiceProvider;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

/**
 * ログをAmazon S3にアップロードするスレッド
 */
public class UploadThread extends Thread
        implements IdleHandler {
    private static final String TAG = UploadThread.class.getSimpleName();
    public static final Integer SERVICE_PROVIDER_TABLE_CHECK_INTERVAL = 5000;
    public static final Integer IN_VEHICLE_DEVICE_TABLE_CHECK_INTERVAL = 5000;
    public static final Integer UPLOAD_DELAY_MILLIS = 5000;
    private static final int LOADER_ID = 0;
    private final Context context;
    private final BlockingQueue<File> uploadFiles;
    private String deviceId;
    private final Object awsCredentialsLock = new Object();
    private AWSCredentials awsCredentials;

    public UploadThread(Context context, BlockingQueue<File> uploadFiles) {
        this.context = context;
        this.uploadFiles = uploadFiles;
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
        boolean succeed = false;
        try {
            String bucket = "odt-android";
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

        while (awsCredentials == null) {
            try (Cursor cursor = context.getContentResolver().query(ServiceProvider.CONTENT.URI, null, null, null, null)) {
                Thread.sleep(SERVICE_PROVIDER_TABLE_CHECK_INTERVAL);

                if (!cursor.moveToFirst()) {
                    Log.i(TAG, "waiting for AWS Credentials");
                    continue;
                }

                ServiceProvider serviceProvider = new ServiceProvider(cursor);
                if (!serviceProvider.existAwsKeys()) {
                    Log.i(TAG, "waiting for AWS Credentials");
                    continue;
                }

                synchronized (awsCredentialsLock) {
                    awsCredentials = serviceProvider.getBasicAWSCredentials();
                }
            } catch (InterruptedException e) {
                break;
            }

            try (Cursor cursor = context.getContentResolver().query(InVehicleDevice.CONTENT.URI, null, null, null, null)) {
                Thread.sleep(IN_VEHICLE_DEVICE_TABLE_CHECK_INTERVAL);

                if (!cursor.moveToFirst()) {
                    Log.i(TAG, "waiting for DeviceID");
                    continue;
                }

                deviceId = cursor.getString(cursor.getColumnIndexOrThrow(InVehicleDevice.Columns.LOGIN));
            } catch (InterruptedException e) {
                break;
            }
        }

        Looper.prepare();
        Looper.myQueue().addIdleHandler(this);
        Looper.loop();
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
