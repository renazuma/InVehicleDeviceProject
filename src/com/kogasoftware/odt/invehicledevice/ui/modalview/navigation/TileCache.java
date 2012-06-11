package com.kogasoftware.odt.invehicledevice.ui.modalview.navigation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.SerializationException;
import org.apache.commons.lang3.SerializationUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.cache.Weigher;
import com.google.common.io.Closeables;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.SharedPreferencesKey;
import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;
import com.kogasoftware.odt.webapi.WebAPIException;

/**
 * TODO: VoiceCacheと共通部分をまとめる
 */
public class TileCache {
	private static final String TAG = TileCache.class.getSimpleName();

	private static class InstanceState implements Serializable {
		public InstanceState() {
			this(new AtomicInteger(0), new TreeMap<TileKey, File>());
		}

		public InstanceState(AtomicInteger sequence, Map<TileKey, File> map) {
			this.sequence = new AtomicInteger(sequence.get());
			this.map = new TreeMap<TileKey, File>(map);
		}

		private static final long serialVersionUID = -8533398684555140768L;
		private final AtomicInteger sequence;
		private final TreeMap<TileKey, File> map;
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
		try {
			Object object = SerializationUtils.deserialize(new FileInputStream(
					instanceStateFile));
			if (!(object instanceof InstanceState)) {
				Log.w(TAG, "!(" + object + " instanceof InstanceState)");
				return new InstanceState();
			}
			result = (InstanceState) object;
			succeed = true;
		} catch (IOException e) {
			Log.w(TAG, e);
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

	private final AtomicInteger sequence = new AtomicInteger(0);
	private final File outputDirectory;
	private final File instanceStateFile;
	private final LoadingCache<TileKey, File> fileCache;

	private final AtomicBoolean dirty = new AtomicBoolean(false);
	private CommonLogic commonLogic = new CommonLogic();

	public TileCache(Context context, Integer maxBytes) throws IOException {
		fileCache = CacheBuilder
				.newBuilder()
				.weigher(new Weigher<TileKey, File>() {
					@Override
					public int weigh(TileKey key, File file) {
						return (int) file.length();
					}
				})
				.removalListener(new RemovalListener<TileKey, File>() {
					@Override
					public void onRemoval(
							RemovalNotification<TileKey, File> notification) {
						if (!notification.getValue().delete()) {
							Log.w(TAG, "!\"" + notification.getValue()
									+ "\".delete()");
						}
					}
				}).maximumWeight(maxBytes)
				.build(new CacheLoader<TileKey, File>() {
					@Override
					public File load(TileKey tileKey) throws Exception {
						return receiveMapTileImage(tileKey);
					}
				});
		outputDirectory = context.getExternalFilesDir("tile");
		if (outputDirectory == null) {
			throw new IOException(
					"context.getExternalFilesDir(\"tile\") is null");
		}
		if (!outputDirectory.exists() && !outputDirectory.mkdirs()) {
			throw new IOException("!\"" + outputDirectory + "\".mkdirs()");
		}
		instanceStateFile = new File(outputDirectory, "index.serialized");

		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		Boolean clear = preferences.getBoolean(
				SharedPreferencesKey.CLEAR_TILE_CACHE, false);
		if (clear) {
			SharedPreferences.Editor editor = preferences.edit();
			editor.putBoolean(SharedPreferencesKey.CLEAR_TILE_CACHE, false);
			editor.commit();
			if (instanceStateFile.exists() && !instanceStateFile.delete()) {
				throw new IOException("!\"" + instanceStateFile + "\".delete()");
			}
		} else {
			InstanceState instanceState = loadInstanceState(instanceStateFile);
			sequence.set(instanceState.sequence.get());
			for (Entry<TileKey, File> entry : instanceState.map.entrySet()) {
				fileCache.put(entry.getKey(), entry.getValue());
			}
		}
	}

	/**
	 * TileKeyに対応するデータを取得
	 * 
	 * @param key
	 * @return
	 * @throws ExecutionException
	 */
	public File get(final TileKey key) throws ExecutionException {
		File result = fileCache.get(key);
		if (dirty.getAndSet(false)) {
			saveInstanceState();
		}
		return result;
	}

	public void invalidate(String voice) {
		fileCache.invalidate(voice);
	}

	// @Override
	// public File load(TileKey key) throws IOException {
	// File file = new File(outputDirectory, key.toFileName() + ".wav");
	// dirty.set(true);
	// return file;
	// }

	protected void saveInstanceState() throws ExecutionException {
		try {
			InstanceState instanceState = new InstanceState(sequence,
					fileCache.asMap());
			SerializationUtils.serialize(instanceState, new FileOutputStream(
					instanceStateFile));
		} catch (IOException e) {
			throw new ExecutionException(e);
		} catch (SerializationException e) {
			throw new ExecutionException(e);
		}
	}

	public File getIfPresent(TileKey key) {
		return fileCache.getIfPresent(key);
	}

	public void refresh(TileKey key) {
		fileCache.refresh(key);
	}

	public File receiveMapTileImage(TileKey key) throws Exception {
		final CountDownLatch countDownLatch = new CountDownLatch(1);
		final AtomicReference<Bitmap> outputBitmap = new AtomicReference<Bitmap>(
				null);
		commonLogic.getDataSource().getMapTile(key.getCenter(), key.getZoom(),
				new WebAPICallback<Bitmap>() {
					@Override
					public void onException(int reqkey, WebAPIException ex) {
					}

					@Override
					public void onFailed(int reqkey, int statusCode,
							String response) {
					}

					@Override
					public void onSucceed(int reqkey, int statusCode,
							Bitmap bitmap) {
						outputBitmap.set(bitmap);
						countDownLatch.countDown();
					}
				});
		countDownLatch.await();
		if (outputBitmap.get() == null) {
			throw new NullPointerException();
		}

		File file = new File(outputDirectory, key.toFileName() + ".png");
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(file);
			outputBitmap.get().compress(CompressFormat.PNG, 100,
					fileOutputStream);
		} finally {
			Closeables.closeQuietly(fileOutputStream);
			outputBitmap.get().recycle();
		}

		return file;
	}

	public void setCommonLogic(CommonLogic commonLogic) {
		this.commonLogic.dispose();
		this.commonLogic = commonLogic;
	}
}