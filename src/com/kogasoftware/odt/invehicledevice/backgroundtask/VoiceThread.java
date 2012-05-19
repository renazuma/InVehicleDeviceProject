package com.kogasoftware.odt.invehicledevice.backgroundtask;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.util.Log;

import com.google.common.eventbus.Subscribe;

public class VoiceThread extends Thread {
	public static class SpeakEvent {
		public final String message;

		public SpeakEvent(String message) {
			this.message = message;
		}
	}

	private static final String TAG = VoiceThread.class.getSimpleName();
	private static final Integer MAX_CACHE_BYTES = 100 * 1024 * 1024;
	private final BlockingQueue<String> voices = new LinkedBlockingQueue<String>();
	private final Context context;

	public VoiceThread(Context context) {
		this.context = context;
	}

	@Subscribe
	public void enqueue(SpeakEvent event) {
		voices.add(event.message);
	}

	@Override
	public void run() {
		try {
			VoiceCache voiceLoader = new VoiceCache(context, MAX_CACHE_BYTES);
			while (true) {
				String voice = voices.take();
				speak(voiceLoader, voice);
			}
		} catch (IOException e) {
			Log.e(TAG, "IOException", e);
		} catch (InterruptedException e) {
		}
	}

	private void speak(VoiceCache voiceLoader, String voice)
			throws InterruptedException {
		MediaPlayer mediaPlayer = new MediaPlayer();
		try {
			try {
				File voicePath = voiceLoader.get(voice);
				mediaPlayer.setDataSource(voicePath.getAbsolutePath());
			} catch (ExecutionException e) {
				Log.w(TAG, e);
				return;
			}
			mediaPlayer.prepare();

			final Semaphore semaphore = new Semaphore(0);
			mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					semaphore.release();
				}
			});
			mediaPlayer.setOnErrorListener(new OnErrorListener() {
				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					semaphore.release();
					return false;
				}
			});
			mediaPlayer.start();
			semaphore.acquire();
		} catch (IOException e) {
			Log.w(TAG, e);
		} finally {
			mediaPlayer.release();
		}
	}
}