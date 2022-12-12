package com.kogasoftware.odt.invehicledevice.infra.contentprovider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.Content;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.DefaultCharge;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.InVehicleDevice;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.OperationRecord;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.Platform;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.Reservation;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.ServiceProvider;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.ServiceUnitStatusLog;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.User;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.VehicleNotification;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.ZenrinMapsAccount;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.task.GetOperationSchedulesTask;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.task.GetServiceProviderTask;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.task.GetVehicleNotificationsTask;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.task.PatchOperationRecordTask;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.task.PatchPassengerRecordTask;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.task.PatchVehicleNotificationTask;

import junit.framework.Assert;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 車載器内部データの管理とサーバーとの同期を行うContentProvider
 */
public class InVehicleDeviceContentProvider extends ContentProvider {
    public static final String AUTHORITY = "com.kogasoftware.odt.invehicledevice.infra.contentprovider";
    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    public static final ObjectMapper JSON = new ObjectMapper();

    static {
        JSON.registerModule(new JodaModule());
        JSON.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        JSON.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        JSON.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        for (Content content : new Content[]{InVehicleDevice.CONTENT,
                OperationRecord.CONTENT, OperationSchedule.CONTENT,
                PassengerRecord.CONTENT, Platform.CONTENT, Reservation.CONTENT,
                ServiceProvider.CONTENT, ServiceUnitStatusLog.CONTENT,
                User.CONTENT, VehicleNotification.CONTENT,
                DefaultCharge.CONTENT, ZenrinMapsAccount.CONTENT}) {
            content.addTo(MATCHER);
        }
    }

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    // 順番を保持したまま処理したい通信があるため、スレッドの数は1で固定
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    public SQLiteDatabase getDatabase() {
        return database;
    }

    public ScheduledExecutorService getExecutorService() {
        return executorService;
    }

    public ContentResolver getContentResolver() {
        return getContext().getContentResolver();
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();

        databaseHelper = new DatabaseHelper(context);
        database = databaseHelper.getWritableDatabase();

        // TODO: 定期処理はここで良い？ContentProviderはあくまでDBとのやり取りを担う機能としておいて、定期処理はMainActivityで開始するべきでは？
        // TODO: おそらく、Activityがバックグラウンドに入っても連携はし続ける必要があるので、ContentProviderに入っているのだと考えられる。
        // TODO: それであれば、別途serviceに切り出したりした方が良いかも。

        // 一定間隔で、未読通知をサーバから取得。（スケジュール通知がある場合、スケジュール取得も実行）
        executorService.scheduleWithFixedDelay(
                new GetVehicleNotificationsTask(context, database, executorService),
                5,
                GetVehicleNotificationsTask.INTERVAL_MILLIS,
                TimeUnit.MILLISECONDS);

        // TODO: 以下のサーバへのデータ送信は、対象データ更新直後でも良いのでは？
        // TODO: その場合は、アプリがフロントにある場合にだけローダーで実行すれば良いので、Activityの方に処理を移せる。
        // TODO: それとも、非同期なのでバックグラウンドに入ったら止まってしまう可能性がある事を考慮して定期実行になっている？
        // 一定間隔で、既読かつサーバ側と同期前の通知データを、サーバ側に送信
        executorService.scheduleWithFixedDelay(
                new PatchVehicleNotificationTask(context, database, executorService),
                10,
                PatchVehicleNotificationTask.INTERVAL_MILLIS,
                TimeUnit.MILLISECONDS);

        // 一定間隔で、更新のあった運行履歴を、サーバ側に送信
        executorService.scheduleWithFixedDelay(
                new PatchOperationRecordTask(context, database, executorService),
                10,
                PatchOperationRecordTask.INTERVAL_MILLIS,
                TimeUnit.MILLISECONDS);

        // 一定間隔で、更新のあった乗降情報を、サーバ側に送信
        executorService.scheduleWithFixedDelay(
                new PatchPassengerRecordTask(context, database, executorService),
                10,
                PatchPassengerRecordTask.INTERVAL_MILLIS,
                TimeUnit.MILLISECONDS);

        return true;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int code = MATCHER.match(uri);
        switch (code) {
            case InVehicleDevice.TABLE_CODE:
                return InVehicleDevice.replaceLoginAndPassword(values, this,
                        () -> onUpdateAuthenticationToken());
            case VehicleNotification.TABLE_CODE:
                return VehicleNotification.replace(values, this);
            case OperationSchedule.TABLE_CODE:
                return OperationSchedule.replace(values, this);
            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        int match = MATCHER.match(uri);
        switch (match) {
            case ServiceProvider.TABLE_CODE:
                return ServiceProvider.query(this, projection, selection,
                        selectionArgs, sortOrder);
            case InVehicleDevice.TABLE_CODE:
                return InVehicleDevice.query(this, projection, selection,
                        selectionArgs, sortOrder);
            case VehicleNotification.TABLE_CODE:
                return VehicleNotification.query(this, projection, selection,
                        selectionArgs, sortOrder);
            case OperationSchedule.TABLE_CODE:
                return OperationSchedule.query(this, projection, selection,
                        selectionArgs, sortOrder);
            case PassengerRecord.TABLE_CODE:
                return PassengerRecord.query(this, projection, selection,
                        selectionArgs, sortOrder);
            case DefaultCharge.TABLE_CODE:
                return DefaultCharge.query(this, projection, selection,
                        selectionArgs, sortOrder);
            case ZenrinMapsAccount.TABLE_CODE:
                return ZenrinMapsAccount.query(this, projection, selection,
                        selectionArgs, sortOrder);
            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int match = MATCHER.match(uri);
        if (match == InVehicleDevice.TABLE_CODE) {
            return InVehicleDevice.delete(this, selection, selectionArgs);
        }
        throw new IllegalArgumentException("Unknown uri: " + uri);
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int match = MATCHER.match(uri);
        switch (match) {
            case PassengerRecord.TABLE_CODE:
                return PassengerRecord.update(this, values, selection, selectionArgs);
            case ServiceUnitStatusLog.TABLE_CODE:
                return ServiceUnitStatusLog.update(this, values, selection);
            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }
    }

    private void onUpdateAuthenticationToken() {
        database.delete(ServiceProvider.TABLE_NAME, null, null);
        try (Cursor cursor = database.query(InVehicleDevice.TABLE_NAME, null, null,
                null, null, null, null)) {
            if (!cursor.moveToFirst()) {
                return;
            }

            int authenticationTokenIndex = cursor.getColumnIndexOrThrow(InVehicleDevice.Columns.AUTHENTICATION_TOKEN);

            if (cursor.isNull(authenticationTokenIndex)) {
                return;
            }

        }
        executorService.execute(new GetServiceProviderTask(getContext(), database, executorService));
        executorService.execute(new GetOperationSchedulesTask(getContext(), database, executorService));
    }

    /**
     * Implement this to shut down the ContentProvider instance. You can then
     * invoke this method in unit tests.
     *
     * @see "http://developer.android.com/reference/android/content/ContentProvider.html#shutdown%28%29"
     */
    @Override
    public void shutdown() {
        try {
            executorService.shutdownNow();
            try {
                Assert.assertTrue(executorService.awaitTermination(30, TimeUnit.SECONDS));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } finally {
            try {
                database.close();
                databaseHelper.close();
            } finally {
                super.shutdown();
            }
        }
    }
}
