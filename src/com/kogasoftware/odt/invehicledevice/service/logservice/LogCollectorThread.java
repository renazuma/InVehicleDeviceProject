package com.kogasoftware.odt.invehicledevice.service.logservice;

import java.io.ByteArrayInputStream;
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
	private final PipeThread pipeThread;
	private final AtomicReference<OutputStream> currentOutputStream = new AtomicReference<OutputStream>(
			null);
	private final InputStream inputStream;
	protected final Context context;

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
				Log.w(TAG + "#"
						+ LogCollectorThread.this.getClass().getSimpleName(), e);
			} finally {
				Closeables.closeQuietly(fileOutputStream);
				currentOutputStream.set(null);
			}
		}

		@Override
		public void run() {
			try {
				while (true) {
					String format = (new SimpleDateFormat("yyyyMMddHHmmss.SSS"))
							.format(new Date());
					File file = new File(dataDirectory, format
							+ "_" + name + ".log");
					save(file, inputStream);
					rawLogFiles.add(file);
					Thread.sleep(10 * 1000);
				}
			} catch (InterruptedException e) {
			} finally {
				Closeables.closeQuietly(inputStream);
			}
		}
	}

	public LogCollectorThread(Context context, File dataDirectory,
			BlockingQueue<File> rawLogFiles, String name) {
		this.context = context;
		this.dataDirectory = dataDirectory;
		this.rawLogFiles = rawLogFiles;
		this.name = name;
		InputStream tempInputStream = new ByteArrayInputStream(new byte[] {});
		try {
			tempInputStream = new PipedInputStream(pipedOutputStream);
		} catch (IOException e) {
			Log.e(TAG, e.toString(), e);
		}
		inputStream = tempInputStream;
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
		Flushables.flushQuietly(pipedOutputStream);
		Flushables.flushQuietly(currentOutputStream.get());
	}
}
