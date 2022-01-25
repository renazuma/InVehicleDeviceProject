package com.kogasoftware.odt.invehicledevice.service.logservice;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.kogasoftware.odt.invehicledevice.R;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import androidx.core.app.NotificationCompat;

/**
 * ログの管理
 */
public class LogService extends Service {
    private static final String TAG = LogService.class.getSimpleName();
    public static final long CHECK_DEVICE_INTERVAL_MILLIS = 10 * 1000;
    public static final String LOGCAT_FILE_TAG = "_logcat_";
    public static final String DROPBOX_FILE_TAG = "_dropbox_";
    private final BlockingQueue<File> rawLogFiles = new LinkedBlockingQueue<>();
    private final BlockingQueue<File> compressedLogFiles = new LinkedBlockingQueue<>();
    private final SendLogBroadcastReceiver sendLogBroadcastReceiver = new SendLogBroadcastReceiver(
            rawLogFiles);
    private Boolean destroyed = false;
    private Thread logcatThread = new Thread();
    private Thread compressThread = new Thread();
    private Thread uploadThread = new Thread();
    private final List<Closeable> closeables = new LinkedList<>();

    /**
     * シャットダウン時、可能な限りSDカードのマウントが解除される前に書込み中のログをフラッシュするため、
     * ACTION_SHUTDOWNを受信して処理を行う。
     */
    public static class ShutdownBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            context.stopService(new Intent(context, LogService.class));
        }
    }

    public File getDataDirectory() {
        return new File(Environment.getExternalStorageDirectory()
                + File.separator + ".odt" + File.separator + "log");
    }

    public static void waitForDataDirectory(File directory)
            throws InterruptedException {
        while (true) {
            Thread.sleep(CHECK_DEVICE_INTERVAL_MILLIS);
            if (!Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                continue;
            }
            if (!directory.exists() && !directory.mkdirs()) {
                Log.w(TAG, "!\"" + directory + "\".mkdirs()");
                continue;
            }
            if (!directory.canWrite()) {
                Log.w(TAG, "!\"" + directory + "\".canWrite()");
                continue;
            }
            break;
        }
    }

    public UploadThread createUploadThread(
            BlockingQueue<File> compressedLogFiles) {
        return new UploadThread(this, compressedLogFiles);
    }

    /**
     * スレッド開始。onDestroy()発生後に行われるのを防ぐためメインスレッドで実行する。
     */
    public Boolean startLog(SplitFileOutputStream logcatSplitFileOutputStream) {
        closeables.add(logcatSplitFileOutputStream);

        if (destroyed) {
            Log.i(TAG, "destroyed=" + destroyed + " / startLog returned");
            return false;
        }

        compressThread = new CompressThread(rawLogFiles,
                compressedLogFiles);
        uploadThread = createUploadThread(compressedLogFiles);

        try {
            logcatThread = new LogcatThread(logcatSplitFileOutputStream);
        } catch (IOException e) {
            Log.wtf(TAG, e);
            // ログが出力できない致命的なエラーのため、サービスをクラッシュさせ再起動させる
            throw new RuntimeException("can't create log");
        }

        logcatThread.start();
        compressThread.start();
        uploadThread.start();

        return true;
    }

    @Override
    public void onCreate() {
        destroyed = false;
        super.onCreate();
        Log.i(TAG, "onCreate()");

        registerReceiver(sendLogBroadcastReceiver, new IntentFilter(
                SendLogBroadcastReceiver.ACTION_SEND_LOG));

        final Handler handler = new Handler();

        // IOを子スレッドで行う
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    // ディレクトリ準備完了を別スレッドで待つ
                    final File dataDirectory = getDataDirectory();
                    waitForDataDirectory(dataDirectory);

                    // ディレクトリに最初から存在しているファイルを読み取る
                    rawLogFiles.addAll(getRawLogFiles(dataDirectory));
                    compressedLogFiles
                            .addAll(getCompressedLogFiles(dataDirectory));

                    // ディレクトリ準備完了後にストリームと出力ファイルを準備する
                    final SplitFileOutputStream logcatSplitFileOutputStream = new SplitFileOutputStream(
                            dataDirectory, LOGCAT_FILE_TAG, rawLogFiles);

                    // スレッド開始は、onDestroy()発生後に行われるのを防ぐためメインスレッドで行う。
                    boolean posted = handler.post(() -> startLog(logcatSplitFileOutputStream));
                    if (!posted) {
                        try {
                            logcatSplitFileOutputStream.close();
                        } catch (IOException e) {
                            Log.w(TAG, e);
                        }
                    }
                } catch (InterruptedException e) {
                } catch (IOException e) {
                    // 致命的なエラーのため、再起動する
                    Log.e(TAG, e.toString(), e);
                    stopSelf();
                }
            }
        };
        thread.start();
    }

    @Override
    public void onDestroy() {
        destroyed = true;
        super.onDestroy();
        Log.i(TAG, "onDestroy()");
        logcatThread.interrupt();
        compressThread.interrupt();
        uploadThread.interrupt();
        for (Closeable closeable : closeables) {
            try {
                closeable.close();
            } catch (IOException e) {
                Log.w(TAG, e);
            }
        }
        try {
            unregisterReceiver(sendLogBroadcastReceiver);
        } catch (IllegalArgumentException e) {
            Log.i(TAG, e.toString(), e);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static List<File> getCompressedLogFiles(File directory) {
        List<File> files = new LinkedList<>();
        File[] defaultFiles = directory.listFiles((dir, filename) -> filename.endsWith(".gz"));
        if (defaultFiles != null) {
            files.addAll(Arrays.asList(defaultFiles));
        }
        return files;
    }

    public static List<File> getRawLogFiles(File directory) {
        List<File> files = new LinkedList<>();
        File[] defaultFiles = directory.listFiles((dir, filename) -> filename.endsWith(".log"));
        if (defaultFiles != null) {
            files.addAll(Arrays.asList(defaultFiles));
        }
        return files;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i(TAG, "onStartCommand(" + intent + ", " + flags + ", " + startId + ")");

        // TODO: OS8.0以降は、サービスをバックグラウンドで動かし続けるために、通知の実装とフォアグラウンドの偽装が必須になる。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // TODO: idはアプリ内で一意である必要がある。管理まで手が回らないので重複しない様に固定としている。
            String channelId = "log_channel";
            String channelName = "log_channel";
            String notificationTitle = "ログ保存サービス";
            String notificationText = "アプリケーションのログを保存しています";
            // TODO: idはアプリ内で一意である必要がある。管理まで手が回らないので重複しない様に固定としている。
            int notificationId = 2;

            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
            Notification notification = new NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(notificationTitle)
                    .setContentText(notificationText)
                    .build();
            startForeground(notificationId, notification);
        }

        return Service.START_STICKY;
    }
}
