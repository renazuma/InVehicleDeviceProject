package com.kogasoftware.odt.invehicledevice.service.voiceservice;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.SerializationException;
import com.kogasoftware.odt.apiclient.Serializations;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.cache.Weigher;
import com.kogasoftware.openjtalk.OpenJTalk;

/**
 * 音声データをキャッシュするクラス
 */
public class VoiceCache {
	private static final String TAG = VoiceCache.class.getSimpleName();

	private static class InstanceState implements Serializable {
		public InstanceState() {
			this(new AtomicInteger(0), new TreeMap<String, File>());
		}

		public InstanceState(AtomicInteger sequence, Map<String, File> map) {
			this.sequence = new AtomicInteger(sequence.get());
			this.map = new TreeMap<String, File>(map);
		}

		private static final long serialVersionUID = -8533398684555140767L;
		private final AtomicInteger sequence;
		private final TreeMap<String, File> map;
	}

	/**
	 * キャッシュの状態をファイルから読み取る。コンストラクタから使うため、staticメソッドにしておく。
	 * 
	 * @param instanceStateFile
	 * @param sequence
	 * @return
	 */
	protected static InstanceState loadInstanceState(File instanceStateFile) {
		InstanceState result = new InstanceState();
		if (!instanceStateFile.exists()) {
			return result;
		}
		Boolean succeed = false;
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(instanceStateFile);
			result = Serializations.deserialize(fileInputStream,
					InstanceState.class);
			succeed = true;
		} catch (FileNotFoundException e) {
			Log.e(TAG, e.toString(), e);
		} catch (SerializationException e) {
			Log.e(TAG, e.toString(), e);
		}
		if (!succeed) {
			if (!instanceStateFile.delete()) {
				Log.w(TAG, "!\"" + instanceStateFile + "\".delete()");
			}
			return new InstanceState();
		}
		return result;
	}

	private final OpenJTalk openJTalk;
	private final AtomicInteger sequence = new AtomicInteger(0);
	private final File outputDirectory;
	private final File instanceStateFile;
	private final Cache<String, File> cache;
	private final AtomicBoolean dirty = new AtomicBoolean(false);

	public VoiceCache(Context context, Integer maxBytes) throws IOException {
		cache = CacheBuilder.newBuilder().weigher(new Weigher<String, File>() {
			@Override
			public int weigh(String voice, File file) {
				return (int) file.length();
			}
		}).removalListener(new RemovalListener<String, File>() {
			@Override
			public void onRemoval(RemovalNotification<String, File> notification) {
				if (!notification.getValue().delete()) {
					Log.w(TAG, "!\"" + notification.getValue() + "\".delete()");
				}
			}
		}).maximumWeight(maxBytes).build();

		String s = File.separator;
		File dataDirectory = new File(Environment.getExternalStorageDirectory()
				+ s + ".odt" + s + "open_jtalk");
		if (!dataDirectory.isDirectory()) {
			throw new IOException("!(" + dataDirectory + ").isDirectory()");
		}
		File libraryDirectory = context.getFilesDir();

		File voiceDirectory = new File(dataDirectory + s + "voice" + s
				+ "mei_normal");
		File dictionaryDirectory = new File(dataDirectory + s + "dictionary");
		File outputDirectoryBase = context.getExternalFilesDir("open_jtalk");
		if (outputDirectoryBase == null) {
			throw new IOException(
					"context.getExternalFilesDir(\"open_jtalk\") is null");
		}
		outputDirectory = new File(outputDirectoryBase, "output");
		if (!outputDirectory.exists() && !outputDirectory.mkdirs()) {
			throw new IOException("!\"" + outputDirectory + "\".mkdirs()");
		}
		instanceStateFile = new File(outputDirectory + s + "index.serialized");

		openJTalk = new OpenJTalk(voiceDirectory, dictionaryDirectory,
				libraryDirectory);

		InstanceState instanceState = loadInstanceState(instanceStateFile);
		sequence.set(instanceState.sequence.get());
		for (Entry<String, File> entry : instanceState.map.entrySet()) {
			cache.put(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * voiceに対応する音声ファイルを取得
	 * 
	 * @param voice
	 * @return
	 * @throws ExecutionException
	 */
	public File get(final String voice) throws ExecutionException {
		File result = cache.get(voice, new Callable<File>() {
			@Override
			public File call() throws IOException {
				return load(voice);
			}
		});

		if (dirty.getAndSet(false)) {
			saveInstanceState();
		}
		return result;
	}

	public void invalidate(String voice) {
		cache.invalidate(voice);
	}

	protected File load(String voice) throws IOException {
		File file = new File(outputDirectory + File.separator
				+ sequence.getAndIncrement() + ".wav");
		openJTalk.synthesis(file, voice);
		dirty.set(true);
		return file;
	}

	protected void saveInstanceState() throws ExecutionException {
		try {
			InstanceState instanceState = new InstanceState(sequence,
					cache.asMap());
			Serializations.serialize(instanceState, new FileOutputStream(
					instanceStateFile));
		} catch (IOException e) {
			throw new ExecutionException(e);
		} catch (SerializationException e) {
			throw new ExecutionException(e);
		}
	}

	public File getIfPresent(String voice) {
		return cache.getIfPresent(voice);
	}
}
