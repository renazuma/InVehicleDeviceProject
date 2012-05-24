package com.kogasoftware.odt.invehicledevice.test.util;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;

import android.view.View;

import com.google.common.util.concurrent.Uninterruptibles;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess;
import com.kogasoftware.odt.invehicledevice.logic.datasource.DataSource;
import com.kogasoftware.odt.invehicledevice.logic.datasource.DataSourceFactory;
import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;

public class TestUtil {
	public static void setDataSource(DataSource ds) {
		DataSourceFactory.setInstance(ds);
	}
	
	public static void setDate(DateTime date) {
		CommonLogic.setDate(new Date(date.getMillis()));
	}
	
	public static void clearStatus() {
		StatusAccess.clearSavedFile();
	}
	
	public static Boolean waitForStartUi(final InVehicleDeviceActivity activity) throws InterruptedException {
		Thread t = new Thread() {
			@Override
			public void run() {
				while (!Thread.currentThread().isInterrupted()) {
					Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
					if (activity.findViewById(android.R.id.content).getVisibility() == View.VISIBLE) {
						return;
					}
				}
			}
		};
		t.start();
		t.join(60 * 1000);
		if (t.isAlive()) {
			t.interrupt();
			return false;
		}
		return true;
	}
}
