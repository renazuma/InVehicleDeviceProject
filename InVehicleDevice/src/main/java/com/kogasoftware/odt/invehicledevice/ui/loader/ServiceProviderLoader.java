package com.kogasoftware.odt.invehicledevice.ui.loader;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;

import com.kogasoftware.odt.invehicledevice.contentprovider.table.ServiceProvider;
import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;

/**
 * ServiceProviderの同期用Loaderを操作するクラス
 */

public class ServiceProviderLoader {

  // TODO:InVehicleDeviceActivity配下で一意である必要がある。Activityクラスで管理した方が良い？
  private static final Integer LOADER_ID = 2;

  private InVehicleDeviceActivity inVehicleDeviceActivity;

  public ServiceProviderLoader(InVehicleDeviceActivity inVehicleDeviceActivity) {
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
              ServiceProvider.CONTENT.URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
      Runnable showOperationListFragmentTask = new Runnable() {
        @Override
        public void run() {	inVehicleDeviceActivity.showOperationListFragment(); }
      };
      Runnable hideOperationListFragmentTask = new Runnable() {
        @Override
        public void run() { inVehicleDeviceActivity.hideOperationListFragment(); }
      };
      Runnable showOrderedOperationFragmentTask = new Runnable() {
        @Override
        public void run() {	inVehicleDeviceActivity.showOrderedOperationFragment();	}
      };
      Runnable hideOrderedOperationFragmentTask = new Runnable() {
        @Override
        public void run() {	inVehicleDeviceActivity.hideOrderedOperationFragment();	}
      };

      if (cursor.moveToFirst()) {
        ServiceProvider serviceProvider = new ServiceProvider(cursor);
        inVehicleDeviceActivity.setServiceProvider(serviceProvider);
        if (serviceProvider.operationListOnly) {
          inVehicleDeviceActivity.getActivityHandler().post(showOperationListFragmentTask);
        } else {
          inVehicleDeviceActivity.getActivityHandler().post(showOrderedOperationFragmentTask);
        }
      } else {
        inVehicleDeviceActivity.setServiceProvider(null);
        inVehicleDeviceActivity.getActivityHandler().post(hideOrderedOperationFragmentTask);
        inVehicleDeviceActivity.getActivityHandler().post(hideOperationListFragmentTask);
      }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
  };

}
