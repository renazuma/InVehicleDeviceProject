package com.kogasoftware.odt.invehicledevice.ui.loader;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;

import com.kogasoftware.odt.invehicledevice.contentprovider.table.InVehicleDevice;
import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;

/**
 * 車載器情報の同期用Loaderを操作するクラス
 */

public class InVehicleDeviceLoader {

  // TODO:InVehicleDeviceActivity配下で一意である必要がある。Activityクラスで管理した方が良い？
  public static final Integer LOADER_ID = 1;

  private InVehicleDeviceActivity inVehicleDeviceActivity;

  public InVehicleDeviceLoader(InVehicleDeviceActivity inVehicleDeviceActivity) {
    // TODO:Activityを使いまわすのは良くない気がする。別の方法があれば変えたい。
    this.inVehicleDeviceActivity = inVehicleDeviceActivity;
  }

  public void initLoader() {
    inVehicleDeviceActivity.getActivityLoaderManager().initLoader(LOADER_ID, null, callbacks);
  }

  public void destroyLoader() {
    inVehicleDeviceActivity.getActivityLoaderManager().destroyLoader(LOADER_ID);
  }

  private final LoaderManager.LoaderCallbacks<Cursor> callbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
      return new CursorLoader(inVehicleDeviceActivity,
              InVehicleDevice.CONTENT.URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
      if (data.moveToFirst()) { return; }

      inVehicleDeviceActivity.getActivityHandler().post(new Runnable() {
        @Override
        public void run() { inVehicleDeviceActivity.showLoginFragment(); }
      });
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
  };
}
