package com.kogasoftware.odt.invehicledevice.presenter;

import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;
import androidx.work.BackoffPolicy;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.kogasoftware.odt.invehicledevice.service.logsenderservice.LogSenderService;
import com.kogasoftware.odt.invehicledevice.service.logsenderservice.LogUploadWorker;

import java.util.concurrent.TimeUnit;

/**
 * 車載器データをサーバと同期
 */

public class LogSenderPresenter {

    private final Context context;

    public LogSenderPresenter(Context context) {
        this.context = context;
    }


    public void onCreate() {
        try {
            ContextCompat.startForegroundService(context, new Intent(context, LogSenderService.class));
        } catch (UnsupportedOperationException e) {
            // IsolatedContext
        }

        executeUploadWorker();
    }

    public void onDestroy() {
        context.stopService(new Intent(context, LogSenderService.class));
    }

    private void executeUploadWorker() {
        PeriodicWorkRequest uploadWorkRequest =
            new PeriodicWorkRequest.Builder(LogUploadWorker.class, 1, TimeUnit.HOURS)
                .setBackoffCriteria(BackoffPolicy.LINEAR, PeriodicWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
                .build();

        // WorManagerはアプリの挙動と関係無く動き続けるので、前のWorkが残っている可能性を考え、一度キャンセルをする。
        WorkManager.getInstance(context).cancelUniqueWork("LogUploadWorker");
        WorkManager.getInstance(context).enqueueUniquePeriodicWork("LogUploadWorker", ExistingPeriodicWorkPolicy.REPLACE, uploadWorkRequest);
    }
}
