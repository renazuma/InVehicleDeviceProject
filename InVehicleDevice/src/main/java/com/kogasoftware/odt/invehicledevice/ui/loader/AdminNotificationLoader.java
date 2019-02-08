package com.kogasoftware.odt.invehicledevice.ui.loader;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.kogasoftware.odt.invehicledevice.contentprovider.table.VehicleNotification;
import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;

import java.util.List;

/**
 * 管理者通知をを購読し、通知を行うLoaderを操作するクラス
 */

public class AdminNotificationLoader {

  // TODO:InVehicleDeviceActivity配下で一意である必要がある。Activityクラスで管理した方が良い？
  public static final Integer LOADER_ID = 3;

  private InVehicleDeviceActivity inVehicleDeviceActivity;

  public AdminNotificationLoader(InVehicleDeviceActivity inVehicleDeviceActivity) {
    // TODO:Activityを使いまわすのは良くない気がする。別の方法があれば変えたい。
    this.inVehicleDeviceActivity = inVehicleDeviceActivity;
  }

  public void initLoader() {
    inVehicleDeviceActivity.getLoaderManager().initLoader(LOADER_ID, null, callbacks);
  }

  public void destroyLoader() {
    inVehicleDeviceActivity.getLoaderManager().destroyLoader(LOADER_ID);
  }

  private final LoaderManager.LoaderCallbacks<Cursor> callbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
      return new CursorLoader(
              inVehicleDeviceActivity,
              VehicleNotification.CONTENT.URI,
              null,
              VehicleNotification.WHERE_ADMIN_NOTIFICATION_FRAGMENT_CONTENT,
              null,
              null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
      if (data.getCount() == 0) { return; }

      Handler mainUIHandler = new Handler(Looper.getMainLooper());

      mainUIHandler.post(new Runnable() {
        @Override
        public void run() { inVehicleDeviceActivity.showNotificationAlertFragment(); }
      });

      // finalをした変数じゃないとpostDelayed内で使用出来ないので、先に作成している
      final List<VehicleNotification> vehicleNotifications = VehicleNotification.getAll(data);
      mainUIHandler.postDelayed(new Runnable() {
        @Override
        public void run() { inVehicleDeviceActivity.showAdminNotificationsFragment(vehicleNotifications); }
      }, inVehicleDeviceActivity.VEHICLE_NOTIFICATION_ALERT_DELAY_MILLIS);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
  };
}
