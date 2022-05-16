package com.kogasoftware.odt.invehicledevice.service.logsenderservice;

import android.util.Log;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.primitives.Bytes;

import java.io.IOException;
import java.io.InputStream;

/**
 * logcatを受信するスレッド
 */
public class LogcatThread extends Thread {
    private static final String TAG = LogcatThread.class.getSimpleName();
    public static final Long DEFAULT_SPLIT_BYTES = 2 * 1024 * 1024L;
    public static final Long DEFAULT_TIMEOUT_MILLIS = 60 * 60 * 1000L;
    public static final Long WAIT_FOR_AVAILABLE_MILLIS = 200L;
    private final InputStream inputStream;
    private final SplitFileOutputStream splitFileOutputStream;
    private final Long splitBytes;
    private final Long timeoutMillis;

    public LogcatThread(SplitFileOutputStream splitFileOutputStream)
            throws IOException {
        this(splitFileOutputStream, new LogcatInputStream(),
                DEFAULT_SPLIT_BYTES, DEFAULT_TIMEOUT_MILLIS);
    }

    @VisibleForTesting
    public LogcatThread(SplitFileOutputStream splitFileOutputStream,
                        InputStream inputStream, Long splitBytes, Long timeoutMillis) {
        this.splitFileOutputStream = splitFileOutputStream;
        this.inputStream = inputStream;
        this.splitBytes = splitBytes;
        this.timeoutMillis = timeoutMillis;
    }

    @Override
    public void run() {
        Log.i(TAG, "start");
        try {
            while (true) {
                if (splitFileOutputStream.getElapsedMillisSinceFirstWrite() > timeoutMillis) {
                    splitFileOutputStream.split();
                }
                int available = inputStream.available();
                if (available <= 0) {
                    Thread.sleep(WAIT_FOR_AVAILABLE_MILLIS);
                    continue;
                }
                byte[] buffer = new byte[available];
                inputStream.read(buffer);
                boolean written = false;
                if (splitFileOutputStream.getCount() + buffer.length >= splitBytes) {
                    int newLineIndex = Bytes.lastIndexOf(buffer,
                            (byte) '\n');
                    if (newLineIndex >= 0) {
                        splitFileOutputStream
                                .write(buffer, 0, newLineIndex + 1);
                        splitFileOutputStream.split();
                        if (buffer.length >= newLineIndex + 1) {
                            splitFileOutputStream.write(buffer,
                                    newLineIndex + 1, buffer.length
                                            - newLineIndex - 1);
                        }
                        written = true;
                    }
                }
                if (!written) {
                    splitFileOutputStream.write(buffer);
                }
            }
        } catch (IOException e) {
            Log.w(TAG, e);
        } catch (InterruptedException e) {
        } finally {
            try {
                splitFileOutputStream.close();
            } catch (IOException e) {
                Log.w(TAG, e);
            }
            Log.i(TAG, "exit");
        }
    }
}
