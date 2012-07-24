package com.kogasoftware.odt.invehicledevice.service.logservice;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;

import com.google.common.base.Optional;
import com.google.common.io.Closeables;
import com.kogasoftware.odt.invehicledevice.empty.EmptyCloseable;
import com.kogasoftware.odt.invehicledevice.empty.EmptyThread;

import android.content.Context;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;

public class LogThread extends Thread {
	private static final String TAG = LogThread.class.getSimpleName();
	public static final Integer CHECK_INTERVAL = 2000;
	private final Context context;
	private final File dataDirectory;
	private final Object processLock = new Object();
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
			Thread.sleep(LogThread.CHECK_INTERVAL);
		}
	}

	public LogThread(Context context, File dataDirectory, BlockingQueue<File> rawLogFiles) {
		this.context = context;
		this.dataDirectory = dataDirectory;
		this.rawLogFiles = rawLogFiles;
	}

	@Override
	public void run() {
		try {
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
					String format = (new SimpleDateFormat("yyyyMMddHHmmss.SSSZ"))
							.format(new Date());
					File file = new File(dataDirectory, format + ".log");
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

	private void save(File file, InputStream inputStream) throws InterruptedException {
		final Integer MAX_BYTES = 5 * 1024 * 1024;
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(file);
			for (Integer bytes = 0; bytes < MAX_BYTES; ++bytes) {
				Thread.sleep(0); // interruption point
				fileOutputStream.write(inputStream.read());
			}
		} catch (IOException e) {
			Log.w(TAG, e);
		} finally {
			Closeables.closeQuietly(fileOutputStream);
		}
	}
}
