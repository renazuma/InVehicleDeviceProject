package com.kogasoftware.odt.invehicledevice.service.logservice;

import java.io.Closeable;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.google.common.io.Closeables;
import com.kogasoftware.odt.invehicledevice.empty.EmptyThread;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.SharedPreferencesKeys;

public class LogService extends Service {
	private static final String TAG = LogService.class.getSimpleName();
	public static final long CHECK_DEVICE_INTERVAL_MILLIS = 10 * 1000;
	public static final String LOGCAT_FILE_TAG = "_logcat_";
	public static final String DROPBOX_FILE_TAG = "_dropbox_";
	private final BlockingQueue<File> rawLogFiles = new LinkedBlockingQueue<File>();
	private final BlockingQueue<File> compressedLogFiles = new LinkedBlockingQueue<File>();
	private final SendLogBroadcastReceiver sendLogBroadcastReceiver = new SendLogBroadcastReceiver(
			rawLogFiles);
	private Boolean destroyed = false;
	private Thread logcatThread = new EmptyThread();
	private Thread dropboxThread = new EmptyThread();
	private Thread compressThread = new EmptyThread();
	private Thread uploadThread = new EmptyThread();
	private List<Closeable> closeables = new LinkedList<Closeable>();

	/**
	 * シャットダウン時、可能な限りSDカードのマウントが解除される前に書込み中のログをフラッシュするため、
	 * ACTION_SHUTDOWNを受信して処理を行う。
	 */
	public static class ShutdownBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			context.stopService(new Intent(context, LogService.class));
		}
	}

	private final ILogService.Stub binder = new ILogService.Stub() {
		@Override
		public void setServerUploadCredentials(final String accessKeyId,
				final String secretAccessKey) {
			Log.i(TAG, "setServerUploadCredentials(" + accessKeyId + ")");
			Thread saveThread = new Thread() {
				@Override
				public void run() {
					SharedPreferences.Editor editor = LogService.this
							.getSharedPreferences(
									UploadThread.SHARED_PREFERENCES_NAME,
									Context.MODE_PRIVATE).edit();
					editor.putString(SharedPreferencesKeys.AWS_ACCESS_KEY_ID,
							accessKeyId);
					editor.putString(
							SharedPreferencesKeys.AWS_SECRET_ACCESS_KEY,
							secretAccessKey);
					editor.apply();
				}
			};
			saveThread.start();
		}
	};

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

	public UploadThread createUploadThread(
			BlockingQueue<File> compressedLogFiles) {
		return new UploadThread(this, compressedLogFiles);
	}

	/**
	 * スレッド開始。onDestroy()発生後に行われるのを防ぐためメインスレッドで実行する。
	 */
	public Boolean startLog(SplitFileOutputStream logcatSplitFileOutputStream,
			SplitFileOutputStream dropboxSplitFileOutputStream) {
		closeables.add(logcatSplitFileOutputStream);
		closeables.add(dropboxSplitFileOutputStream);

		if (destroyed) {
			Log.i(TAG, "destroyed=" + destroyed + " / startLog returned");
			return false;
		}

		compressThread = new CompressThread(this, rawLogFiles,
				compressedLogFiles);
		uploadThread = createUploadThread(compressedLogFiles);

		try {
			logcatThread = new LogcatThread(logcatSplitFileOutputStream);
		} catch (IOException e) {
			Log.wtf(TAG, e);
			// ログが出力できない致命的なエラーのため、サービスをクラッシュさせ再起動させる
			throw new RuntimeException("can't create log");
		}
		if (getPackageManager().checkPermission("android.permission.READ_LOGS",
				getPackageName()) == PackageManager.PERMISSION_GRANTED) {
			dropboxThread = new DropBoxThread(this,
					dropboxSplitFileOutputStream);
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
		super.onCreate();
		Log.i(TAG, "onCreate()");

		registerReceiver(sendLogBroadcastReceiver, new IntentFilter(
				SendLogBroadcastReceiver.ACTION_SEND_LOG));

		final Handler handler = new Handler();

		// IOを子スレッドで行う
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					// ディレクトリ準備完了を別スレッドで待つ
					final File dataDirectory = getDataDirectory();
					waitForDataDirectory(dataDirectory);

					// ディレクトリに最初から存在しているファイルを読み取る
					rawLogFiles.addAll(getRawLogFiles(dataDirectory));
					compressedLogFiles
							.addAll(getCompressedLogFiles(dataDirectory));

					// ディレクトリ準備完了後にストリームと出力ファイルを準備する
					final SplitFileOutputStream logcatSplitFileOutputStream = new SplitFileOutputStream(
							dataDirectory, LOGCAT_FILE_TAG, rawLogFiles);
					final SplitFileOutputStream dropboxSplitFileOutputStream = new SplitFileOutputStream(
							dataDirectory, DROPBOX_FILE_TAG, rawLogFiles);

					// スレッド開始は、onDestroy()発生後に行われるのを防ぐためメインスレッドで行う。
					Boolean posted = handler.post(new Runnable() {
						@Override
						public void run() {
							startLog(logcatSplitFileOutputStream,
									dropboxSplitFileOutputStream);
						}
					});
					if (!posted) {
						Closeables.closeQuietly(logcatSplitFileOutputStream);
						Closeables.closeQuietly(dropboxSplitFileOutputStream);
					}
				} catch (InterruptedException e) {
				} catch (IOException e) {
					// 致命的なエラーのため、再起動する
					Log.e(TAG, e.toString(), e);
					stopSelf();
				}
			}
		};
		thread.start();
	}

	@Override
	public void onDestroy() {
		destroyed = true;
		super.onDestroy();
		Log.i(TAG, "onDestroy()");
		logcatThread.interrupt();
		dropboxThread.interrupt();
		compressThread.interrupt();
		uploadThread.interrupt();
		for (Closeable closeable : closeables) {
			Closeables.closeQuietly(closeable);
		}
		try {
			unregisterReceiver(sendLogBroadcastReceiver);
		} catch (IllegalArgumentException e) {
			Log.i(TAG, e.toString(), e);
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
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

	public static List<File> getDropBoxLogFiles(File directory) {
		List<File> files = new LinkedList<File>();
		File[] defaultFiles = directory.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				return filename.endsWith(".log")
						&& filename.indexOf(DROPBOX_FILE_TAG) >= 0;
			}
		});
		if (defaultFiles != null) {
			files.addAll(Arrays.asList(defaultFiles));
		}
		return files;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		Log.i(TAG, "onStartCommand(" + intent + ", " + flags + ", " + startId
				+ ")");
		return Service.START_STICKY;
	}
}