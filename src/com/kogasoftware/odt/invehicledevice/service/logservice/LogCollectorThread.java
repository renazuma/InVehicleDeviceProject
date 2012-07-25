package com.kogasoftware.odt.invehicledevice.service.logservice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.common.base.Stopwatch;
import com.google.common.io.Closeables;
import com.google.common.io.Flushables;

public class LogCollectorThread extends Thread implements Flushable {
	private static final String TAG = LogCollectorThread.class.getSimpleName();
	private static final Integer MAX_HOURS = 1;
	private static final Integer MAX_BYTES = 2 * 1024 * 1024;
	private final String name;
	private final BlockingQueue<File> rawLogFiles;
	private final File dataDirectory;
	private final String deviceId;
	private final PipeThread pipeThread;
	private final AtomicReference<OutputStream> currentOutputStream = new AtomicReference<OutputStream>(
			null);

	protected final PipedOutputStream pipedOutputStream = new PipedOutputStream();

	class PipeThread extends Thread {
		private void save(File file, InputStream inputStream)
				throws InterruptedException {
			FileOutputStream fileOutputStream = null;
			try {
				Stopwatch stopwatch = new Stopwatch().start();
				fileOutputStream = new FileOutputStream(file);
				currentOutputStream.set(fileOutputStream);
				for (Integer bytes = 0; bytes < MAX_BYTES
						&& stopwatch.elapsedTime(TimeUnit.HOURS) < MAX_HOURS; ++bytes) {
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
				currentOutputStream.set(null);
			}
		}

		@Override
		public void run() {
			PipedInputStream pipedInputStream = null;
			try {
				pipedInputStream = new PipedInputStream(pipedOutputStream);
				while (true) {
					String format = (new SimpleDateFormat("yyyyMMddHHmmss.SSS"))
							.format(new Date());
					File file = new File(dataDirectory, deviceId + "_" + format
							+ "_" + name + ".log");
					save(file, pipedInputStream);
					rawLogFiles.add(file);
					Thread.sleep(10 * 1000);
				}
			} catch (InterruptedException e) {
			} catch (IOException e) {
				Log.w(TAG, e);
			} finally {
				Closeables.closeQuietly(pipedInputStream);
			}
		}
	}

	public LogCollectorThread(Context context, File dataDirectory,
			BlockingQueue<File> rawLogFiles, String name) {
		this.dataDirectory = dataDirectory;
		this.rawLogFiles = rawLogFiles;
		this.name = name;
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		deviceId = telephonyManager.getDeviceId(); // TODO
		pipeThread = new PipeThread();
	}

	@Override
	public void start() {
		super.start();
		pipeThread.start();
	}

	@Override
	public void interrupt() {
		super.interrupt();
		pipeThread.interrupt();
	}

	@Override
	public void flush() {
		Flushables.flushQuietly(currentOutputStream.get());
	}
}
