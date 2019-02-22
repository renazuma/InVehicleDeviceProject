package com.kogasoftware.odt.invehicledevice.view.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Html;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.model.contentprovider.table.ServiceProvider;
import com.kogasoftware.odt.invehicledevice.model.contentprovider.table.VehicleNotification;
import com.kogasoftware.odt.invehicledevice.model.contentprovider.task.SignInErrorBroadcastIntent;
import com.kogasoftware.odt.invehicledevice.presenter.service.logservice.LogService;
import com.kogasoftware.odt.invehicledevice.presenter.service.serviceunitstatuslogservice.ServiceUnitStatusLogService;
import com.kogasoftware.odt.invehicledevice.presenter.service.startupservice.AirplaneModeOnBroadcastIntent;
import com.kogasoftware.odt.invehicledevice.presenter.service.startupservice.StartupService;
import com.kogasoftware.odt.invehicledevice.view.fragment.NormalVehicleNotificationFragment;
import com.kogasoftware.odt.invehicledevice.view.fragment.OperationListFragment;
import com.kogasoftware.odt.invehicledevice.view.fragment.OrderedOperationFragment;
import com.kogasoftware.odt.invehicledevice.view.fragment.ScheduleVehicleNotificationFragment;
import com.kogasoftware.odt.invehicledevice.view.fragment.SignInFragment;
import com.kogasoftware.odt.invehicledevice.view.fragment.VehicleNotificationAlertFragment;
import com.kogasoftware.odt.invehicledevice.presenter.loader.LoaderFacade;
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.Fragments;

import java.util.List;

/**
 * 全体の大枠。サインイン前はSignInFragmentを表示し、サインイン後は、自治体に依存して「運行予定一覧画面」か「順番に運行を進める画面」を表示する
 */
public class InVehicleDeviceActivity extends Activity {

  // 通知表示遅延秒数
  public static final Integer VEHICLE_NOTIFICATION_ALERT_DELAY_MILLIS = 5000;

  // Activityで一意になる、Fragment用のTAG
  // TODO: 各Fragment内で管理しても一意に出来るので、その方が良いのでは？
  private static final String TAG_BASE = InVehicleDeviceActivity.class + "/";
  private static final String SCHEDULE_VEHICLE_NOTIFICATION_FRAGMENT_TAG = TAG_BASE + ScheduleVehicleNotificationFragment.class;
  private static final String ORDERED_OPERATION_FRAGMENT_TAG = TAG_BASE + OrderedOperationFragment.class;
  private static final String VEHICLE_NOTIFICATION_FRAGMENT_TAG = TAG_BASE + NormalVehicleNotificationFragment.class + "/%d";
  private static final String VEHICLE_NOTIFICATION_ALERT_FRAGMENT_TAG = TAG_BASE + VehicleNotificationAlertFragment.class;
  private static final String SIGN_IN_FRAGMENT_TAG = TAG_BASE + SignInFragment.class;
  public static final String OPERATION_LIST_FRAGMENT_TAG = TAG_BASE + OperationListFragment.class;
  private static final String AIRPLANE_MODE_ALERT_DIALOG_FRAGMENT_TAG = TAG_BASE + AirplaneModeAlertDialogFragment.class;

  // 権限の許可が必要なパーミッション
  private static final String[] MUST_GRANT_PERMISSIONS = new String[]{
          Manifest.permission.ACCESS_FINE_LOCATION,   // GPS
          Manifest.permission.WRITE_EXTERNAL_STORAGE, // SDカードへの書き込み
          Manifest.permission.READ_PHONE_STATE
  };

  // インスタンス変数
  private Boolean destroyed = true;
  private ServiceProvider serviceProvider;
  private LoaderFacade loaderFacade;

