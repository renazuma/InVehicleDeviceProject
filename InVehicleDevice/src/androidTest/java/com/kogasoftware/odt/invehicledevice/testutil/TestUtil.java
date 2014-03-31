package com.kogasoftware.odt.invehicledevice.testutil;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import junit.framework.Assert;
import junitx.framework.AssertionFailedError;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Fragment;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.os.StrictMode;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;

import com.google.common.base.Stopwatch;
import com.google.common.util.concurrent.Uninterruptibles;
import com.kogasoftware.odt.invehicledevice.apiclient.InVehicleDeviceApiClient;
import com.kogasoftware.odt.invehicledevice.apiclient.InVehicleDeviceApiClientFactory;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage;
import com.kogasoftware.odt.invehicledevice.service.startupservice.IStartupService;
import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;
import com.robotium.solo.Solo;

public class TestUtil {
	public static void setApiClient(InVehicleDeviceApiClient ds) {
		InVehicleDeviceApiClientFactory.setInstance(ds);
	}

	public static void clearLocalStorage() {
		LocalStorage.clearSavedFile();
	}

	public static void clearLocalStorage(Context context) {
		clearLocalStorage();
		(new LocalStorage(context)).close();
		clearLocalStorage();
		Uninterruptibles.sleepUninterruptibly(getLocalStorageSaveMillis(),
				TimeUnit.MILLISECONDS);
	}

	public static void clearLocalStorage(Instrumentation i) {
		clearLocalStorage(i.getContext());
		clearLocalStorage(i.getTargetContext());
	}

	public static void assertChange(Callable<Boolean> condition) {
		assertChange(condition, 20 * 1000);
	}

	public static void assertChange(Callable<Boolean> condition, long timeout) {
		try {
			Stopwatch stopwatch = new Stopwatch().start();
			while (stopwatch.elapsed(TimeUnit.MILLISECONDS) < timeout) {
				if (condition.call()) {
					return;
				}
				Thread.sleep((long) (Math.log(timeout) * 20));
			}
		} catch (Exception e) {
			throw new AssertionFailedError(e);
		}
		Assert.fail();
	}

	public static void assertShow(Solo solo, Class<? extends View> c) {
		assertShow(solo.getView(c, 0));
	}

	public static void assertShow(View view) {
		assertChangeVisibility(view, true);
	}

	public static void assertChangeVisibility(final View view,
			final Boolean visibility) {
		assertChange(new Callable<Boolean>() {
			@Override
			public Boolean call() {
				return visibility.equals(view.getVisibility() == View.VISIBLE);
			}
		});
	}

	public static void assertHide(Solo solo, Integer resourceId) {
		assertHide(solo.getView(resourceId));
	}

	public static void assertHide(View view) {
		assertChangeVisibility(view, false);
	}

	public static Boolean waitForStartUI(final InVehicleDeviceActivity activity)
			throws InterruptedException {
		Thread t = new Thread() {
			@Override
			public void run() {
				while (!Thread.currentThread().isInterrupted()) {
					Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
					if (activity.isUiInitialized()) {
						return;
					}
				}
			}
		};
		t.start();
		t.join(20 * 1000);
		if (t.isAlive()) {
			t.interrupt();
			return false;
		}
		return true;
	}

	public static void runTestOnUiThreadSync(Activity activity,
			final Runnable runnable) throws InterruptedException {
		runTestOnUiThreadSync(activity, runnable, 20);
	}

