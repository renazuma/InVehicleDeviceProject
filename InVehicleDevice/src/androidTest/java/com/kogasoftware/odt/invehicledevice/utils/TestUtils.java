package com.kogasoftware.odt.invehicledevice.utils;

import java.io.Closeable;
import java.util.concurrent.ExecutorService;

import junitx.framework.Assert;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kogasoftware.odt.invehicledevice.contentprovider.table.InVehicleDevices;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.OperationRecords;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.OperationSchedules;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.PassengerRecords;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.Platforms;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.Reservations;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.ServiceProviders;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.ServiceUnitStatusLogs;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.Users;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.VehicleNotifications;
import com.robotium.solo.Solo;

public class TestUtils {
	public static void close(Object... objects) throws Exception {
		for (Object object : objects) {
			if (object == null) {
				continue;
			} else if (object instanceof Thread) {
				Thread thread = (Thread) object;
				thread.interrupt();
				thread.join(20 * 1000);
				Assert.assertFalse(thread.isAlive());
			} else if (object instanceof Solo) {
				((Solo) object).finishOpenedActivities();
			} else if (object instanceof SQLiteDatabase) {
				((SQLiteDatabase) object).close();
			} else if (object instanceof SQLiteOpenHelper) {
				((SQLiteOpenHelper) object).close();
			} else if (object instanceof Closeable) {
				((Closeable) object).close();
			} else if (object instanceof ExecutorService) {
				((ExecutorService) object).shutdownNow();
			} else {
				throw new IllegalArgumentException("Unrecognized object: "
						+ object);
			}
		}
	}

	public static void clean(Object... objects) {
		for (Object object : objects) {
			if (object instanceof SQLiteDatabase) {
				// TODO: もうすこし賢い方法
				for (String tableName : new String[]{
						InVehicleDevices.TABLE_NAME,
						ServiceUnitStatusLogs.TABLE_NAME,
						VehicleNotifications.TABLE_NAME,
						Reservations.TABLE_NAME, Users.TABLE_NAME,
						PassengerRecords.TABLE_NAME,
						OperationSchedules.TABLE_NAME,
						OperationRecords.TABLE_NAME,
						ServiceProviders.TABLE_NAME, Platforms.TABLE_NAME}) {
					((SQLiteDatabase) object).delete(tableName, null, null);
				}
			} else if (object instanceof SharedPreferences) {
				((SharedPreferences) object).edit().clear().commit();
			} else {
				throw new IllegalArgumentException("Unrecognized object: "
						+ object);
			}
		}
	}
}