  // BroadcastReceiverの定義
  private final BroadcastReceiver signInErrorReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      new Handler(Looper.getMainLooper()).post(new Runnable() {
        @Override
        public void run() { showLoginFragment(); }
      });
    }
  };

  private final BroadcastReceiver airplaneModeOnReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      new Handler(Looper.getMainLooper()).post(new Runnable() {
        @Override
        public void run() { showAirplaneModeAlertDialogFragment(); }
      });
    }
  };

  public void setServiceProvider(ServiceProvider serviceProvider) {
    this.serviceProvider = serviceProvider;
  }

  // Activityのcallback
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    destroyed = false;
    requestPermissions();
    startServiceComponents();
    init_activity_instances();
    setContentView(R.layout.in_vehicle_device_activity);
    registerReceiverComponents();
    loaderFacade.initLoaders();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    loaderFacade.destroyLoaders();
    unregisterReceiverComponents();
    stopServiceComponents();
    destroyed = true;
  }

  @Override
  protected void onStart() {
    super.onStart();
  }

  @Override
  protected void onStop() {
    super.onStop();
  }


  // fragment表示メソッド、その他privateメソッド
  // TODO: Fragment表示用のメソッドは、Activityクラスに置かず、各Fragmentクラス等に分けた方が良い？
  public static class AirplaneModeAlertDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
      AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
      builder.setIcon(android.R.drawable.ic_dialog_info);
      builder.setMessage(Html.fromHtml("<big><big>機内モードをOFFにしてください</big></big>"));
      builder.setPositiveButton(Html.fromHtml("<big><big>確認</big></big>"), null);
      return builder.create();
    }
  }

  private void requestPermissions() {
    if (!this.isGrantedPermissions()) {
      ActivityCompat.requestPermissions(this, MUST_GRANT_PERMISSIONS, 1000);
    }
  }

  private boolean isGrantedPermissions() {
    for (String permission : MUST_GRANT_PERMISSIONS) {
      if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
        return false;
      }
    }
    return true;
  }

  private void startServiceComponents() {
    try {
      ContextCompat.startForegroundService(this, new Intent(this, ServiceUnitStatusLogService.class));
      ContextCompat.startForegroundService(this, new Intent(this, LogService.class));
      // TODO: StartupServiceについては、バックグラウンドで動き続けるべきかの判断が出来なかったため、据え置きとしている
      // ※8.0以降はバックグラウンドでは動かない
      startService(new Intent(this, StartupService.class));
    } catch (UnsupportedOperationException e) {
      // IsolatedContext
    }
  }

  private void stopServiceComponents() {
    stopService(new Intent(this, ServiceUnitStatusLogService.class));
    stopService(new Intent(this, LogService.class));
  }

  private void init_activity_instances() {
    // Loaderを使用するためには、onStartより前の時点で、一度getLoaderManagerを実行しておく必要がある。
    getLoaderManager();
    loaderFacade = new LoaderFacade(this);
  }

  private void registerReceiverComponents() {
    registerReceiver(signInErrorReceiver, new IntentFilter(SignInErrorBroadcastIntent.ACTION));
    registerReceiver(airplaneModeOnReceiver, new IntentFilter(AirplaneModeOnBroadcastIntent.ACTION));
  }


  private void unregisterReceiverComponents() {
    unregisterReceiver(signInErrorReceiver);
    unregisterReceiver(airplaneModeOnReceiver);
  }

  public void showLoginFragment() {
    if (destroyed) { return; }

    if (getFragmentManager().findFragmentByTag(SIGN_IN_FRAGMENT_TAG) != null) { return; }

    Fragments.showModalFragment(getFragmentManager(), SignInFragment.newInstance(), SIGN_IN_FRAGMENT_TAG);
  }

  private void showAirplaneModeAlertDialogFragment() {
    if (destroyed) { return; }

    FragmentManager fragmentManager = getFragmentManager();

    if (fragmentManager.findFragmentByTag(AIRPLANE_MODE_ALERT_DIALOG_FRAGMENT_TAG) == null) {
      AirplaneModeAlertDialogFragment airplaneModeAlertDialogFragment = new AirplaneModeAlertDialogFragment();
      airplaneModeAlertDialogFragment.show(fragmentManager,
              AIRPLANE_MODE_ALERT_DIALOG_FRAGMENT_TAG);
    }
  }

  public void showNotificationAlertFragment() {
    if (destroyed
            || serviceProvider == null
            || getFragmentManager().findFragmentByTag(VEHICLE_NOTIFICATION_ALERT_FRAGMENT_TAG) != null) {
      return;
    }

    Fragments.showModalFragment(getFragmentManager(),
            VehicleNotificationAlertFragment.newInstance(),
            VEHICLE_NOTIFICATION_ALERT_FRAGMENT_TAG);
  }

  public void showAdminNotificationsFragment(List<VehicleNotification> VehicleNotifications) {
    if (destroyed) { return; }

    for (final VehicleNotification vehicleNotification : VehicleNotifications) {
      final String tag = String.format(VEHICLE_NOTIFICATION_FRAGMENT_TAG, vehicleNotification.id);

      if (getFragmentManager().findFragmentByTag(tag) != null) { return; }

      Fragments.showModalFragment(
              getFragmentManager(),
              NormalVehicleNotificationFragment.newInstance(vehicleNotification),
              tag);
    }
  }

  public void showScheduleNotificationsFragment() {
    if (destroyed
            || serviceProvider == null
            || getFragmentManager().findFragmentByTag(
            SCHEDULE_VEHICLE_NOTIFICATION_FRAGMENT_TAG) != null) {
      return;
    }

    Fragments.showModalFragment(getFragmentManager(),
            ScheduleVehicleNotificationFragment.newInstance(!serviceProvider.operationListOnly),
            SCHEDULE_VEHICLE_NOTIFICATION_FRAGMENT_TAG);
  }

  public void showOperationListFragment() {
    if (destroyed) { return; }

    if (getFragmentManager().findFragmentByTag(OPERATION_LIST_FRAGMENT_TAG) != null) { return; }

    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
    fragmentTransaction.add(R.id.modal_fragment_container,
            OperationListFragment.newInstance(false),
            OPERATION_LIST_FRAGMENT_TAG);
    fragmentTransaction.commitAllowingStateLoss();
  }

  public void hideOperationListFragment() {
    if (destroyed) { return; }

    Fragment fragment = getFragmentManager().findFragmentByTag(OPERATION_LIST_FRAGMENT_TAG);

    if (fragment == null) { return; }

    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
    fragmentTransaction.remove(fragment);
    fragmentTransaction.commitAllowingStateLoss();
  }

  public void showOrderedOperationFragment() {

    if (destroyed) { return; }

    if (getFragmentManager().findFragmentByTag(ORDERED_OPERATION_FRAGMENT_TAG) != null) { return; }

    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
    fragmentTransaction.add(R.id.modal_fragment_container,
            OrderedOperationFragment.newInstance(),
            ORDERED_OPERATION_FRAGMENT_TAG);
    fragmentTransaction.commitAllowingStateLoss();
  }

  public void hideOrderedOperationFragment() {

    if (destroyed) { return; }

    Fragment fragment = getFragmentManager().findFragmentByTag(ORDERED_OPERATION_FRAGMENT_TAG);

    if (fragment == null) { return; }

    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
    fragmentTransaction.remove(fragment);
    fragmentTransaction.commitAllowingStateLoss();
  }
}
