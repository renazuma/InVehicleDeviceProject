package com.kogasoftware.odt.invehicledevice.service.logservice;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.kogasoftware.odt.invehicledevice.empty.EmptyThread;

public class LogService extends Service {
	private static final String TAG = LogService.class.getSimpleName();
	public static final long CHECK_DEVICE_INTERVAL_MILLIS = 10 * 1000;
	private final BlockingQueue<File> rawLogFiles = new LinkedBlockingQueue<File>();
	private final BlockingQueue<File> compressedLogFiles = new LinkedBlockingQueue<File>();
	private Thread logcatThread = new EmptyThread();
	private Thread dropboxThread = new EmptyThread();
	private Thread compressThread = new EmptyThread();
	private Thread uploadThread = new EmptyThread();

	@Override
	public void onCreate() {
		Log.i(TAG, "onCreate()");

		String s = File.separator;
		final File dataDirectory = new File(
				Environment.getExternalStorageDirectory() + s + ".odt" + s
						+ "log");

		logcatThread = new LogcatThread(this, dataDirectory, rawLogFiles);
		dropboxThread = new DropboxThread(this, dataDirectory, rawLogFiles);
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

				File[] defaultRawLogFiles = dataDirectory
						.listFiles(new FilenameFilter() {
							@Override
							public boolean accept(File dir, String filename) {
								return filename.endsWith(".log");
							}
						});
				if (defaultRawLogFiles != null) {
					rawLogFiles.addAll(Arrays.asList(defaultRawLogFiles));
				}
				File[] defaultCompressedLogFiles = dataDirectory
						.listFiles(new FilenameFilter() {
							@Override
							public boolean accept(File dir, String filename) {
								return filename.endsWith(".gz");
							}
						});
				if (defaultCompressedLogFiles != null) {
					compressedLogFiles.addAll(Arrays
							.asList(defaultCompressedLogFiles));
				}

				logcatThread.start();
				dropboxThread.start();
				compressThread.start();
				uploadThread.start();
			}
		});
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy()");
		logcatThread.interrupt();
		dropboxThread.interrupt();
		compressThread.interrupt();
		uploadThread.interrupt();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
