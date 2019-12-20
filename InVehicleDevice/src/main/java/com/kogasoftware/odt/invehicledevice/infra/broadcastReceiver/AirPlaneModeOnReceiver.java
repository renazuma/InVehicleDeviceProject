package com.kogasoftware.odt.invehicledevice.infra.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;

import com.kogasoftware.odt.invehicledevice.service.startupservice.AirplaneModeOnBroadcastIntent;
import com.kogasoftware.odt.invehicledevice.view.activity.InVehicleDeviceActivity;
import com.kogasoftware.odt.invehicledevice.view.fragment.modal.AirplaneModeAlertDialogFragment;

/**
 * 機内モードを検知した際に、アラートダイアログを表示
 */

public class AirPlaneModeOnReceiver {

  private Context context;

  public AirPlaneModeOnReceiver(Context context) {
    this.context = context;
  }

  private final BroadcastReceiver airplaneModeOnReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {

      // キャストしてfinalをしないと、runにinVehicleActivityを渡せない
      final InVehicleDeviceActivity inVehicleDeviceActivity = (InVehicleDeviceActivity)context;

      new Handler(Looper.getMainLooper()).post(new Runnable() {
        @Override
        public void run() { AirplaneModeAlertDialogFragment.showDialog(inVehicleDeviceActivity);}
      });
    }
  };

  public void registerReceiver() {
    context.registerReceiver(airplaneModeOnReceiver, new IntentFilter(AirplaneModeOnBroadcastIntent.ACTION));
  }

  public void unregisterReceiver() {
    context.unregisterReceiver(airplaneModeOnReceiver);
  }

}
