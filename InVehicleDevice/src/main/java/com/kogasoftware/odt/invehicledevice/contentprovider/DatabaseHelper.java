package com.kogasoftware.odt.invehicledevice.contentprovider;

import java.util.List;
import java.util.regex.Pattern;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.R;

public class DatabaseHelper extends SQLiteOpenHelper {
	static final Integer DATABASE_VERSION = 20;
	final List<String> migrationSqls = Lists.newArrayList();

	public DatabaseHelper(Context context) {
		super(context, "InVehicleDeviceContent.db", null, DATABASE_VERSION);
		for (String sql : Lists.newArrayList(context.getResources()
				.getStringArray(R.array.migration))) {
			migrationSqls.add(sql.replaceAll(Pattern.quote("$(id)"),
					BaseColumns._ID));
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		onUpgrade(db, 0, DATABASE_VERSION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		for (Integer version = oldVersion + 1; version <= newVersion; version++) {
			db.execSQL(migrationSqls.get(version));
		}
	}
}
