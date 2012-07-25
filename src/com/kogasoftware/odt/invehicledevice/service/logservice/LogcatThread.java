package com.kogasoftware.odt.invehicledevice.service.logservice;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.common.io.Closeables;
import com.kogasoftware.odt.invehicledevice.empty.EmptyCloseable;

public class LogcatThread extends Thread {
	private static final String TAG = LogcatThread.class.getSimpleName();
	public static final Integer CHECK_INTERVAL = 2000;
	private final Context context;
	private final File dataDirectory;
	private final BlockingQueue<File> rawLogFiles;
	private volatile Closeable processCloser = new EmptyCloseable();

	public void interrupt() {
		super.interrupt();
		Closeables.closeQuietly(processCloser);
	}

	private Process getProcess() throws InterruptedException {
		while (true) {
			try {
				return Runtime.getRuntime().exec("logcat -v time");
			} catch (IOException e) {
				Log.w(TAG, e);
			}
			Thread.sleep(LogcatThread.CHECK_INTERVAL);
		}
	}

	public LogcatThread(Context context, File dataDirectory,
			BlockingQueue<File> rawLogFiles) {
		this.context = context;
		this.dataDirectory = dataDirectory;
		this.rawLogFiles = rawLogFiles;
	}

	@Override
	public void run() {
		try {
			TelephonyManager telephonyManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			String deviceId = telephonyManager.getDeviceId(); // TODO
			final Process process = getProcess();
			processCloser = new Closeable() {
				@Override
				public void close() {
					process.destroy();
				}
			};
			InputStream inputStream = process.getInputStream();
			try {
				while (true) {
					String format = (new SimpleDateFormat("yyyyMMddHHmmss.SSS"))
							.format(new Date());
					File file = new File(dataDirectory, deviceId + "_" + format + ".log");
					save(file, inputStream);
					rawLogFiles.add(file);
				}
			} finally {
				Closeables.closeQuietly(inputStream);
			}
		} catch (InterruptedException e) {
			Closeables.closeQuietly(processCloser);
		}
	}

	private void save(File file, InputStream inputStream)
			throws InterruptedException {
		final Integer MAX_BYTES = 2 * 1024 * 1024;
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(file);
			for (Integer bytes = 0; bytes < MAX_BYTES; ++bytes) {
				Thread.sleep(0); // interruption point
				Integer oneByte = inputStream.read();
				if (oneByte.equals(-1)) {
					break;
				}
				fileOutputStream.write(oneByte);
			}
		} catch (IOException e) {
			Log.w(TAG, e);
		} finally {
			Closeables.closeQuietly(fileOutputStream);
		}
	}
}
