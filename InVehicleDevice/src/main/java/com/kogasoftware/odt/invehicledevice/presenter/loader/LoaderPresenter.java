package com.kogasoftware.odt.invehicledevice.presenter.loader;

import com.kogasoftware.odt.invehicledevice.view.activity.InVehicleDeviceActivity;

/**
 * Activityで扱うLoaderを操作するクラス
 */

public class LoaderPresenter {

  // 管理対象Loader
  private AdminNotificationLoader adminNotificationLoader;
  private ScheduleNotificationLoader scheduleNotificationLoader;
  private ServiceProviderLoader serviceProviderLoader;
  private InVehicleDeviceLoader inVehicleDeviceLoader;

  public LoaderPresenter(InVehicleDeviceActivity inVehicleDeviceActivity) {
    this.inVehicleDeviceLoader = new InVehicleDeviceLoader(inVehicleDeviceActivity);
    this.serviceProviderLoader = new ServiceProviderLoader(inVehicleDeviceActivity);
    this.adminNotificationLoader = new AdminNotificationLoader(inVehicleDeviceActivity);
    this.scheduleNotificationLoader = new ScheduleNotificationLoader(inVehicleDeviceActivity);
  }

  public void onCreate() {
    inVehicleDeviceLoader.initLoader();
    serviceProviderLoader.initLoader();
    adminNotificationLoader.initLoader();
    scheduleNotificationLoader.initLoader();
  }

  public void onDestroy() {
    inVehicleDeviceLoader.destroyLoader();
    serviceProviderLoader.destroyLoader();
    adminNotificationLoader.destroyLoader();
    scheduleNotificationLoader.destroyLoader();
  }
}
