package com.kogasoftware.odt.invehicledevice.infra.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;

import com.kogasoftware.odt.invehicledevice.infra.contentprovider.task.SignInErrorBroadcastIntent;
import com.kogasoftware.odt.invehicledevice.view.activity.InVehicleDeviceActivity;
import com.kogasoftware.odt.invehicledevice.view.fragment.modal.SignInFragment;

/**
 * サインインエラーを検知した際に、サインイン画面を表示
 */

public class SignInErrorReceiver {

  private Context context;

  public SignInErrorReceiver(Context context) {
    this.context = context;
  }

  private final BroadcastReceiver signInErrorReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {

      // キャストしてfinalをしないと、runにinVehicleActivityを渡せない
      final InVehicleDeviceActivity inVehicleDeviceActivity = (InVehicleDeviceActivity)context;

      new Handler(Looper.getMainLooper()).post(new Runnable() {
        @Override
        public void run() { SignInFragment.showModal(inVehicleDeviceActivity); }
      });
    }
  };

  public void registerReceiver() {
    context.registerReceiver(signInErrorReceiver, new IntentFilter(SignInErrorBroadcastIntent.ACTION));
  }

  public void unregisterReceiver() {
    context.unregisterReceiver(signInErrorReceiver);
  }

}
