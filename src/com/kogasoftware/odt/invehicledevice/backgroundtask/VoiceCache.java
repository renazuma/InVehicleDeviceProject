package com.kogasoftware.odt.invehicledevice.backgroundtask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.SerializationException;
import org.apache.commons.lang3.SerializationUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.cache.Weigher;
import com.kogasoftware.odt.invehicledevice.logic.SharedPreferencesKey;
import com.kogasoftware.openjtalk.OpenJTalk;

/**
 * 音声データをキャッシュするクラス
 */
public class VoiceCache {
	private static final String TAG = VoiceCache.class.getSimpleName();

	private static class CacheIndex implements Serializable {
		private static final long serialVersionUID = -8533398684555140766L;
		public AtomicInteger sequence = new AtomicInteger(0);
		public TreeMap<String, File> map = new TreeMap<String, File>();
	}

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
		if (!cacheIndexFile.exists()) {
			return result;
		}
		Boolean succeed = false;
		try {
			Object object = SerializationUtils.deserialize(new FileInputStream(
					cacheIndexFile));
			if (!(object instanceof CacheIndex)) {
				Log.w(TAG, "!(" + object + " instanceof TreeMap<?, ?>)");
				return new TreeMap<String, File>();
			}
			CacheIndex cacheIndex = (CacheIndex) object;
			sequence.set(cacheIndex.sequence.get());
			result.putAll(cacheIndex.map);
			succeed = true;
		} catch (IOException e) {
			Log.w(TAG, e);
		} catch (SerializationException e) {
			Log.e(TAG, e.toString(), e);
		} finally {
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

		Boolean clear = false;
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		clear = preferences.getBoolean(SharedPreferencesKey.CLEAR_VOICE_CACHE,
				false);
		if (clear) {
			SharedPreferences.Editor editor = preferences.edit();
			editor.putBoolean(SharedPreferencesKey.CLEAR_VOICE_CACHE, false);
			editor.commit();
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
		try {
			CacheIndex cacheIndex = new CacheIndex();
			cacheIndex.sequence = sequence;
			cacheIndex.map = new TreeMap<String, File>(cache.asMap());
			SerializationUtils.serialize(cacheIndex, new FileOutputStream(
					cacheIndexFile));
		} catch (IOException e) {
			throw new ExecutionException(e);
		} catch (SerializationException e) {
			throw new ExecutionException(e);
		}
	}
}
