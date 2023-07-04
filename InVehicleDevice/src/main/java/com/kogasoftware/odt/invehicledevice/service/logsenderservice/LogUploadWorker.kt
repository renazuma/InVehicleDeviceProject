package com.kogasoftware.odt.invehicledevice.service.logsenderservice

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.PutObjectRequest
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.InVehicleDevice
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.ServiceProvider
import java.io.File

// HACK: リファクタリング候補
// 1. awsCredentials の synchronized は、元々は再ログイン等でサービスが多重起動した場合の対策っぽく、Workerを使うならおそらく不要なので、調査の上削除
// 2. prepareCredentials で、device_idの取得も行っている。名前がおかしいので修正するか分離する
// 3. deviceIdやawsCredentialsの取得はここで行わない
class LogUploadWorker(appContext: Context, workerParams: WorkerParameters):
        CoroutineWorker(appContext, workerParams) {

    companion object {
        private const val BUCKET = "odt-android"
        private const val TAG = "LogUploadWorker"
        private const val SERVICE_PROVIDER_TABLE_CHECK_INTERVAL: Long = 5000
        private const val IN_VEHICLE_DEVICE_TABLE_CHECK_INTERVAL: Long = 5000
        private const val DIRECTORY_CHECK_INTERVAL: Long = 5000
        private const val UPLOAD_DELAY_MILLIS: Long = 10000
    }

    private val awsCredentialsLock = Any()
    private var awsCredentials: AWSCredentials? = null
    private var deviceId: String? = null

    override suspend fun doWork(): Result {
        Log.i(TAG, "Start LogUploadWork.")

        try {
            prepareAWSCredentials()
            val s3Client = AmazonS3Client(awsCredentials)

            // 各ログファイルをS3にアップロード
            for (uploadFile in getLogFiles()) {
                Thread.sleep(UPLOAD_DELAY_MILLIS);

                s3Client.putObject(PutObjectRequest(BUCKET, "log/" + deviceId + "_" + uploadFile.name, uploadFile))

                Log.i(TAG, "\"$uploadFile\" uploaded")

                if (!uploadFile.delete()) {
                    Log.w(TAG, "!\"$uploadFile\".delete()")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, e.stackTraceToString(), e)
            return Result.retry()
        }

        Log.i(TAG, "Finish LogUploadWork.")
        return Result.success()
    }

    private fun prepareAWSCredentials(): Unit {
        while (awsCredentials == null && deviceId == null) {
            try {
                if (!prepareAwsCredential()) {
                    Log.i(TAG, "waiting for AWS Credentials")
                    continue
                }

                if (!prepareDeviceId()) {
                    Log.i(TAG, "waiting for DeviceID")
                    continue
                }
            } catch (e: Exception) {
                continue
            }
        }
    }

    private fun prepareAwsCredential(): Boolean {
        var credentialsObtained = false
        applicationContext.contentResolver.query(ServiceProvider.CONTENT.URI, null, null, null, null).use { cursor ->
            Thread.sleep(SERVICE_PROVIDER_TABLE_CHECK_INTERVAL);
            if (cursor!!.moveToFirst()) {
                val serviceProvider = ServiceProvider(cursor)
                if (serviceProvider.existAwsKeys()) {
                    synchronized(awsCredentialsLock) { awsCredentials = serviceProvider.basicAWSCredentials }
                    credentialsObtained = true
                }
            }
        }
        return credentialsObtained
    }

    private fun prepareDeviceId(): Boolean {
        var deviceIdObtained = false
        applicationContext.contentResolver.query(InVehicleDevice.CONTENT.URI, null, null, null, null).use { cursor ->
            Thread.sleep(IN_VEHICLE_DEVICE_TABLE_CHECK_INTERVAL);
            if (cursor!!.moveToFirst()) {
                deviceId = cursor.getString(cursor.getColumnIndexOrThrow(InVehicleDevice.Columns.LOGIN))
                deviceIdObtained = true
            }
        }
        return deviceIdObtained
    }

    private fun getLogFiles(): List<File> {
        val dataDirectory = getDataDirectory()
        return getCompressedLogFiles(dataDirectory)
    }

    private fun getDataDirectory(): File {
        val directory = File(applicationContext.filesDir, "log")
        while (true) {
            Thread.sleep(DIRECTORY_CHECK_INTERVAL);
            if (directory.exists() && directory.canWrite()) break;
        }
        return directory
    }

    private fun getCompressedLogFiles(directory: File): List<File> {
        return directory.listFiles { _, filename -> filename.endsWith(".gz") }?.toList() ?: emptyList()
    }
}
