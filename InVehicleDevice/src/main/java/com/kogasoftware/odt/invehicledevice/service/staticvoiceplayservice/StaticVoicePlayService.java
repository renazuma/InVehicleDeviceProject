package com.kogasoftware.odt.invehicledevice.service.staticvoiceplayservice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.kogasoftware.odt.invehicledevice.service.staticvoiceplayservice.voice.Voice;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 静的な音声ファイルを再生するサービスです。
 * Created by tnoda on 2017/06/12.
 */

public class StaticVoicePlayService extends Service {
    public final static String VOICE_ACTION = "VOICE_ACTION";
    public final static String VOICE_FILE_KEY = "VOICE_FILE_KEY";
    private final String TAG = StaticVoicePlayService.class.getSimpleName();

    private Thread playThread;
    private final BlockingQueue<Voice> playFiles = new LinkedBlockingQueue<>();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        playThread = new StaticVoicePlayThread(this, playFiles);
        playThread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i(TAG, "onStartCommand(" + intent + ", " + flags + ", " + startId + ")");

        if (intent != null && VOICE_ACTION.equals(intent.getAction())) {
            Voice voice = (Voice) intent.getSerializableExtra(VOICE_FILE_KEY);
            this.playFiles.add(voice);
        }

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        playThread.interrupt();
    }

    public static void playVoice(Context context, Voice voice) {
        Intent intent = new Intent(VOICE_ACTION, null, context,
                StaticVoicePlayService.class);
        intent.putExtra(VOICE_FILE_KEY, voice);
        context.startService(intent);
    }
}
