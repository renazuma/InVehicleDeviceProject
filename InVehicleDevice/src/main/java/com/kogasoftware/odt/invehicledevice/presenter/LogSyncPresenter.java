package com.kogasoftware.odt.invehicledevice.presenter;

import android.content.Context;
import android.content.Intent;
import androidx.core.content.ContextCompat;

import com.kogasoftware.odt.invehicledevice.service.logservice.LogService;

/**
 * 車載器データをサーバと同期
 */

public class LogSyncPresenter {

  private Context context;

  public LogSyncPresenter(Context context) {
    this.context = context;
  }


  public void onCreate() {
    try {
      ContextCompat.startForegroundService(context, new Intent(context, LogService.class));
    } catch (UnsupportedOperationException e) {
      // IsolatedContext
    }
  }

  public void onDestroy() {
    context.stopService(new Intent(context, LogService.class));
  }
}
