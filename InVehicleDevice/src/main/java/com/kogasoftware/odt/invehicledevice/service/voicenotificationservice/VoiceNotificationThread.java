package com.kogasoftware.odt.invehicledevice.service.voicenotificationservice;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import com.kogasoftware.odt.invehicledevice.service.voicenotificationservice.voice.Voice;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;

/**
 * 静的な音声ファイル再生を直列に行うためのスレッドクラス
 * <p>
 * Created by tnoda on 2017/06/12.
 */

public class VoiceNotificationThread extends Thread {
    private final static String TAG = VoiceNotificationThread.class.getSimpleName();

    private final Context context;
    private final BlockingQueue<Voice> playFiles;
    private final AudioManager audioManager;

    public VoiceNotificationThread(Context context, BlockingQueue<Voice> playFiles) {
        this.context = context;
        this.playFiles = playFiles;
        this.audioManager = (AudioManager) context
                .getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Voice voice = playFiles.take();
                playVoiceFile(voice);
            } catch (InterruptedException e) {
            }

        }
    }

    // 音声ファイルの再生
    private void playVoiceFile(final Voice voice) {
        Log.i(TAG, "voice= " + voice.getClass().getSimpleName());
        final Semaphore semaphore = new Semaphore(0);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
        MediaPlayer mp = MediaPlayer.create(this.context, voice.getVoiceFileResId());
        mp.setOnCompletionListener(mediaPlayer -> semaphore.release());

        mp.setOnErrorListener((mp1, what, extra) -> {
            Log.w(TAG, "onError(" + mp1 + ", " + what + ", " + extra
                    + ") voice=\"" + voice.getClass().getSimpleName() + "\"");
            semaphore.release();
            return false;
        });

        Log.d(TAG, "voice=" + voice.getClass().getSimpleName() + ", volume=" + voice.getVolume());
        mp.setVolume(voice.getVolume(), voice.getVolume());

        try {
            mp.start();
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.w(TAG, "voice=" + voice.getClass().getSimpleName());
        } finally {
            mp.release();
        }
    }
}
