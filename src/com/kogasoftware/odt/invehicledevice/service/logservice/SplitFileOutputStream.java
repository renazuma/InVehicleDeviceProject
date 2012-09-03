package com.kogasoftware.odt.invehicledevice.service.logservice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import android.util.Log;

import com.google.common.base.Stopwatch;
import com.google.common.io.NullOutputStream;
import com.google.common.util.concurrent.Uninterruptibles;
import com.kogasoftware.odt.invehicledevice.empty.EmptyFile;

/**
 * writeされたデータをファイルに分割して保存するOutputStream
 */
public class SplitFileOutputStream extends OutputStream {
	private static final String TAG = SplitFileOutputStream.class
			.getSimpleName();
	private final Object memberAccessLock = new Object(); // メンバアクセス時には必ずこれを使ってロックを行う
	private final File baseDirectory;
	private final String baseFileName;
	private final BlockingQueue<File> outputFiles;
	private final Stopwatch stopwatch = new Stopwatch();
	private volatile Long count = 0L;
	private volatile File currentFile = new EmptyFile();
	private volatile OutputStream currentOutputStream = new NullOutputStream();

	public SplitFileOutputStream(File baseDirectory, String baseFileName,
			BlockingQueue<File> outputFiles) throws IOException {
		super();
		this.baseDirectory = baseDirectory;
		this.baseFileName = baseFileName;
		this.outputFiles = outputFiles;
		currentFile = getNewFile();
		currentOutputStream = new FileOutputStream(currentFile);
	}

	private File getNewFile() {
		synchronized (memberAccessLock) {
			while (true) {
				String format = (new SimpleDateFormat("yyyyMMddHHmmss.SSS"))
						.format(new Date());
				File newFile = new File(baseDirectory, format + "_"
						+ baseFileName + ".log");
				if (newFile.exists()) {
					Uninterruptibles.sleepUninterruptibly(50,
							TimeUnit.MILLISECONDS);
					continue;
				}
				return newFile;
			}
		}
	}

	public void split() throws IOException {
		synchronized (memberAccessLock) {
			if (count.equals(0L)) {
				return;
			}
			File newFile = getNewFile();
			OutputStream newOutputStream = new FileOutputStream(newFile); // ここで例外が発生するので、ここまでの時点でメンバ変数の更新はしない

			try {
				currentOutputStream.flush();
			} catch (IOException e) {
				Log.w(TAG, e);
			}
			try {
				currentOutputStream.close();
			} catch (IOException e) {
				Log.w(TAG, e);
			}
			outputFiles.add(currentFile);
			currentFile = newFile;
			currentOutputStream = newOutputStream;
			count = 0L;
			stopwatch.reset();
		}
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		synchronized (memberAccessLock) {
			currentOutputStream.write(b, off, len);
			if (count.equals(0L) && !stopwatch.isRunning()) {
				stopwatch.start();
			}
			count += len;
		}
	}

	@Override
	public void write(byte[] b) throws IOException {
		synchronized (memberAccessLock) {
			write(b, 0, b.length);
		}
	}

	@Override
	public void write(int b) throws IOException {
		synchronized (memberAccessLock) {
			write(new byte[] { (byte) b });
		}
	}

	@Override
	public void flush() throws IOException {
		synchronized (memberAccessLock) {
			currentOutputStream.flush();
		}
	}

	@Override
	public void close() throws IOException {
		try {
			synchronized (memberAccessLock) {
				try {
					try {
						flush();
					} catch (IOException e) {
						Log.w(TAG, e);
					}
					try {
						currentOutputStream.close();
					} catch (IOException e) {
						Log.w(TAG, e);
					}
				} finally {
					outputFiles.add(currentFile);
				}
			}
		} finally {
			super.close();
		}
	}

	public Long getCount() {
		synchronized (memberAccessLock) {
			return count;
		}
	}

	public Long getElapsedMillisSinceFirstWrite() {
		synchronized (memberAccessLock) {
			return stopwatch.elapsedMillis();
		}
	}
}


