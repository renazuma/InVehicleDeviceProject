package com.kogasoftware.odt.invehicledevice.presenter.service;

import android.content.Intent;
import android.support.v4.content.ContextCompat;

import com.kogasoftware.odt.invehicledevice.presenter.service.logservice.LogService;
import com.kogasoftware.odt.invehicledevice.presenter.service.serviceunitstatuslogservice.ServiceUnitStatusLogService;
import com.kogasoftware.odt.invehicledevice.presenter.service.startupservice.StartupService;
import com.kogasoftware.odt.invehicledevice.view.activity.InVehicleDeviceActivity;

/**
 * Created by ksc on 2019/02/22.
 */

public class ServicePresenter {

  private InVehicleDeviceActivity inVehicleDeviceActivity;

  public ServicePresenter(InVehicleDeviceActivity inVehicleDeviceActivity) {
    this.inVehicleDeviceActivity = inVehicleDeviceActivity;
  }

  public void onCreate() {
    try {
      ContextCompat.startForegroundService(inVehicleDeviceActivity, new Intent(inVehicleDeviceActivity, ServiceUnitStatusLogService.class));
      ContextCompat.startForegroundService(inVehicleDeviceActivity, new Intent(inVehicleDeviceActivity, LogService.class));
      // TODO: StartupServiceについては、バックグラウンドで動き続けるべきかの判断が出来なかったため、据え置きとしている
      // ※8.0以降はバックグラウンドでは動かない
      inVehicleDeviceActivity.startService(new Intent(inVehicleDeviceActivity, StartupService.class));
    } catch (UnsupportedOperationException e) {
      // IsolatedContext
    }
  }

  public void onDestroy() {
    inVehicleDeviceActivity.stopService(new Intent(inVehicleDeviceActivity, ServiceUnitStatusLogService.class));
    inVehicleDeviceActivity.stopService(new Intent(inVehicleDeviceActivity, LogService.class));
  }


}
