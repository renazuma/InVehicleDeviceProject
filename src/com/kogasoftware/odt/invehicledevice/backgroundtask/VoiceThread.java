package com.kogasoftware.odt.invehicledevice.backgroundtask;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.util.Log;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;

public class VoiceThread extends Thread {
	public static class SpeakEvent {
		public final String message;

		public SpeakEvent(String message) {
			this.message = message;
		}
	}

	private static final String TAG = VoiceThread.class.getSimpleName();
	private static final Integer MAX_CACHE_BYTES = 100 * 1024 * 1024;
	private static final Integer MAX_MESSAGE_LENGTH = 200;
	private final BlockingQueue<String> voices = new LinkedBlockingQueue<String>();
	private final Context context;
	private volatile CountDownLatch speakableLatch = new CountDownLatch(0); // 参照書き換え可能な状態で複数スレッドから読まれるためvolatileをつける

	public VoiceThread(Context context) {
		this.context = context;
	}

	public static List<String> split(String message, Integer maxLineLength) {
		List<String> result = new LinkedList<String>();
		for (String line : Splitter.on(CharMatcher.anyOf("\r\n"))
				.omitEmptyStrings().split(message)) {
			if (line.length() <= maxLineLength) {
				result.add(line);
				continue;
			}
			for (String word : Splitter
					.on(CharMatcher.BREAKING_WHITESPACE.or(CharMatcher
							.anyOf("。、　"))).omitEmptyStrings().split(line)) {
				if (word.length() <= maxLineLength) {
					result.add(word);
					continue;
				}
				for (String broken : Splitter.fixedLength(maxLineLength).split(word)) {
					result.add(broken);
				}
			}
		}
		return result;
	}

	@Subscribe
	public void enqueue(SpeakEvent event) {
		for (String message : split(event.message, MAX_MESSAGE_LENGTH)) {
			voices.add(message);
		}
	}

	@Override
	public void run() {
		try {
			VoiceCache voiceCache = new VoiceCache(context, MAX_CACHE_BYTES);
			while (true) {
				String voice = voices.take();
				speak(voiceCache, voice);
			}
		} catch (IOException e) {
			Log.e(TAG, "IOException", e);
		} catch (InterruptedException e) {
		}
	}

	@Subscribe
	public void pause(InVehicleDeviceActivity.PausedEvent e) {
		speakableLatch.countDown();
		speakableLatch = new CountDownLatch(1);
	}

	@Subscribe
	public void resume(InVehicleDeviceActivity.ResumedEvent e) {
		speakableLatch.countDown();
	}

	private void speak(final VoiceCache voiceCache, final String voice)
			throws InterruptedException {
		speakableLatch.await();

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
						+ ") voice=\"" + voice + "\"");
				voiceCache.invalidate(voice);
				semaphore.release();
				return false;
			}
		});
		mediaPlayer.setVolume(1.0f, 1.0f);
		try {
			File voicePath = voiceCache.get(voice);
			mediaPlayer.setDataSource(voicePath.getAbsolutePath());
			mediaPlayer.prepare();
			mediaPlayer.start();
			semaphore.acquire();
		} catch (ExecutionException e) {
			Log.w(TAG, "voice=" + voice, e);
			voiceCache.invalidate(voice);
		} catch (IOException e) {
			Log.w(TAG, "voice=" + voice, e);
			voiceCache.invalidate(voice);
		} finally {
			mediaPlayer.release();
		}
	}
}