package com.kogasoftware.odt.invehicledevice;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Log;

import com.kogasoftware.openjtalk.OpenJTalk;

public class VoiceThread extends Thread {
	private final String T = LogTag.get(VoiceThread.class);
	private final BlockingQueue<String> voices;

	private final Context context;

	public VoiceThread(Context context, BlockingQueue<String> voices) {
		this.voices = voices;
		this.context = context;
	}

	public void run2() {
		String s = File.separator;
		String base = Environment.getExternalStorageDirectory() + s + ".odt"
				+ s + "openjtalk";
		File voiceDirectory = new File(base + s + "voice" + s + "mei_normal");
		File dictionaryDirectory = new File(base + s + "dictionary");

		try {
			OpenJTalk openJTalk = new OpenJTalk(context, voiceDirectory,
					dictionaryDirectory);
			for (Integer serial = 0; true; ++serial) {
				String voice = voices.take();
				File output = new File(
						Environment.getExternalStorageDirectory() + s + serial
								+ ".wav");
				openJTalk.synthesis(output, voice); // TODO
													// ここのIOExceptionは無視して良いかも
				MediaPlayer mediaPlayer = new MediaPlayer();
				mediaPlayer.setDataSource(output.getAbsolutePath());
				mediaPlayer.prepare();
				mediaPlayer.start();
			}
		} catch (IOException e) {
			Log.e(T, "IOException", e);
		} catch (InterruptedException e) {
		}
	}
}