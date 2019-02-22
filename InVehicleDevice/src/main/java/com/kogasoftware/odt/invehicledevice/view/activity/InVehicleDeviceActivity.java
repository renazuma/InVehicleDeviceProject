package com.kogasoftware.odt.invehicledevice.view.activity;

import android.app.Activity;
import android.os.Bundle;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.model.contentprovider.table.ServiceProvider;
import com.kogasoftware.odt.invehicledevice.model.contentprovider.table.VehicleNotification;
import com.kogasoftware.odt.invehicledevice.presenter.PermissionChecker;
import com.kogasoftware.odt.invehicledevice.presenter.broadcastReceiver.BroadcastReceiverPresenter;
import com.kogasoftware.odt.invehicledevice.presenter.loader.LoaderPresenter;
import com.kogasoftware.odt.invehicledevice.presenter.service.ServicePresenter;
import com.kogasoftware.odt.invehicledevice.view.fragment.AdminVehicleNotificationFragment;
import com.kogasoftware.odt.invehicledevice.view.fragment.AirplaneModeAlertDialogFragment;
import com.kogasoftware.odt.invehicledevice.view.fragment.OperationListFragment;
import com.kogasoftware.odt.invehicledevice.view.fragment.OrderedOperationFragment;
import com.kogasoftware.odt.invehicledevice.view.fragment.ScheduleVehicleNotificationFragment;
import com.kogasoftware.odt.invehicledevice.view.fragment.SignInFragment;
import com.kogasoftware.odt.invehicledevice.view.fragment.VehicleNotificationAlertFragment;

import java.util.List;

/**
 * 全体の大枠。サインイン前はSignInFragmentを表示し、サインイン後は、自治体に依存して「運行予定一覧画面」か「順番に運行を進める画面」を表示する
 */
public class InVehicleDeviceActivity extends Activity {

  // 通知表示遅延秒数
  public static final Integer VEHICLE_NOTIFICATION_ALERT_DELAY_MILLIS = 5000;

  // インスタンス変数
  private Boolean destroyed = true;
  private ServiceProvider serviceProvider;
  private LoaderPresenter loaderPresenter;
  private BroadcastReceiverPresenter broadcastReceiverPresenter;
  private ServicePresenter servicePresenter;

  public void setServiceProvider(ServiceProvider serviceProvider) {
    this.serviceProvider = serviceProvider;
  }


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.destroyed = false;
    (new PermissionChecker(this)).check();
    setContentView(R.layout.in_vehicle_device_activity);
    initInstances();
    servicePresenter.onCreate();
    broadcastReceiverPresenter.onCreate();
    loaderPresenter.onCreate();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    loaderPresenter.onDestroy();
    broadcastReceiverPresenter.onDestroy();
    servicePresenter.onDestroy();
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


  private void initInstances() {
    // Loaderを使用するためには、onStartより前の時点で、一度getLoaderManagerを実行しておく必要がある。
    getLoaderManager();

    // TODO: presenterにActivityをそのまま渡しているのは良くない。
    // TODO: やり方を変えたいが、大改造が必要になるので、後回し。
    servicePresenter = new ServicePresenter(this);
    loaderPresenter = new LoaderPresenter(this);
    broadcastReceiverPresenter = new BroadcastReceiverPresenter(this);
  }

  // fragment表示メソッド
  // TODO: 本来は、各Fragmentのインスタンス化だけはonCreate時にActivityで行い、ロジックは各Fragmentに、トリガーは各Presenterに置きたい。
  public void showLoginFragment() {
    if (destroyed) { return; }
    SignInFragment.showModal(getFragmentManager());
  }

  public void showAirplaneModeAlertDialogFragment() {
    if (destroyed) { return; }
    AirplaneModeAlertDialogFragment.showDialog(getFragmentManager());
  }

  public void showNotificationAlertFragment() {
    if (destroyed || serviceProvider == null) { return; }
    VehicleNotificationAlertFragment.showModal(getFragmentManager());
  }

  public void showAdminNotificationsFragment(List<VehicleNotification> VehicleNotifications) {
    if (destroyed) { return; }
    AdminVehicleNotificationFragment.showModal(getFragmentManager(), VehicleNotifications);
  }

  public void showScheduleNotificationsFragment() {
    if (destroyed || serviceProvider == null) { return; }
    ScheduleVehicleNotificationFragment.showModal(getFragmentManager(), serviceProvider);
  }

  public void showOperationListFragment() {
    if (destroyed) { return; }
    OperationListFragment.showModal(getFragmentManager());
  }

  public void hideOperationListFragment() {
    if (destroyed) { return; }
    OperationListFragment.hideModal(getFragmentManager());
  }

  public void showOrderedOperationFragment() {
    if (destroyed) { return; }
    OrderedOperationFragment.showModal(getFragmentManager());
  }

  public void hideOrderedOperationFragment() {
    if (destroyed) { return; }
    OrderedOperationFragment.hideModal(getFragmentManager());
  }
}
