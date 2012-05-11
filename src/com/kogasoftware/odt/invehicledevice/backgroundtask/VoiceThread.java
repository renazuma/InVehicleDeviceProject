package com.kogasoftware.odt.invehicledevice.backgroundtask;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.util.Log;

import com.google.common.eventbus.Subscribe;
import com.kogasoftware.openjtalk.OpenJTalk;

public class VoiceThread extends Thread {
	public static class SpeakEvent {
		public final String message;

		public SpeakEvent(String message) {
			this.message = message;
		}
	}

	private static final String TAG = VoiceThread.class.getSimpleName();
	private final File dataDirectory;
	private final File cacheDirectory;
	private final BlockingQueue<String> voices = new LinkedBlockingQueue<String>();

	public VoiceThread(Context context) {
		dataDirectory = context.getExternalFilesDir("open_jtalk");
		cacheDirectory = context.getFilesDir();
	}

	@Override
	public void run() {
		if (!dataDirectory.isDirectory()) {
			Log.w(TAG, "!(" + dataDirectory + ").isDirectory()");
			return;
		}
		String s = File.separator;
		File voiceDirectory = new File(dataDirectory + s + "voice" + s
				+ "mei_normal");
		File dictionaryDirectory = new File(dataDirectory + s + "dictionary");
		File outputDirectory = new File(dataDirectory + s + "output");

		try {
			if (!outputDirectory.exists() && !outputDirectory.mkdirs()) {
				throw new IOException("!\"" + outputDirectory + "\".mkdirs()");
			}
			OpenJTalk openJTalk = new OpenJTalk(voiceDirectory,
					dictionaryDirectory, cacheDirectory);
			for (Integer serial = 0; true; ++serial) {
				String voice = voices.take();
				File outputFile = new File(outputDirectory + File.separator
						+ serial + ".wav");
				openJTalk.synthesis(outputFile, voice); // TODO
				MediaPlayer mediaPlayer = new MediaPlayer();
				try {
					mediaPlayer.setDataSource(outputFile.getAbsolutePath());
					mediaPlayer.prepare();

					final Semaphore semaphore = new Semaphore(0);
					mediaPlayer
							.setOnCompletionListener(new OnCompletionListener() {
								@Override
								public void onCompletion(MediaPlayer mp) {
									semaphore.release();
								}
							});
					mediaPlayer.setOnErrorListener(new OnErrorListener() {
						@Override
						public boolean onError(MediaPlayer mp, int what,
								int extra) {
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
		} catch (IOException e) {
			Log.e(TAG, "IOException", e);
		} catch (InterruptedException e) {
		}
	}

	@Subscribe
	public void speak(SpeakEvent event) {
		voices.add(event.message);
	}
}