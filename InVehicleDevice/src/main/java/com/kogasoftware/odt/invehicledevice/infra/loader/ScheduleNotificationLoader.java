package com.kogasoftware.odt.invehicledevice.infra.loader;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.VehicleNotification;
import com.kogasoftware.odt.invehicledevice.view.activity.InVehicleDeviceActivity;
import com.kogasoftware.odt.invehicledevice.view.fragment.modal.notification.ScheduleVehicleNotificationFragment;
import com.kogasoftware.odt.invehicledevice.view.fragment.modal.notification.VehicleNotificationAlertFragment;

/**
 * スケジュール通知を購読し、通知を行うLoaderを操作するクラス
 * スケジュール通知の仕様は以下
 * VehicleNotificationテーブルが同期された際に、新規スケジュール通知が追加される。
 * スケジュール通知追加を受けて、OperationScheduleテーブルが同期開始される。
 * その際に紐づくOperationScheduleが更新された場合、VehicleNotificationテーブルのScheduleDownloadedも同時に更新される。
 * この時点で更新情報がpublishされ、定義済みCursorLoaderでのデータ取得が走り、onLoadFinishedが実行される。
 * TODO: 画面に従属するLoaderなので、Fragmentに扱わせても良い？ライフサイクルを確認する事
 */

public class ScheduleNotificationLoader {

    // TODO:InVehicleDeviceActivity配下で一意である必要がある。Activityクラスで管理した方が良い？
    public static final Integer LOADER_ID = 4;

    private final InVehicleDeviceActivity inVehicleDeviceActivity;

    public ScheduleNotificationLoader(InVehicleDeviceActivity inVehicleDeviceActivity) {
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
                    VehicleNotification.WHERE_SCHEDULE_VEHICLE_NOTIFICATION_FRAGMENT_CONTENT,
                    null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            String tag = ScheduleNotificationLoader.class.getSimpleName();
            Log.i(tag, "onLoadFinish and start process.");
            if (cursor.getCount() == 0) {
                return;
            }

            Handler mainUIHandler = new Handler(Looper.getMainLooper());

            mainUIHandler.post(() -> VehicleNotificationAlertFragment.showModal(inVehicleDeviceActivity));
            Log.i(tag, "Notification alert fragment modal set.");

            mainUIHandler.postDelayed(() -> ScheduleVehicleNotificationFragment.showModal(inVehicleDeviceActivity), InVehicleDeviceActivity.VEHICLE_NOTIFICATION_ALERT_DELAY_MILLIS);
            Log.i(tag, "Notification schedule fragment modal set.");
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
        }
    };
}
