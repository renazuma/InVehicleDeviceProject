package com.kogasoftware.odt.invehicledevice.ui.frametask.navigation.tilepipeline;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.util.Log;

public class TileBitmapCleaner implements Runnable {
	private static final String TAG = TileBitmapCleaner.class.getSimpleName();
	public static final Long RUN_INTERVAL_MILLIS = 60 * 60 * 1000L;
	private final File directory;
	private final Integer limit;

	public TileBitmapCleaner(Context context) {
		this(TilePipeline.getOutputDirectory(context), 20 * 1024 * 1024);
	}

	public TileBitmapCleaner(File directory, Integer limit) {
		this.directory = directory;
		this.limit = limit;
	}

	@Override
	public void run() {
		File[] fileArray = directory.listFiles();
		if (fileArray == null) {
			return;
		}
		List<File> files = Arrays.asList(fileArray);
		Collections.shuffle(files);

		// 本当は最終アクセス日が古いファイルから削除したいが、それが難しいようなので、ランダムで削除する
		Long total = 0l;
		for (File file : files) {
			Long length = file.length();
			if (total + length > limit) {
				if (!file.delete()) {
					Log.e(TAG, "!\"" + file + "\".delete()");
				}
			} else {
				total = total += length;
			}
		}
	}
}
