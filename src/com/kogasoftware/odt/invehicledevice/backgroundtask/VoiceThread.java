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
import com.kogasoftware.odt.invehicledevice.logic.empty.EmptyThread;
import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;

public class VoiceThread extends Thread {
	public static class SpeakEvent {
		public final String message;

		public SpeakEvent(String message) {
			this.message = message;
		}
	}

	private static class VoiceEntry {
		private final String string;
		private final File file;

		public VoiceEntry(String string, File file) {
			this.string = string;
			this.file = file;
		}

		public String getString() {
			return string;
		}

		public File getFile() {
			return file;
		}
	}

	private static final String TAG = VoiceThread.class.getSimpleName();
	private static final Integer MAX_CACHE_BYTES = 100 * 1024 * 1024;
	private static final Integer MAX_MESSAGE_LENGTH = 200;
	private final BlockingQueue<String> voices = new LinkedBlockingQueue<String>();
	private final BlockingQueue<VoiceEntry> voiceEntries = new LinkedBlockingQueue<VoiceEntry>();
	private final Context context;
	private volatile CountDownLatch speakableLatch = new CountDownLatch(0); // 参照書き換え可能な状態で複数スレッドから読まれるためvolatileをつける

	public VoiceThread(Context context) {
		this.context = context;
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

	@Subscribe
	public void enqueue(SpeakEvent event) {
		for (String message : split(event.message, MAX_MESSAGE_LENGTH)) {
			voices.add(message);
		}
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
							VoiceEntry voiceEntry = voiceEntries.take();
							speak(voiceCache, voiceEntry.getString(),
									voiceEntry.getFile());
						}
					} catch (InterruptedException e) {
					}
				}
			};
			speakThread.start();
			while (true) {
				String voice = voices.take();
				synthesis(voiceCache, voice);
			}
		} catch (IOException e) {
			Log.e(TAG, "IOException", e);
		} catch (InterruptedException e) {
		} finally {
			speakThread.interrupt();
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

	private void synthesis(final VoiceCache voiceCache, final String voice)
			throws InterruptedException {
		try {
			voiceEntries.add(new VoiceEntry(voice, voiceCache.get(voice)));
		} catch (ExecutionException e) {
			Log.w(TAG, "voice=" + voice, e);
			voiceCache.invalidate(voice);
		}
	}

	private void speak(final VoiceCache voiceCache, final String voiceString,
			final File voiceFile) throws InterruptedException {
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
						+ ") voice=\"" + voiceString + "\" at " + voiceFile);
				voiceCache.invalidate(voiceString);
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
			Log.w(TAG, "voice=" + voiceString, e);
			voiceCache.invalidate(voiceString);
		} finally {
			mediaPlayer.release();
		}
	}
}