	public static void runTestOnUiThreadSync(Activity activity,
			final Runnable runnable, Integer timeoutSeconds)
			throws InterruptedException {
		final CountDownLatch cdl = new CountDownLatch(1);
		final AtomicReference<Throwable> throwableReference = new AtomicReference<Throwable>();
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					runnable.run();
				} catch (AssertionFailedError innerError) {
					throwableReference.set(innerError);
				} catch (Exception innerException) {
					// kokode reigaiga okiruto subete sinunode
					throwableReference.set(innerException);
				} finally {
					cdl.countDown();
				}
			}
		});
		if (!cdl.await(timeoutSeconds, TimeUnit.SECONDS)) {
			throw new RuntimeException("runOnUiThreadSync Timeout!");
		}
		if (throwableReference.get() == null) {
			return;
		}
		Throwable throwable = throwableReference.get();
		if (throwable instanceof RuntimeException) {
			throw (RuntimeException) throwable;
		} else if (throwable instanceof AssertionFailedError) {
			throw (AssertionFailedError) throwable;
		} else {
			throw new RuntimeException(throwable);
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
					public void onServiceDisconnected(
							ComponentName componentName) {
						myLooper.quit();
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

	public static ComponentName getTopActivity(Context c) {
		ActivityManager activityManager = (ActivityManager) c
				.getSystemService(Activity.ACTIVITY_SERVICE);
		for (RunningTaskInfo runningTaskInfo : activityManager
				.getRunningTasks(1)) {
			return runningTaskInfo.topActivity;
		}

		throw new RuntimeException("topActivity not found");
	}

	public static void assertChangeVisibility(Context context,
			final Class<? extends Activity> activityClass,
			final Boolean visibility) {
		final ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Activity.ACTIVITY_SERVICE);
		assertChange(new Callable<Boolean>() {
			@Override
			public Boolean call() {
				for (RunningTaskInfo runningTaskInfo : activityManager
						.getRunningTasks(1)) {
					if (visibility.equals(runningTaskInfo.topActivity
							.getClassName().equals(activityClass.getName()))) {
						return true;
					}
				}
				return false;
			}
		});
	}

	public static void assertShow(Context context,
			Class<? extends Activity> activityClass) {
		assertChangeVisibility(context, activityClass, true);
	}

	public static void assertHide(Context context,
			Class<? extends Activity> activityClass) {
		assertChangeVisibility(context, activityClass, false);
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
					((InVehicleDeviceService.LocalBinder) ib).getService()
							.exit();
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
				if (!c.bindService(i, sc, 0)) {
					return;
				}
				Looper.loop();
			}
		};
		t.start();
		t.join();
	}

	private static <T> WeakHashMap<T, Integer> createManyEmptyObjectAndCheckMemory(
			Context context, Class<T> c, Integer numObjects) throws Exception {
		WeakHashMap<T, Integer> whm = new WeakHashMap<T, Integer>();

		// サイズが小さいことを確認
		List<T> l = new LinkedList<T>();
		for (Integer i = 0; i < numObjects; ++i) {
			T t = c.newInstance(); // 引数無しのデフォルトコンストラクタがあることを確認
			l.add(t);
			whm.put(t, i);
		}

		// lowMemoryになっていないことを確認
		ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
		((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE))
				.getMemoryInfo(mi);
		Assert.assertFalse("Low memory available: " + mi.availMem + " bytes",
				mi.lowMemory);
		Assert.assertFalse(whm.isEmpty());
		return whm;
	}

	public static <T> void assertEmptyObject(Instrumentation instrumentation,
			Class<T> c, Boolean bigObject) throws Exception {
		Context context = instrumentation.getContext();
		TestUtil.disableAutoStart(context);
		try {
			instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
			instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_HOME);
		} catch (SecurityException e) {
		}
		Thread.sleep(5000);

		WeakHashMap<T, Integer> whm = createManyEmptyObjectAndCheckMemory(
				context, c, bigObject ? (1 << 12) : (1 << 17));

		// 自動でGCされるかを確認
		Stopwatch sw = new Stopwatch().start();
		while (sw.elapsed(TimeUnit.SECONDS) < 10) {
			if (whm.isEmpty()) {
				return;
			}
			Thread.sleep(500);
			System.gc();
		}

		Assert.fail("WeakHashMap size=" + whm.size() + " " + whm);
	}

	public static byte[] readWithNonBlock(InputStream inputStream)
			throws IOException, InterruptedException {
		return readWithNonBlock(inputStream, 0);
	}

	public static byte[] readWithNonBlock(InputStream inputStream, long timeout)
			throws IOException, InterruptedException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		Stopwatch stopwatch = new Stopwatch().start();
		while (true) {
			int available = inputStream.available();
			if (available <= 0) {
				if (stopwatch.elapsed(TimeUnit.MILLISECONDS) > timeout) {
					break;
				}
				Thread.sleep(timeout / 10);
				continue;
			}
			byte[] buffer = new byte[available];
			inputStream.read(buffer, 0, available);
			byteArrayOutputStream.write(buffer);
			stopwatch.reset().start();
		}
		return byteArrayOutputStream.toByteArray();
	}

	public static <T> void assertEmptyObject(Instrumentation instrumentation,
			Class<T> c) throws Exception {
		assertEmptyObject(instrumentation, c, false);
	}

	public static void assertShow(Fragment fragment) {
		assertChangeVisibility(fragment, true);
	}

	private static void assertChangeVisibility(final Fragment fragment,
			final Boolean visibility) {
		assertChange(new Callable<Boolean>() {
			@Override
			public Boolean call() {
				return visibility.equals(fragment.isVisible());
			}
		});
	}

	public static void assertHide(Fragment fragment) {
		assertChangeVisibility(fragment, false);
	}

	public static void setDate(DateTime dateTime) {
		DateTimeUtils.setCurrentMillisFixed(dateTime.getMillis());
	}

	public static Closeable setTestThreadHandler() {
		final CountDownLatch l = new CountDownLatch(1);
		final Thread thread = Thread.currentThread();
		final HandlerThread hs = new HandlerThread("") {
			@Override
			protected void onLooperPrepared() {
				InVehicleDeviceService.putThreadHandler(thread, new Handler(
						getLooper()));
				l.countDown();
			}
		};
		hs.start();
		try {
			Assert.assertTrue(l.await(10, TimeUnit.SECONDS));
		} catch (InterruptedException e) {
			Assert.fail();
		}
		return new Closeable() {
			@Override
			public void close() {
				hs.quit();
				hs.interrupt();
			}
		};
	}

	public static Long getLocalStorageSaveMillis() {
		return 2000L;
	}

	public static boolean isDefaultLandscape(Context c) {
		WindowManager wm = (WindowManager) c
				.getSystemService(Context.WINDOW_SERVICE);
		Display d = wm.getDefaultDisplay();
		Integer o = d.getOrientation();
		Boolean defaultLandscape = (d.getWidth() > d.getHeight())
				^ (o.equals(Surface.ROTATION_90) | o
						.equals(Surface.ROTATION_270));
		return defaultLandscape;
	}
}
