package com.kogasoftware.odt.invehicledevice.presenter;

import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;

import com.kogasoftware.odt.invehicledevice.service.logsenderservice.LogSenderService;

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
    }

    public void onDestroy() {
        context.stopService(new Intent(context, LogSenderService.class));
    }
}
