package com.kogasoftware.odt.invehicledevice.infra.loader;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;

import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.DefaultCharge;
import com.kogasoftware.odt.invehicledevice.view.activity.InVehicleDeviceActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * default_charge情報をバックグラウンドで取得する
 * HACK: 他のLoaderは画面単位だが、これだけテーブル単位になって居て、粒度が異なる。どちらかに合わせたい。
 * HACK: CursorLoaderはjoinして取得が出来ないので、画面に必要な情報を全部取得する事は出来ない。テーブル単位が正しい気がする。
 */

public class DefaultChargeLoader {

    // HACK: InVehicleDeviceActivity配下で一意である必要がある。Activityクラスで管理した方が良い？
    private static final Integer LOADER_ID = 5;

    private final InVehicleDeviceActivity inVehicleDeviceActivity;

    public DefaultChargeLoader(InVehicleDeviceActivity inVehicleDeviceActivity) {
        // HAKC: Activityを使いまわすのは良くない気がする。別の方法があれば変えたい。
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
            return new CursorLoader(inVehicleDeviceActivity, DefaultCharge.CONTENT.URI, null, null, null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            List default_charges = new ArrayList();
            while (cursor.moveToNext()) {
                default_charges.add(new DefaultCharge(cursor));
            }
            inVehicleDeviceActivity.setDefaultCharges(default_charges);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
        }
    };
}
