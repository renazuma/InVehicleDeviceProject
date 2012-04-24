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
	private final File cacheDirectory;
	private final File dictionaryDirectory;
	private final File outputDirectory;
	private final File voiceDirectory;

	private final BlockingQueue<String> voices = new LinkedBlockingQueue<String>();

	public VoiceThread(Context context) {
		cacheDirectory = context.getFilesDir();
		String s = File.separator;
		File base = context.getExternalFilesDir("open_jtalk");
		voiceDirectory = new File(base + s + "voice" + s + "mei_normal");
		dictionaryDirectory = new File(base + s + "dictionary");
		outputDirectory = new File(base + s + "output");
	}

	@Override
	public void run() {
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
				// ここのIOExceptionは無視して良いかも
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