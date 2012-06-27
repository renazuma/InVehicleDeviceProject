package com.kogasoftware.odt.invehicledevice.service.voiceservice;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.util.Log;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.kogasoftware.odt.invehicledevice.empty.EmptyThread;

public class VoiceThread extends Thread {

	private static final String TAG = VoiceThread.class.getSimpleName();
	private static final Integer MAX_CACHE_BYTES = 100 * 1024 * 1024;
	private static final Integer MAX_MESSAGE_LENGTH = 200;
	private final BlockingQueue<String> voices;
	private final BlockingQueue<String> preparedVoices = new LinkedBlockingQueue<String>();
	private final Context context;
	private final AudioManager audioManager;

	public VoiceThread(Context context, BlockingQueue<String> voices) {
		this.context = context;
		this.voices = voices;
		audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
	}

	public static List<String> split(String message, Integer maxLength) {
		List<String> result = new LinkedList<String>();
		for (String line : Splitter.on(CharMatcher.anyOf("\r\n"))
				.omitEmptyStrings().split(message)) {
			if (line.length() <= maxLength) {
				result.add(line);
				continue;
			}
			for (String word : Splitter
					.on(CharMatcher.BREAKING_WHITESPACE.or(CharMatcher
							.anyOf("。、　()（）「」"))).omitEmptyStrings()
					.split(line)) {
				if (word.length() <= maxLength) {
					result.add(word);
					continue;
				}
				for (String broken : Splitter.fixedLength(maxLength)
						.split(word)) {
					result.add(broken);
				}
			}
		}
		return result;
	}

	@Override
	public void run() {
		Thread speakThread = new EmptyThread();
		try {
			final VoiceCache voiceCache = new VoiceCache(context,
					MAX_CACHE_BYTES);
			speakThread = new Thread() {
				@Override
				public void run() {
					try {
						while (true) {
							String voice = preparedVoices.take();
							speak(voiceCache, voice);
						}
					} catch (InterruptedException e) {
					}
				}
			};
			speakThread.start();
			while (true) {
				String voice = voices.take();
				for (String splitted : split(voice, MAX_MESSAGE_LENGTH)) {
					synthesis(voiceCache, splitted);
				}
			}
		} catch (IOException e) {
			Log.e(TAG, "IOException", e);
		} catch (InterruptedException e) {
		} finally {
			speakThread.interrupt();
		}
	}

	private void synthesis(final VoiceCache voiceCache, final String voice)
			throws InterruptedException {
		try {
			voiceCache.get(voice);
		} catch (ExecutionException e) {
			Log.w(TAG, "voice=" + voice, e);
			voiceCache.invalidate(voice);
			return;
		}
		preparedVoices.add(voice);
	}

	private void speak(final VoiceCache voiceCache, final String voice)
			throws InterruptedException {
		final File voiceFile = voiceCache.getIfPresent(voice);
		if (voiceFile == null) {
			Log.w(TAG, "voice=\"" + voice + "\" is not present");
			return;
		}
		
		Log.i(TAG, "speak voice=" + voice);
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
				audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

		final Semaphore semaphore = new Semaphore(0);
		MediaPlayer mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				semaphore.release();
			}
		});
		mediaPlayer.setOnErrorListener(new OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				Log.w(TAG, "onError(" + mp + ", " + what + ", " + extra
						+ ") voice=\"" + voice + "\" at " + voiceFile);
				voiceCache.invalidate(voice);
				semaphore.release();
				return false;
			}
		});
		mediaPlayer.setVolume(1.0f, 1.0f);
		try {
			mediaPlayer.setDataSource(voiceFile.getAbsolutePath());
			mediaPlayer.prepare();
			mediaPlayer.start();
			semaphore.acquire();
		} catch (IOException e) {
			Log.w(TAG, "voice=" + voice, e);
			voiceCache.invalidate(voice);
		} finally {
			mediaPlayer.release();
		}
	}
}
