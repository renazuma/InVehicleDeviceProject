package com.kogasoftware.odt.invehicledevice.service.logservice;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.zip.GZIPOutputStream;

import android.content.Context;
import android.util.Log;

import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;

public class CompressThread extends Thread {
	private static final String TAG = CompressThread.class.getSimpleName();
	private final BlockingQueue<File> rawLogFiles;
	private final BlockingQueue<File> compressedLogFiles;

	public CompressThread(Context context, File dataDirectory,
			BlockingQueue<File> rawLogFiles,
			BlockingQueue<File> compressedLogFiles) {
		this.rawLogFiles = rawLogFiles;
		this.compressedLogFiles = compressedLogFiles;
	}

	@Override
	public void run() {
		Log.i(TAG, "start");
		try {
			while (true) {
				Thread.sleep(1000);
				File rawLogFile = rawLogFiles.take();
				File compressedLogFile = new File(rawLogFile + ".gz");

				FileInputStream fileInputStream = null;
				GZIPOutputStream gzipOutputStream = null;
				FileOutputStream fileOutputStream = null;
				Boolean succeed = false;
				Boolean retry = false;
				try {
					fileInputStream = new FileInputStream(rawLogFile);
					fileOutputStream = new FileOutputStream(compressedLogFile);
					gzipOutputStream = new GZIPOutputStream(fileOutputStream);
					ByteStreams.copy(fileInputStream, gzipOutputStream);
					succeed = true;
				} catch (FileNotFoundException e) {
					Log.w(TAG, e);
				} catch (IOException e) {
					Log.w(TAG, e);
					retry = true;
				} finally {
					Closeables.closeQuietly(fileInputStream);
					Closeables.closeQuietly(gzipOutputStream);
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
