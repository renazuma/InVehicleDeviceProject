package com.kogasoftware.odt.invehicledevice.contentprovider;

import java.util.List;
import java.util.regex.Pattern;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;

import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.InVehicleDevices;

public class DatabaseHelper extends SQLiteOpenHelper {
	static final Integer DATABASE_VERSION = 20;
	final List<String> migrationSqls = Lists.newArrayList();
	final Context context;

	public DatabaseHelper(Context context) {
		super(context, "InVehicleDeviceContent.db", null, DATABASE_VERSION);
		this.context = context;
		for (String sql : Lists.newArrayList(context.getResources()
				.getStringArray(R.array.migration))) {
			migrationSqls.add(sql.replaceAll(Pattern.quote("$(id)"),
					BaseColumns._ID));
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		onUpgrade(db, 0, DATABASE_VERSION);
		upgradeOldVersionInVehicleDevice(db);
	}

	private void upgradeOldVersionInVehicleDevice(SQLiteDatabase db) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		String url = preferences.getString("server_url", null);
		String authenticationToken = preferences.getString(
				"server_in_vehicle_device_authentication_token", null);
		preferences.edit().clear().apply();
		if (url == null || authenticationToken == null) {
			return;
		}
		ContentValues values = new ContentValues();
		values.put(InVehicleDevices.Columns.LOGIN, "");
		values.put(InVehicleDevices.Columns.PASSWORD, "");
		values.put(InVehicleDevices.Columns.URL, url);
		values.put(InVehicleDevices.Columns.AUTHENTICATION_TOKEN,
				authenticationToken);
		db.insertOrThrow(InVehicleDevices.TABLE_NAME, null, values);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		for (Integer version = oldVersion + 1; version <= newVersion; version++) {
			db.execSQL(migrationSqls.get(version));
		}
	}
}
