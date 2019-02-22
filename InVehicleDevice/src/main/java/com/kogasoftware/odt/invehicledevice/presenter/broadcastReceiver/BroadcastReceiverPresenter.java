package com.kogasoftware.odt.invehicledevice.presenter.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;

import com.kogasoftware.odt.invehicledevice.model.contentprovider.task.SignInErrorBroadcastIntent;
import com.kogasoftware.odt.invehicledevice.presenter.service.startupservice.AirplaneModeOnBroadcastIntent;
import com.kogasoftware.odt.invehicledevice.view.activity.InVehicleDeviceActivity;

/**
 * Created by ksc on 2019/02/22.
 */

public class BroadcastReceiverPresenter {

  private InVehicleDeviceActivity inVehicleDeviceActivity;

  public BroadcastReceiverPresenter(InVehicleDeviceActivity inVehicleDeviceActivity) {
    this.inVehicleDeviceActivity = inVehicleDeviceActivity;
  }

  // BroadcastReceiverの定義
  private final BroadcastReceiver signInErrorReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      new Handler(Looper.getMainLooper()).post(new Runnable() {
        @Override
        public void run() { inVehicleDeviceActivity.showLoginFragment(); }
      });
    }
  };

  private final BroadcastReceiver airplaneModeOnReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      new Handler(Looper.getMainLooper()).post(new Runnable() {
        @Override
        public void run() { inVehicleDeviceActivity.showAirplaneModeAlertDialogFragment(); }
      });
    }
  };

  public void onCreate() {
    inVehicleDeviceActivity.registerReceiver(signInErrorReceiver, new IntentFilter(SignInErrorBroadcastIntent.ACTION));
    inVehicleDeviceActivity.registerReceiver(airplaneModeOnReceiver, new IntentFilter(AirplaneModeOnBroadcastIntent.ACTION));
  }

  public void onDestroy() {
    inVehicleDeviceActivity.unregisterReceiver(signInErrorReceiver);
    inVehicleDeviceActivity.unregisterReceiver(airplaneModeOnReceiver);
  }
}
