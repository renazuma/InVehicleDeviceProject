package com.kogasoftware.odt.invehicledevice.infra.contentprovider;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.util.Log;

import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.InVehicleDevice;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.task.GetServiceProviderTask;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.regex.Pattern;


/**
 * DB管理
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    static final Integer DATABASE_VERSION = 29;
    private static final String TAG = DatabaseHelper.class.getSimpleName();
    final List<String> migrationSqls = Lists.newArrayList();
    final Context context;

    public DatabaseHelper(Context context, String fileName) {
        super(context, fileName, null, DATABASE_VERSION);
        this.context = context;
        for (String sql : Lists.newArrayList(context.getResources()
                .getStringArray(R.array.migration))) {
            migrationSqls.add(sql.replaceAll(Pattern.quote("$(id)"),
                    BaseColumns._ID));
        }
    }

    public DatabaseHelper(Context context) {
        this(context, "InVehicleDeviceContent.db");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        onUpgrade(db, 0, DATABASE_VERSION);
        upgradeOldVersionInVehicleDevice(db);
    }

    /**
     * データベースを使っていないバージョンからのアップグレード用メソッド。 すべてのデバイス上でアップグレードが完了したら削除する。
     */
    private void upgradeOldVersionInVehicleDevice(SQLiteDatabase db) {
        SharedPreferences preferences;
        try {
            preferences = PreferenceManager
                    .getDefaultSharedPreferences(context);
        } catch (UnsupportedOperationException e) {
            Log.i(TAG,
                    "PreferenceManager.getDefaultSharedPreferences(context) "
                            + "threw UnsupportedOperationException");
            return;
        }
        String url = preferences.getString("server_url", null);
        String authenticationToken = preferences.getString(
                "server_in_vehicle_device_authentication_token", null);
        preferences.edit().clear().putString(InVehicleDevice.Columns.URL, url)
                .apply();
        if (url == null || authenticationToken == null) {
            return;
        }
        ContentValues values = new ContentValues();
        values.put(InVehicleDevice.Columns.LOGIN, "");
        values.put(InVehicleDevice.Columns.PASSWORD, "");
        values.put(InVehicleDevice.Columns.URL, url);
        values.put(InVehicleDevice.Columns.AUTHENTICATION_TOKEN,
                authenticationToken);
        db.insertOrThrow(InVehicleDevice.TABLE_NAME, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (int version = oldVersion + 1; version <= newVersion; version++) {
            db.execSQL(migrationSqls.get(version));
        }
        if (oldVersion < 21) {
            ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
            executorService.execute(new GetServiceProviderTask(context, db, executorService));
        }
    }
}
