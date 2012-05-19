package com.kogasoftware.odt.invehicledevice.backgroundtask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.util.Log;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.cache.Weigher;
import com.google.common.io.Closeables;
import com.kogasoftware.openjtalk.OpenJTalk;

/**
 * 音声データをキャッシュするクラス
 */
public class VoiceCache {
	private static final String TAG = VoiceCache.class.getSimpleName();

	/**
	 * キャッシュのインデックスをファイルから読み取る。コンストラクタから使うため、staticメソッドにしておく。
	 * 
	 * @param cacheIndexFile
	 * @param sequence
	 * @return
	 */
	protected static Map<String, File> loadCacheIndex(File cacheIndexFile,
			AtomicInteger sequence) {
		Map<String, File> result = new TreeMap<String, File>();
		FileInputStream fileInputStream = null;
		ObjectInputStream objectInputStream = null;
		Boolean succeed = false;
		try {
			fileInputStream = new FileInputStream(cacheIndexFile);
			objectInputStream = new ObjectInputStream(fileInputStream);
			sequence.set(objectInputStream.readInt());
			Object object = objectInputStream.readObject();
			if (!(object instanceof TreeMap<?, ?>)) {
				Log.w(TAG, "!(" + object + " instanceof TreeMap<?, ?>)");
				return new TreeMap<String, File>();
			}
			Map<String, File> map = new TreeMap<String, File>();
			for (Entry<?, ?> entry : ((Map<?, ?>) object).entrySet()) {
				if (!(entry.getKey() instanceof String)) {
					Log.w(TAG, "!(" + entry + ".getKey() instanceof String)");
				}
				if (!(entry.getValue() instanceof File)) {
					Log.w(TAG, "!(" + entry + ".getValue() instanceof File)");
				}
				map.put((String) entry.getKey(), (File) entry.getValue());
			}
			succeed = true;
		} catch (IOException e) {
			Log.w(TAG, e);
		} catch (ClassNotFoundException e) {
			Log.w(TAG, e);
		} finally {
			Closeables.closeQuietly(objectInputStream);
			Closeables.closeQuietly(fileInputStream);
			if (!succeed && !cacheIndexFile.delete()) {
				Log.w(TAG, "!\"" + cacheIndexFile + "\".delete()");
				return new TreeMap<String, File>();
			}
		}
		return result;
	}

	private final OpenJTalk openJTalk;
	private final AtomicInteger sequence = new AtomicInteger(0);
	private final File outputDirectory;
	private final File cacheIndexFile;
	private final Cache<String, File> cache;
	private final AtomicBoolean dirty = new AtomicBoolean(false);

	public VoiceCache(Context context, Integer maxBytes) throws IOException {
		this(context, maxBytes, false);
	}

	public VoiceCache(Context context, Integer maxBytes, Boolean clear)
			throws IOException {
		cache = CacheBuilder.newBuilder().weigher(new Weigher<String, File>() {
			@Override
			public int weigh(String voice, File file) {
				long length = file.length();
				return (int) length;
			}
		}).removalListener(new RemovalListener<String, File>() {
			@Override
			public void onRemoval(RemovalNotification<String, File> notification) {
				if (!notification.getValue().delete()) {
					Log.w(TAG, "!\"" + notification.getValue() + "\".delete()");
				}
			}
		}).maximumWeight(maxBytes).build();
		File dataDirectory = context.getExternalFilesDir("open_jtalk");
		File libraryDirectory = context.getFilesDir();

		if (!dataDirectory.isDirectory()) {
			throw new IOException("!(" + dataDirectory + ").isDirectory()");
		}
		String s = File.separator;
		File voiceDirectory = new File(dataDirectory + s + "voice" + s
				+ "mei_normal");
		File dictionaryDirectory = new File(dataDirectory + s + "dictionary");
		outputDirectory = new File(dataDirectory + s + "output");
		if (!outputDirectory.exists() && !outputDirectory.mkdirs()) {
			throw new IOException("!\"" + outputDirectory + "\".mkdirs()");
		}
		cacheIndexFile = new File(outputDirectory + s + "index.serialized");

		openJTalk = new OpenJTalk(voiceDirectory, dictionaryDirectory,
				libraryDirectory);

		if (clear) {
			if (!cacheIndexFile.delete()) {
				throw new IOException("!\"" + cacheIndexFile + "\".delete()");
			}
		} else {
			cache.putAll(loadCacheIndex(cacheIndexFile, sequence));
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
			saveCacheIndex();
		}
		return result;
	}

	protected File load(String voice) throws IOException {
		File file = new File(outputDirectory + File.separator
				+ sequence.getAndIncrement() + ".wav");
		openJTalk.synthesis(file, voice);
		dirty.set(true);
		return file;
	}

	protected void saveCacheIndex() throws ExecutionException {
		FileOutputStream fileOutputStream = null;
		ObjectOutputStream objectOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(cacheIndexFile);
			objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeInt(sequence.get());
			objectOutputStream.writeObject(new TreeMap<String, File>(cache
					.asMap()));
		} catch (IOException e) {
			throw new ExecutionException(e);
		} finally {
			Closeables.closeQuietly(objectOutputStream);
			Closeables.closeQuietly(fileOutputStream);
		}
	}
}
