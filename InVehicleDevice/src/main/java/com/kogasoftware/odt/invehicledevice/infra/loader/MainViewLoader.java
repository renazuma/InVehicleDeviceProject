package com.kogasoftware.odt.invehicledevice.infra.loader;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.ServiceProvider;
import com.kogasoftware.odt.invehicledevice.view.activity.InVehicleDeviceActivity;
import com.kogasoftware.odt.invehicledevice.view.fragment.OrderedOperationFragment;

/**
 * ServiceProvider情報を購読し、オペレーション画面を操作するLoaderを操作するクラス
 * 想定実行タイミング：　アプリ起動時、サインイン後のSP再取得時
 */

public class MainViewLoader {

  // TODO:InVehicleDeviceActivity配下で一意である必要がある。Activityクラスで管理した方が良い？
  private static final Integer LOADER_ID = 2;

  private InVehicleDeviceActivity inVehicleDeviceActivity;

  public MainViewLoader(InVehicleDeviceActivity inVehicleDeviceActivity) {
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
      return new CursorLoader(inVehicleDeviceActivity, ServiceProvider.CONTENT.URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
      Handler mainUIHandler = new Handler(Looper.getMainLooper());

      if (cursor.moveToFirst()) {
        inVehicleDeviceActivity.setServiceProvider(new ServiceProvider(cursor));
        mainUIHandler.post(new Runnable() {
          @Override
          public void run() { OrderedOperationFragment.showModal(inVehicleDeviceActivity); }
        });
      } else {
        inVehicleDeviceActivity.setServiceProvider(null);
        mainUIHandler.post(new Runnable() {
          @Override
          public void run() { OrderedOperationFragment.hideModal(inVehicleDeviceActivity); }
        });
      }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
  };

}
