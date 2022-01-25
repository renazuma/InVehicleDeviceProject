package com.kogasoftware.odt.invehicledevice.infra.loader;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.VehicleNotification;
import com.kogasoftware.odt.invehicledevice.view.activity.InVehicleDeviceActivity;

import org.joda.time.DateTime;

/**
 * メモの変更を検知した際に、バックエンドへ同期完了の更新を行うためのLoaderクラス
 */

public class MemoChangedNotificationLoader {

    // TODO:InVehicleDeviceActivity配下で一意である必要がある。Activityクラスで管理した方が良い？
    public static final Integer LOADER_ID = 7;

    private final InVehicleDeviceActivity inVehicleDeviceActivity;

    public MemoChangedNotificationLoader(InVehicleDeviceActivity inVehicleDeviceActivity) {
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
                    VehicleNotification.WHERE_MEMO_CHANGED_VEHICLE_NOTIFICATION_FRAGMENT_CONTENT,
                    null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            if (cursor.getCount() == 0) {
                return;
            }

            Handler mainUIHandler = new Handler(Looper.getMainLooper());

            mainUIHandler.post(new Runnable() {
                @Override
                public void run() {
                    try (Cursor cursor = inVehicleDeviceActivity.getContentResolver()
                            .query(VehicleNotification.CONTENT.URI,
                                    null,
                                    VehicleNotification.WHERE_MEMO_CHANGED_VEHICLE_NOTIFICATION_FRAGMENT_CONTENT,
                                    null, null)) {
                        if (cursor.moveToFirst()) {
                            do {
                                VehicleNotification vehicleNotification = new VehicleNotification(cursor);
                                vehicleNotification.response = VehicleNotification.Response.YES;
                                vehicleNotification.readAt = DateTime.now();
                                inVehicleDeviceActivity.getContentResolver().insert(
                                        VehicleNotification.CONTENT.URI,
                                        vehicleNotification.toContentValues());
                            } while (cursor.moveToNext());
                        }
                    }
                }
            });
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
        }
    };
}
