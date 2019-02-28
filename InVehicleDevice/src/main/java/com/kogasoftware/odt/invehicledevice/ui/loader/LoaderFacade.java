package com.kogasoftware.odt.invehicledevice.ui.loader;

import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;

/**
 * Activityで扱うLoaderを操作するクラス
 */

public class LoaderFacade {

  // 管理対象Loader
  private AdminNotificationLoader adminNotificationLoader;
  private ScheduleNotificationLoader scheduleNotificationLoader;
  private ServiceProviderLoader serviceProviderLoader;
  private InVehicleDeviceLoader inVehicleDeviceLoader;

  public LoaderFacade(InVehicleDeviceActivity inVehicleDeviceActivity) {
    this.inVehicleDeviceLoader = new InVehicleDeviceLoader(inVehicleDeviceActivity);
    this.serviceProviderLoader = new ServiceProviderLoader(inVehicleDeviceActivity);
    this.adminNotificationLoader = new AdminNotificationLoader(inVehicleDeviceActivity);
    this.scheduleNotificationLoader = new ScheduleNotificationLoader(inVehicleDeviceActivity);
  }

  public void initLoaders() {
    inVehicleDeviceLoader.initLoader();
    serviceProviderLoader.initLoader();
    adminNotificationLoader.initLoader();
    scheduleNotificationLoader.initLoader();
  }

  public void destroyLoaders() {
    inVehicleDeviceLoader.destroyLoader();
    serviceProviderLoader.destroyLoader();
    adminNotificationLoader.destroyLoader();
    scheduleNotificationLoader.destroyLoader();
  }
}
