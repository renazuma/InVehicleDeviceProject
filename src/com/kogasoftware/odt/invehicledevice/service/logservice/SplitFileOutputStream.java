package com.kogasoftware.odt.invehicledevice.service.logservice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import android.util.Log;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.io.NullOutputStream;
import com.kogasoftware.odt.invehicledevice.empty.EmptyFile;
import com.kogasoftware.odt.invehicledevice.empty.EmptyThread;

/**
 * writeされたデータをファイルに分割して保存するOutputStream
 */
public class SplitFileOutputStream extends OutputStream {
	private static final String TAG = SplitFileOutputStream.class
			.getSimpleName();
	private static final Integer DEFAULT_TIMEOUT_SECONDS = 60 * 60;
	private static final Integer DEFAULT_MAX_BYTES = 2 * 1024 * 1024;
	private final Object memberAccessLock = new Object(); // メンバアクセス時には必ずこれを使ってロックを行う
	private final File baseDirectory;
	private final String baseFileName;
	private final BlockingQueue<File> outputFiles;
	private final Integer maxBytes;
	private final Runnable timeout;
	private volatile Thread timeoutThread = new EmptyThread();
	private volatile Integer currentBytes = 0;
	private volatile File currentFile = new EmptyFile();
	private volatile OutputStream currentOutputStream = new NullOutputStream();

	public SplitFileOutputStream(File baseDirectory, String baseFileName,
			BlockingQueue<File> outputFiles) throws IOException {
		this(baseDirectory, baseFileName, outputFiles, DEFAULT_TIMEOUT_SECONDS,
				DEFAULT_MAX_BYTES);
	}

	@VisibleForTesting
	public SplitFileOutputStream(File baseDirectory, String baseFileName,
			BlockingQueue<File> outputFiles, final Integer timeoutSeconds,
			Integer maxBytes) throws IOException {
		super();
		this.baseDirectory = baseDirectory;
		this.baseFileName = baseFileName;
		this.outputFiles = outputFiles;
		this.maxBytes = maxBytes;
		timeout = new Runnable() {
			@Override
			public void run() {
				try {
					while (true) {
						Thread.sleep(timeoutSeconds * 1000);
						try {
							synchronized (memberAccessLock) {
								if (currentBytes.equals(0)) {
									continue;
								}
								changeFile();
							}
						} catch (IOException e) {
							Log.e(TAG, e.toString(), e);
						}
					}
				} catch (InterruptedException e) {
				}
			}
		};
		currentFile = getNewFile();
		currentOutputStream = new FileOutputStream(currentFile);
		resetTimeoutThread();
	}

	private File getNewFile() {
		synchronized (memberAccessLock) {
			String format = (new SimpleDateFormat("yyyyMMddHHmmss.SSS"))
					.format(new Date());
			return new File(baseDirectory, format + "_" + baseFileName + ".log");
		}
	}

	private void resetTimeoutThread() {
		synchronized (memberAccessLock) {
			timeoutThread.interrupt();
			timeoutThread = new Thread(timeout);
			timeoutThread.start();
		}
	}

	private void changeFile() throws IOException {
		synchronized (memberAccessLock) {
			File newFile = getNewFile();
			OutputStream newOutputStream = new FileOutputStream(newFile); // ここで例外が発生するので、ここまでの時点でメンバ変数の更新はしない

			resetTimeoutThread();
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
			currentBytes = 0;
		}
	}

	@Override
	public void write(int oneByte) throws IOException {
		synchronized (memberAccessLock) {
			Log.i(TAG, currentBytes + "/" + maxBytes + " " + (char)oneByte + " " + currentFile);
			currentBytes++;
			currentOutputStream.write(oneByte);
			// ファイルサイズがmaxBytesを超えたら、保存先ファイルを交換
			if (currentBytes >= maxBytes) {
				changeFile();
			}
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
				timeoutThread.interrupt();
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
}


