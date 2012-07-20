package com.kogasoftware.odt.invehicledevice.test.util;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.joda.time.DateTime;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;

import com.google.common.util.concurrent.Uninterruptibles;
import com.jayway.android.robotium.solo.Solo;
import com.kogasoftware.odt.invehicledevice.datasource.DataSource;
import com.kogasoftware.odt.invehicledevice.datasource.DataSourceFactory;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource;
import com.kogasoftware.odt.invehicledevice.service.startupservice.IStartupService;
import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;

public class TestUtil {
	private static final String TAG = TestUtil.class.getSimpleName();

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

	public static Boolean waitForStartUI(final InVehicleDeviceActivity activity)
			throws InterruptedException {
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

	public static void runOnUiThreadSync(Activity activity,
			final Runnable runnable) throws InterruptedException {
		final CountDownLatch cdl = new CountDownLatch(1);
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					runnable.run();
				} finally {
					cdl.countDown();
				}
			}
		});
		if (!cdl.await(10, TimeUnit.SECONDS)) {
			throw new RuntimeException("runOnUiThreadSync Timeout!");
		}
	}

	public static void setAutoStart(final Context context, final Boolean enable)
			throws InterruptedException {
		Thread t = new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				final Looper myLooper = Looper.myLooper();
				ServiceConnection serviceConnection = new ServiceConnection() {
					@Override
					public void onServiceConnected(ComponentName componentName,
							IBinder service) {
						IStartupService startupService = IStartupService.Stub
								.asInterface(service);
						myLooper.quit();
						try {
							if (enable) {
								startupService.enable();
							} else {
								startupService.disable();
							}

						} catch (RemoteException e) {
						}
					}

					@Override
					public void onServiceDisconnected(ComponentName arg0) {
					}
				};

				context.bindService(
						new Intent(IStartupService.class.getName()),
						serviceConnection, Context.BIND_AUTO_CREATE);
				Looper.loop();
			}
		};
		t.start();
		t.join();
	}

	public static void disableAutoStart(Context context)
			throws InterruptedException {
		setAutoStart(context, false);
	}

	public static void enableAutoStart(Context context)
			throws InterruptedException {
		setAutoStart(context, true);
	}

	public static void enableStrictMode() {
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectAll().penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll()
				.penaltyLog().penaltyDeath().build());
	}
	
	public static void exitService(final Context c) throws InterruptedException {
		final Intent i = new Intent(c, InVehicleDeviceService.class);
		Thread t = new Thread() {
			Looper myLooper;
			final ServiceConnection sc = new ServiceConnection() {
				@Override
				public void onServiceConnected(ComponentName cn, IBinder ib) {
					((InVehicleDeviceService.LocalBinder) ib).getService().exit();
					c.unbindService(this);
					c.stopService(i);
					myLooper.quit();
					interrupt();
				}

				@Override
				public void onServiceDisconnected(ComponentName cn) {
				}
			};

			@Override
			public void run() {
				Looper.prepare();
				myLooper = Looper.myLooper();
				Assert.assertTrue(c
						.bindService(i, sc, Context.BIND_AUTO_CREATE));
				Looper.loop();
			}
		};
		t.start();
		t.join();
	}
}
