package com.kogasoftware.odt.invehicledevice.presenter.loader;

import com.kogasoftware.odt.invehicledevice.view.activity.InVehicleDeviceActivity;

/**
 * Activityで扱う必要があるLoaderを操作するクラス
 */

public class LoaderPresenter {

  // 管理対象Loader
  private onCreateInVehicleDeviceLoader onCreateInVehicleDeviceLoader;
  private ServiceProviderLoader serviceProviderLoader;
  private AdminNotificationLoader adminNotificationLoader;
  private ScheduleNotificationLoader scheduleNotificationLoader;

  public LoaderPresenter(InVehicleDeviceActivity inVehicleDeviceActivity) {
    this.onCreateInVehicleDeviceLoader = new onCreateInVehicleDeviceLoader(inVehicleDeviceActivity);
    this.serviceProviderLoader = new ServiceProviderLoader(inVehicleDeviceActivity);
    this.adminNotificationLoader = new AdminNotificationLoader(inVehicleDeviceActivity);
    this.scheduleNotificationLoader = new ScheduleNotificationLoader(inVehicleDeviceActivity);
  }

  public void onCreate() {
    onCreateInVehicleDeviceLoader.initLoader();
    serviceProviderLoader.initLoader();
    adminNotificationLoader.initLoader();
    scheduleNotificationLoader.initLoader();
  }

  public void onDestroy() {
    onCreateInVehicleDeviceLoader.destroyLoader();
    serviceProviderLoader.destroyLoader();
    adminNotificationLoader.destroyLoader();
    scheduleNotificationLoader.destroyLoader();
  }
}
