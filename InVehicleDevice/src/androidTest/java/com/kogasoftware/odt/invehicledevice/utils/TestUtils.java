package com.kogasoftware.odt.invehicledevice.utils;

import java.io.Closeable;
import java.util.concurrent.ExecutorService;

import junitx.framework.Assert;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kogasoftware.odt.invehicledevice.contentprovider.table.InVehicleDevice;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.OperationRecord;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.Platform;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.Reservation;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.ServiceProvider;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.ServiceUnitStatusLog;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.User;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.VehicleNotification;
import com.robotium.solo.Solo;

public class TestUtils {
	public static void dispose(Object... objects) throws Exception {
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
				SQLiteDatabase database = (SQLiteDatabase) object;
				clear(database);
				database.close();
			} else if (object instanceof SQLiteOpenHelper) {
				((SQLiteOpenHelper) object).close();
			} else if (object instanceof Closeable) {
				((Closeable) object).close();
			} else if (object instanceof ExecutorService) {
				((ExecutorService) object).shutdownNow();
			} else if (object instanceof SharedPreferences) {
				clear(object);
			} else {
				throw new IllegalArgumentException("Unrecognized object: "
						+ object);
			}
		}
	}

	public static void clear(Object... objects) {
		for (Object object : objects) {
			if (object instanceof SQLiteDatabase) {
				// TODO: もうすこし賢い方法
				for (String tableName : new String[]{
						InVehicleDevice.TABLE_NAME,
						ServiceUnitStatusLog.TABLE_NAME,
						VehicleNotification.TABLE_NAME,
						Reservation.TABLE_NAME, User.TABLE_NAME,
						PassengerRecord.TABLE_NAME,
						OperationSchedule.TABLE_NAME,
						OperationRecord.TABLE_NAME,
						ServiceProvider.TABLE_NAME, Platform.TABLE_NAME}) {
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
