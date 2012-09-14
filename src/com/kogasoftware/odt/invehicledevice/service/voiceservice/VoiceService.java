package com.kogasoftware.odt.invehicledevice.service.voiceservice;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.kogasoftware.odt.invehicledevice.empty.EmptyThread;

/**
 * OpenJTalkライブラリのプロセスを分離するためのサービス TODO: TTSとして書き直し
 */
public class VoiceService extends Service {
	public static final String ACTION_VOICE = VoiceService.class.getName()
			+ ".ACTION_VOICE";
	public static final String MESSAGE_KEY = "MESSAGE_KEY";
	private static final String TAG = VoiceService.class.getSimpleName();
	private final BlockingQueue<String> voices = new LinkedBlockingQueue<String>();
	private Thread voiceThread = new EmptyThread();

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		voiceThread = new VoiceThread(this, voices);
		voiceThread.start();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		Log.i(TAG, "onStartCommand(" + intent + ", " + flags + ", " + startId
				+ ")");
		if (intent != null && intent.getAction() != null
				&& intent.getAction().equals(ACTION_VOICE)) {
			List<CharSequence> messages = intent
					.getCharSequenceArrayListExtra(MESSAGE_KEY);
			if (messages != null) {
				for (CharSequence message : messages) {
					voices.add(message.toString());
				}
			}
		}
		return Service.START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		voiceThread.interrupt();
	}
}
