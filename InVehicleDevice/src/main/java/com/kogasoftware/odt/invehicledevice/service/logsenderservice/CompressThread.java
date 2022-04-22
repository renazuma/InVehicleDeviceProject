package com.kogasoftware.odt.invehicledevice.service.logsenderservice;

import android.util.Log;

import com.google.common.io.ByteStreams;
import com.google.common.io.Closer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.zip.GZIPOutputStream;

/**
 * ログの圧縮を行うスレッド
 */
public class CompressThread extends Thread {
    private static final String TAG = CompressThread.class.getSimpleName();
    public static final String COMPRESSED_FILE_SUFFIX = ".gz";
    private final BlockingQueue<File> rawLogFiles;
    private final BlockingQueue<File> compressedLogFiles;

    public CompressThread(BlockingQueue<File> rawLogFiles,
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

                boolean succeed = false;
                boolean retry = false;
                try {
                    compress(rawLogFile, compressedLogFile);
                    succeed = true;
                } catch (FileNotFoundException e) {
                    Log.w(TAG, e);
                } catch (IOException e) {
                    Log.w(TAG, e);
                    retry = true;
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

    private void compress(File rawLogFile, File compressedLogFile)
            throws IOException {
        Closer closer = Closer.create();
        try {
            FileInputStream fileInputStream = closer.register(new FileInputStream(rawLogFile));
            FileOutputStream fileOutputStream = closer.register(new FileOutputStream(
                    compressedLogFile));
            GZIPOutputStream compressOutputStream = closer.register(new GZIPOutputStream(
                    fileOutputStream));
            ByteStreams.copy(fileInputStream, compressOutputStream);
        } catch (Throwable e) {
            throw closer.rethrow(e);
        } finally {
            closer.close();
        }
    }
}
