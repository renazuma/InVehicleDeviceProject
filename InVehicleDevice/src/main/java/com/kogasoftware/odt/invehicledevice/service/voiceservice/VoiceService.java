package com.kogasoftware.odt.invehicledevice.service.voiceservice;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;

/**
 * OpenJTalkライブラリが完全に信頼できないため、プロセスを分離し、SEGVが発生してもメイン画面には 影響が出ないようにするためのサービス
 * TODO: TTSとして書き直し
 */
public class VoiceService extends Service {
	public static final String ACTION_SPEAK = VoiceService.class.getName()
			+ ".ACTION_SPEAK";
	public static final String ACTION_ENABLE = VoiceService.class.getName()
			+ ".ACTION_ENABLE";
	public static final String ACTION_DISABLE = VoiceService.class.getName()
			+ ".ACTION_DISABLE";
	public static final String MESSAGE_KEY = "MESSAGE_KEY";
	private static final String TAG = VoiceService.class.getSimpleName();
	private final BlockingQueue<String> voices = new LinkedBlockingQueue<String>();
	private Thread voiceThread;
	private HandlerThread voiceDownloaderClientThread;
	private Boolean enabled = false;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "onCreate");
		voiceThread = new VoiceThread(this, voices);
		voiceDownloaderClientThread = new VoiceDownloaderClientThread(this,
				"voiceDownloaderClientThread");
		voiceThread.start();
		voiceDownloaderClientThread.start();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		Log.i(TAG, "onStartCommand(" + intent + ", " + flags + ", " + startId
				+ ")");
		if (intent == null) {
			return Service.START_STICKY;
		}
		String action = intent.getAction();
		if (action == null) {
			return Service.START_STICKY;
		}
		if (action.equals(ACTION_SPEAK)) {
			if (enabled) {
				String message = intent.getStringExtra(MESSAGE_KEY);
				if (message != null) {
					voices.add(message);
				}
			}
		} else if (action.equals(ACTION_ENABLE)) {
			enabled = true;
		} else if (action.equals(ACTION_DISABLE)) {
			enabled = false;
			voices.clear();
		}
		return Service.START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "onDestroy");
		voiceThread.interrupt();
		voiceDownloaderClientThread.quit();
		voiceDownloaderClientThread.interrupt();
	}

	public static void speak(Context context, String message) {
		Intent intent = new Intent(ACTION_SPEAK, null, context,
				VoiceService.class);
		intent.putExtra(MESSAGE_KEY, message);
		context.startService(intent);
	}

	public static void enable(Context context) {
		context.startService(new Intent(ACTION_ENABLE, null, context,
				VoiceService.class));
	}

	public static void disable(Context context) {
		context.startService(new Intent(ACTION_DISABLE, null, context,
				VoiceService.class));
	}
}
