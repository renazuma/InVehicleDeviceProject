package com.kogasoftware.odt.invehicledevice;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;

import com.kogasoftware.openjtalk.OpenJTalk;

public class VoiceThread extends Thread {
	private static final String TAG = VoiceThread.class.getSimpleName();
	private static final Integer TIMEOUT_MILLIS = 20 * 1000;
	private final BlockingQueue<String> voices;
	private final File cacheDirectory;
	private final File voiceDirectory;
	private final File dictionaryDirectory;
	private final File outputDirectory;

	public VoiceThread(Context context, BlockingQueue<String> voices) {
		this.voices = voices;
		cacheDirectory = context.getFilesDir();
		String s = File.separator;
		File base = context.getExternalFilesDir("open_jtalk");
		voiceDirectory = new File(base + s + "voice" + s + "mei_normal");
		dictionaryDirectory = new File(base + s + "dictionary");
		outputDirectory = new File(base + s + "output");
		setName("VoiceThread-" + getId() + "-constructed");
	}

	@Override
	public void run() {
		try {
			setName("VoiceThread-" + getId() + "-working");
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
								public void onCompletion(MediaPlayer arg0) {
									semaphore.release();
								}
							});
					mediaPlayer.start();
					semaphore.tryAcquire(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
				} finally {
					mediaPlayer.release();
				}
			}
		} catch (IOException e) {
			Log.e(TAG, "IOException", e);
		} catch (InterruptedException e) {
		} finally {
			setName("VoiceThread-" + getId() + "-exit");
		}
	}
}