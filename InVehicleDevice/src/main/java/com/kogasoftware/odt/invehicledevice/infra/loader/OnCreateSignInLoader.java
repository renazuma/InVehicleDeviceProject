package com.kogasoftware.odt.invehicledevice.infra.loader;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.InVehicleDevice;
import com.kogasoftware.odt.invehicledevice.view.activity.InVehicleDeviceActivity;
import com.kogasoftware.odt.invehicledevice.view.fragment.modal.SignInFragment;

/**
 * インストール直後に、ログイン画面を表示するためのLoaderを操作するクラス
 * 車載器情報は誤ったデータでもDBに保存されるため、このローダは、インストール直後のデータが空の場合にしか通らない。
 **/

public class OnCreateSignInLoader {

    // TODO:InVehicleDeviceActivity配下で一意である必要がある。Activityクラスで管理した方が良い？
    // TODO: SignInFragmentとLOADER_IDが同じ。という事は、これはActivityクラスで起動derでSignInj
    public static final Integer LOADER_ID = 1;

    private final InVehicleDeviceActivity inVehicleDeviceActivity;

    public OnCreateSignInLoader(InVehicleDeviceActivity inVehicleDeviceActivity) {
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
                    InVehicleDevice.CONTENT.URI, null, null, null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data.moveToFirst()) {
                return;
            }

            Handler mainUIHandler = new Handler(Looper.getMainLooper());

            mainUIHandler.post(() -> SignInFragment.showModal(inVehicleDeviceActivity));
        }


        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
        }
    };
}
