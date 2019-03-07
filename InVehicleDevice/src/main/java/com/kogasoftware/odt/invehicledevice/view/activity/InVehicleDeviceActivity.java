package com.kogasoftware.odt.invehicledevice.view.activity;

import android.app.Activity;
import android.os.Bundle;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.model.contentprovider.table.ServiceProvider;
import com.kogasoftware.odt.invehicledevice.presenter.PermissionChecker;
import com.kogasoftware.odt.invehicledevice.presenter.broadcastReceiver.BroadcastReceiverPresenter;
import com.kogasoftware.odt.invehicledevice.presenter.loader.LoaderPresenter;
import com.kogasoftware.odt.invehicledevice.presenter.service.ServicePresenter;

/**
 * 全体の大枠。サインイン前はSignInFragmentを表示し、サインイン後は、自治体に依存して「運行予定一覧画面」か「順番に運行を進める画面」を表示する
 */
public class InVehicleDeviceActivity extends Activity {

  // 通知表示遅延秒数
  public static final Integer VEHICLE_NOTIFICATION_ALERT_DELAY_MILLIS = 5000;

  // インスタンス変数
  public Boolean destroyed = true; // TODO: 変数でライフサイクル管理をするのをやめたい。
  public ServiceProvider serviceProvider; // TODO: ServiceProviderの同期状態を変素で管理するのをやめたい。
  private LoaderPresenter loaderPresenter;
  private BroadcastReceiverPresenter broadcastReceiverPresenter;
  private ServicePresenter servicePresenter;

  // TODO: 不要にしたい
  public void setServiceProvider(ServiceProvider serviceProvider) {
    this.serviceProvider = serviceProvider;
  }


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.destroyed = false;
    (new PermissionChecker(this)).check();

    // このActivityは固有の表示を持たず、コンテナのみを提供しており、具体的な表示はFragmentが行う。
    setContentView(R.layout.in_vehicle_device_activity);

    // Loaderを使用するためには、onStartより前の時点で、一度getLoaderManagerを実行しておく必要がある。
    getLoaderManager();

    // TODO: 各presenterに渡すのは、InVehicleDeviceActivityではなく、context等にしておいた方が良いのでは？
    servicePresenter = new ServicePresenter(this);
    servicePresenter.onCreate();

    loaderPresenter = new LoaderPresenter(this);
    loaderPresenter.onCreate();

    broadcastReceiverPresenter = new BroadcastReceiverPresenter(this);
    broadcastReceiverPresenter.onCreate();
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
}
