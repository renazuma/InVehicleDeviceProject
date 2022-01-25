package com.kogasoftware.odt.invehicledevice.service.logservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.util.concurrent.BlockingQueue;

/**
 * クラッシュログをLogServiceReportSenderから受信
 */
public class SendLogBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = SendLogBroadcastReceiver.class
            .getSimpleName();
    public static final String ACTION_SEND_LOG = LogService.class
            .getSimpleName() + ".ACTION_SEND_LOG";
    public static final String EXTRAS_KEY_LOG_FILE_NAME = "file";
    private final BlockingQueue<File> outputLogFiles;

    public SendLogBroadcastReceiver(BlockingQueue<File> outputLogFiles) {
        this.outputLogFiles = outputLogFiles;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            Log.w(TAG, "onReceive intent == null");
            return;
        }
        Bundle extras = intent.getExtras();
        if (extras == null) {
            Log.w(TAG, "onReceive intent.getExtras() == null");
            return;
        }
        String fileString = extras.getString(EXTRAS_KEY_LOG_FILE_NAME);
        if (fileString == null) {
            Log.w(TAG, "onReceive intent.getExtras().getString() == null");
            return;
        }
        File file = new File(fileString);
        if (!file.exists()) {
            Log.w(TAG, "!\"" + file + "\".exists()");
            return;
        }
        Log.i(TAG, "\"" + file + "\" added");
        outputLogFiles.add(file);
    }
}
