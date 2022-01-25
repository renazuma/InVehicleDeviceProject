package com.kogasoftware.odt.invehicledevice.presenter;

import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;

import com.kogasoftware.odt.invehicledevice.service.serviceunitstatuslogservice.ServiceUnitStatusLogService;

/**
 * 車載器データをサーバと同期
 */

public class UnitStatusLogSyncPresenter {

    private final Context context;

    public UnitStatusLogSyncPresenter(Context context) {
        this.context = context;
    }

    public void onCreate() {
        try {
            ContextCompat.startForegroundService(context, new Intent(context, ServiceUnitStatusLogService.class));
        } catch (UnsupportedOperationException e) {
            // IsolatedContext
        }
    }

    public void onDestroy() {
        context.stopService(new Intent(context, ServiceUnitStatusLogService.class));
    }
}
