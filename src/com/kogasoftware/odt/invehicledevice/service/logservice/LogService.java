package com.kogasoftware.odt.invehicledevice.service.logservice;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.kogasoftware.odt.invehicledevice.empty.EmptyThread;
import com.kogasoftware.odt.invehicledevice.ui.activity.StartupActivity;

public class LogService extends Service {
	private static final String TAG = LogService.class.getSimpleName();
	public static final long CHECK_DEVICE_INTERVAL_MILLIS = 10 * 1000;
	public static final Integer FOREGROUND_NOTIFICATION_ID = 10;
	private final BlockingQueue<File> rawLogFiles = new LinkedBlockingQueue<File>();
	private final BlockingQueue<File> compressedLogFiles = new LinkedBlockingQueue<File>();
	private final SendLogBroadcastReceiver sendLogBroadcastReceiver = new SendLogBroadcastReceiver(
			rawLogFiles);
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

		try {
			logcatThread = new LogcatThread(new SplitFileOutputStream(
					dataDirectory, "logcat", rawLogFiles));
			dropboxThread = new DropBoxThread(this, new SplitFileOutputStream(
					dataDirectory, "dropbox", rawLogFiles));
		} catch (IOException e) {
			Log.wtf(TAG, e);
			logcatThread.interrupt();
			dropboxThread.interrupt();
			// ログが出力できない致命的なエラーのため、サービスをクラッシュさせ再起動させる
			throw new RuntimeException("can't create log");
		}

		compressThread = new CompressThread(this, rawLogFiles,
				compressedLogFiles);
		uploadThread = new UploadThread(this, dataDirectory, compressedLogFiles);

		registerReceiver(sendLogBroadcastReceiver, new IntentFilter(
				SendLogBroadcastReceiver.ACTION_SEND_LOG));

		// @see
		// http://stackoverflow.com/questions/3687200/implement-startforeground-method-in-android
		// The intent to launch when the user clicks the expanded notification
		Intent notificationIntent = new Intent(this, StartupActivity.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);

		// This constructor is deprecated. Use Notification.Builder instead
		Notification notification = new Notification(
				android.R.drawable.ic_menu_info_details, "車載器アプリケーションを起動しています",
				System.currentTimeMillis());

		// This method is deprecated. Use Notification.Builder instead.
		notification.setLatestEventInfo(this, "車載器アプリケーション", "起動しています",
				pendIntent);

		notification.flags |= Notification.FLAG_NO_CLEAR;
		startForeground(FOREGROUND_NOTIFICATION_ID, notification);

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
		stopForeground(false);
		logcatThread.interrupt();
		dropboxThread.interrupt();
		compressThread.interrupt();
		uploadThread.interrupt();
		unregisterReceiver(sendLogBroadcastReceiver);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
