package com.kogasoftware.odt.invehicledevice.service.logservice;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.kogasoftware.odt.invehicledevice.empty.EmptyThread;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.SharedPreferencesKey;
import com.kogasoftware.odt.invehicledevice.ui.BigToast;
import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;

public class LogService extends Service {
	private static final String TAG = LogService.class.getSimpleName();
	public static final long CHECK_DEVICE_INTERVAL_MILLIS = 10 * 1000;
	private BlockingQueue<File> rawLogFiles = new LinkedBlockingQueue<File>();
	private BlockingQueue<File> compressedLogFiles = new LinkedBlockingQueue<File>();
	private Thread logThread = new EmptyThread();
	private Thread compressThread = new EmptyThread();
	private Thread uploadThread = new EmptyThread();

	@Override
	public void onCreate() {
		Log.i(TAG, "onCreate()");

		String s = File.separator;
		final File dataDirectory = new File(
				Environment.getExternalStorageDirectory() + s + ".odt" + s
						+ "log");

		logThread = new LogThread(this, dataDirectory, rawLogFiles);
		compressThread = new CompressThread(this, dataDirectory, rawLogFiles,
				compressedLogFiles);
		uploadThread = new UploadThread(this, dataDirectory, compressedLogFiles);
		final Handler handler = new Handler();
		handler.post(new Runnable() {
			@Override
			public void run() {
				String externalStorageState = Environment
						.getExternalStorageState();
				if (!externalStorageState.equals(Environment.MEDIA_MOUNTED)) {
					handler.postDelayed(this, CHECK_DEVICE_INTERVAL_MILLIS);
					return;
				}

				if (!dataDirectory.exists() && !dataDirectory.mkdirs()) {
					handler.postDelayed(this, CHECK_DEVICE_INTERVAL_MILLIS);
					return;
				}

				File[] defaultRawLogFiles = dataDirectory.listFiles(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String filename) {
						return filename.endsWith(".log");
					}
				});
				if (defaultRawLogFiles != null) {
					rawLogFiles.addAll(Arrays.asList(defaultRawLogFiles));
				}
				File[] defaultCompressedLogFiles = dataDirectory.listFiles(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String filename) {
						return filename.endsWith(".xz");
					}
				});
				if (defaultCompressedLogFiles != null) {
					compressedLogFiles.addAll(Arrays.asList(defaultCompressedLogFiles));
				}

				logThread.start();
				compressThread.start();
				uploadThread.start();
			}
		});
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy()");
		logThread.interrupt();
		compressThread.interrupt();
		uploadThread.interrupt();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
