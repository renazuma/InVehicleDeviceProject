package com.kogasoftware.odt.invehicledevice.test.util;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.joda.time.DateTime;

import android.view.View;

import com.google.common.util.concurrent.Uninterruptibles;
import com.jayway.android.robotium.solo.Solo;
import com.kogasoftware.odt.invehicledevice.datasource.DataSource;
import com.kogasoftware.odt.invehicledevice.datasource.DataSourceFactory;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource;
import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;

public class TestUtil {
	public static void setDataSource(DataSource ds) {
		DataSourceFactory.setInstance(ds);
	}
	
	public static void setDate(DateTime date) {
		InVehicleDeviceService.setDate(new Date(date.getMillis()));
	}
	
	public static void clearStatus() {
		LocalDataSource.clearSavedFile();
	}
	
	public static void willShow(Solo solo, Class<? extends View> c) {
		willShow(solo, solo.getView(c, 0));
	}

	public static void willShow(Solo solo, View view) {
		Assert.assertTrue(solo.waitForView(view));
	}
	
	public static void willHide(Solo solo, Integer resourceId) {
		willHide(solo.getView(resourceId));
	}
	
	public static void willHide(View view) {
		for (Integer i = 0; i < 20; ++i) {
			if (view.getVisibility() != View.VISIBLE) {
				return;
			}
		}
		Assert.fail();
	}
	
	public static Boolean waitForStartUI(final InVehicleDeviceActivity activity) throws InterruptedException {
		Thread t = new Thread() {
			@Override
			public void run() {
				while (!Thread.currentThread().isInterrupted()) {
					Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
					if (activity.isUIInitialized()) {
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

	public static void setDate(String string) {
		setDate(DateTime.parse(string));
	}
}
