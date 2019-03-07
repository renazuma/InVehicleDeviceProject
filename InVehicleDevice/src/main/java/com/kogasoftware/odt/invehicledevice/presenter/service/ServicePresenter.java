package com.kogasoftware.odt.invehicledevice.presenter.service;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;

import com.kogasoftware.odt.invehicledevice.presenter.service.logservice.LogService;
import com.kogasoftware.odt.invehicledevice.presenter.service.serviceunitstatuslogservice.ServiceUnitStatusLogService;
import com.kogasoftware.odt.invehicledevice.presenter.service.startupservice.StartupService;

/**
 * Created by ksc on 2019/02/22.
 */

public class ServicePresenter {

  private Context context;

  public ServicePresenter(Context context) {
    this.context = context;
  }

  public void onCreate() {
    try {
      ContextCompat.startForegroundService(context, new Intent(context, ServiceUnitStatusLogService.class));
      ContextCompat.startForegroundService(context, new Intent(context, LogService.class));
      // TODO: StartupServiceについては、バックグラウンドで動き続けるべきかの判断が出来なかったため、据え置きとしている
      // ※8.0以降はバックグラウンドでは動かない
      context.startService(new Intent(context, StartupService.class));
    } catch (UnsupportedOperationException e) {
      // IsolatedContext
    }
  }

  public void onDestroy() {
    context.stopService(new Intent(context, ServiceUnitStatusLogService.class));
    context.stopService(new Intent(context, LogService.class));
  }
}
