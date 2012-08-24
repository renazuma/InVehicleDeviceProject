package com.kogasoftware.odt.invehicledevice.service.logservice;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.util.Log;

import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
import com.kogasoftware.odt.invehicledevice.empty.EmptyCloseable;

public class LogcatThread extends Thread {
	private static final String TAG = LogcatThread.class.getSimpleName();
	public static final Integer CHECK_INTERVAL = 2000;
	private volatile Closeable processCloser = new EmptyCloseable();
	private final OutputStream outputStream;

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

	public LogcatThread(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	@Override
	public void run() {
		Log.i(TAG, "start");
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
				ByteStreams.copy(inputStream, outputStream);
			} catch (IOException e) {
				Log.w(TAG, e);
			} finally {
				Closeables.closeQuietly(inputStream);
			}
		} catch (InterruptedException e) {
			Closeables.closeQuietly(processCloser);
		}
		Log.i(TAG, "exit");
	}
}
