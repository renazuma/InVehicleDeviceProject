package com.kogasoftware.odt.invehicledevice.presenter.loader;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.kogasoftware.odt.invehicledevice.model.contentprovider.table.ServiceProvider;
import com.kogasoftware.odt.invehicledevice.view.activity.InVehicleDeviceActivity;
import com.kogasoftware.odt.invehicledevice.view.fragment.OperationListFragment;
import com.kogasoftware.odt.invehicledevice.view.fragment.OrderedOperationFragment;

/**
 * ServiceProvider情報を購読し、オペレーション画面を操作するLoaderを操作するクラス
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
    inVehicleDeviceActivity.getLoaderManager().initLoader(LOADER_ID, null, callbacks);
  }

  public void destroyLoader() {
    inVehicleDeviceActivity.getLoaderManager().destroyLoader(LOADER_ID);
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
        public void run() { OperationListFragment.showModal(inVehicleDeviceActivity); }
      };
      Runnable hideOperationListFragmentTask = new Runnable() {
        @Override
        public void run() { OperationListFragment.hideModal(inVehicleDeviceActivity); }
      };
      Runnable showOrderedOperationFragmentTask = new Runnable() {
        @Override
        public void run() { OrderedOperationFragment.showModal(inVehicleDeviceActivity); }
      };
      Runnable hideOrderedOperationFragmentTask = new Runnable() {
        @Override
        public void run() { OrderedOperationFragment.hideModal(inVehicleDeviceActivity); }
      };

      Handler mainUIHandler = new Handler(Looper.getMainLooper());

      // TODO: この場合、スケジュールの更新通知がすぐに出るので、ここでわざわざリスト表示をさせなくても良いのでは？不要なら削除したい。
      if (cursor.moveToFirst()) {
        ServiceProvider serviceProvider = new ServiceProvider(cursor);
        // TODO: Fragment表示が不要だとしても、ServiceProvider取得の完了設定は必要。
        inVehicleDeviceActivity.setServiceProvider(serviceProvider);
        if (serviceProvider.operationListOnly) {
          mainUIHandler.post(showOperationListFragmentTask);
        } else {
          mainUIHandler.post(showOrderedOperationFragmentTask);
        }
      } else {
        inVehicleDeviceActivity.setServiceProvider(null);
        mainUIHandler.post(hideOrderedOperationFragmentTask);
        mainUIHandler.post(hideOperationListFragmentTask);
      }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
  };

}
