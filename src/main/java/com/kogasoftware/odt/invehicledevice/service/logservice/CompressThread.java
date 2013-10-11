package com.kogasoftware.odt.invehicledevice.service.logservice;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.zip.GZIPOutputStream;

import android.content.Context;
import android.util.Log;

import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;

public class CompressThread extends Thread {
	private static final String TAG = CompressThread.class.getSimpleName();
	public static final String COMPRESSED_FILE_SUFFIX = ".gz";
	private final BlockingQueue<File> rawLogFiles;
	private final BlockingQueue<File> compressedLogFiles;

	public CompressThread(Context context, BlockingQueue<File> rawLogFiles,
			BlockingQueue<File> compressedLogFiles) {
		this.rawLogFiles = rawLogFiles;
		this.compressedLogFiles = compressedLogFiles;
	}

	@Override
	public void run() {
		Log.i(TAG, "start");
		try {
			while (true) {
				File rawLogFile = rawLogFiles.take();
				if (rawLogFile.length() == 0L) {
					Log.w(TAG, "\"" + rawLogFile
							+ "\".length() == 0L / ignored");
					if (!rawLogFile.delete()) {
						Log.w(TAG, "!\"" + rawLogFile + "\".delete()");
					}
					continue;
				}
				File compressedLogFile = new File(rawLogFile
						+ COMPRESSED_FILE_SUFFIX);

				FileInputStream fileInputStream = null;
				OutputStream compressOutputStream = null;
				FileOutputStream fileOutputStream = null;
				Boolean succeed = false;
				Boolean retry = false;
				try {
					fileInputStream = new FileInputStream(rawLogFile);
					fileOutputStream = new FileOutputStream(compressedLogFile);
					compressOutputStream = new GZIPOutputStream(
							fileOutputStream);
					ByteStreams.copy(fileInputStream, compressOutputStream);
					succeed = true;
				} catch (FileNotFoundException e) {
					Log.w(TAG, e);
				} catch (IOException e) {
					Log.w(TAG, e);
					retry = true;
				} finally {
					Closeables.closeQuietly(fileInputStream);
					Closeables.closeQuietly(compressOutputStream);
					Closeables.closeQuietly(fileOutputStream);
				}
				if (succeed) {
					compressedLogFiles.add(compressedLogFile);
					if (!rawLogFile.delete()) {
						Log.w(TAG, "!\"" + rawLogFile + "\".delete()");
					}
				} else if (retry) {
					rawLogFiles.add(rawLogFile);
				}
			}
		} catch (InterruptedException e) {
		}
		Log.i(TAG, "exit");
	}
}
