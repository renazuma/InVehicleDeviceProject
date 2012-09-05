package com.kogasoftware.odt.invehicledevice.service.logservice;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
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
	private final SendLogBroadcastReceiver sendLogBroadcastReceiver = new SendLogBroadcastReceiver(
			rawLogFiles);
	private Boolean destroyed = false;
	private Thread logcatThread = new EmptyThread();
	private Thread dropboxThread = new EmptyThread();
	private Thread compressThread = new EmptyThread();
	private Thread uploadThread = new EmptyThread();

	public File getDataDirectory() {
		return new File(Environment.getExternalStorageDirectory()
				+ File.separator + ".odt" + File.separator + "log");
	}

	public static void waitForDataDirectory(File directory)
			throws InterruptedException {
		while (true) {
			Thread.sleep(CHECK_DEVICE_INTERVAL_MILLIS);
			if (!Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				continue;
			}
			if (!directory.exists() && !directory.mkdirs()) {
				Log.w(TAG, "!\"" + directory + "\".mkdirs()");
				continue;
			}
			if (!directory.canWrite()) {
				Log.w(TAG, "!\"" + directory + "\".canWrite()");
				continue;
			}
			break;
		}
	}
	
	public UploadThread createUploadThread(BlockingQueue<File> compressedLogFiles) {
		return new UploadThread(this, compressedLogFiles);
	}
	
	public Boolean startLog() {
		if (destroyed) {
			Log.i(TAG, "destroyed=" + destroyed + " / startLog returned");
			return false;
		}

		File dataDirectory = getDataDirectory();

		rawLogFiles.addAll(getRawLogFiles(dataDirectory));
		compressedLogFiles.addAll(getCompressedLogFiles(dataDirectory));

		compressThread = new CompressThread(this, rawLogFiles,
				compressedLogFiles);
		uploadThread = createUploadThread(compressedLogFiles);

		try {
			logcatThread = new LogcatThread(new SplitFileOutputStream(
					dataDirectory, "logcat", rawLogFiles));
			dropboxThread = new DropBoxThread(this, new SplitFileOutputStream(
					dataDirectory, "dropbox", rawLogFiles));
		} catch (IOException e) {
			Log.wtf(TAG, e);
			// ログが出力できない致命的なエラーのため、サービスをクラッシュさせ再起動させる
			throw new RuntimeException("can't create log");
		}

		logcatThread.start();
		dropboxThread.start();
		compressThread.start();
		uploadThread.start();

		return true;
	}

	@Override
	public void onCreate() {
		destroyed = false;
		Log.i(TAG, "onCreate()");

		registerReceiver(sendLogBroadcastReceiver, new IntentFilter(
				SendLogBroadcastReceiver.ACTION_SEND_LOG));

		final Handler handler = new Handler();
		new Thread() {
			@Override
			public void run() {
				try {
					waitForDataDirectory(getDataDirectory());
					handler.post(new Runnable() {
						@Override
						public void run() {
							startLog();
						}
					});
				} catch (InterruptedException e) {
				}
			}
		}.start();
	}

	@Override
	public void onDestroy() {
		destroyed = true;
		Log.i(TAG, "onDestroy()");
		logcatThread.interrupt();
		dropboxThread.interrupt();
		compressThread.interrupt();
		uploadThread.interrupt();
		try {
			unregisterReceiver(sendLogBroadcastReceiver);
		} catch (IllegalArgumentException e) {
			Log.i(TAG, e.toString(), e);
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public static List<File> getCompressedLogFiles(File directory) {
		List<File> files = new LinkedList<File>();
		File[] defaultFiles = directory.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				return filename.endsWith(".gz");
			}
		});
		if (defaultFiles != null) {
			files.addAll(Arrays.asList(defaultFiles));
		}
		return files;
	}

	public static List<File> getRawLogFiles(File directory) {
		List<File> files = new LinkedList<File>();
		File[] defaultFiles = directory.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				return filename.endsWith(".log");
			}
		});
		if (defaultFiles != null) {
			files.addAll(Arrays.asList(defaultFiles));
		}
		return files;
	}
}
