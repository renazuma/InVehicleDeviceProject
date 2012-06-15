package com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.map;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.cache.Weigher;
import com.google.common.io.Closeables;
import com.google.common.math.DoubleMath;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;
import com.kogasoftware.odt.webapi.WebAPIException;

/**
 * TODO: VoiceCacheと共通部分をまとめる
 */
public class TileFileCache {
	private static final String TAG = TileFileCache.class.getSimpleName();
	private static final Object CACHE_FILE_ACCESS_LOCK = new Object(); // ファイルアクセス中のスレッドを一つに制限するためのロック。将来的にはロックの粒度をファイル毎にする必要があるかもしれない。
	private final File outputDirectory;
	private final LoadingCache<TileKey, File> fileCache;
	private volatile CommonLogic commonLogic = new CommonLogic();

	public TileFileCache(Context context, Integer maxBytes) throws IOException {
		fileCache = CacheBuilder
				.newBuilder()
				.weigher(new Weigher<TileKey, File>() {
					@Override
					public int weigh(TileKey key, File file) {
						synchronized (CACHE_FILE_ACCESS_LOCK) {
							return (int) file.length();
						}
					}
				})
				.removalListener(new RemovalListener<TileKey, File>() {
					@Override
					public void onRemoval(
							RemovalNotification<TileKey, File> notification) {
						synchronized (CACHE_FILE_ACCESS_LOCK) {
							if (!notification.getValue().delete()) {
								Log.w(TAG, "!\"" + notification.getValue()
										+ "\".delete()");
							}
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
	}

	/**
	 * TileKeyに対応するデータを取得
	 * 
	 * @param key
	 * @return
	 * @throws ExecutionException
	 */
	public File get(final TileKey key) throws ExecutionException {
		return fileCache.get(key);
	}

	public void invalidate(TileKey key) {
		fileCache.invalidate(key);
	}

	public File getIfPresent(TileKey key) {
		return fileCache.getIfPresent(key);
	}

	public File receiveMapTileImage(TileKey key) throws Exception {
		File file = new File(outputDirectory, key.toFileName() + ".png");
		synchronized (CACHE_FILE_ACCESS_LOCK) {
			if (file.exists()) {
				return file;
			}
		}
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
		Bitmap bitmap = outputBitmap.get();
		if (bitmap == null) {
			throw new NullPointerException();
		}

		int alignedLength = (int) Math.pow(
				2,
				Math.floor(DoubleMath.log2(Math.max(bitmap.getWidth(),
						bitmap.getHeight()))));
		Bitmap alignedBitmap = Bitmap.createBitmap(alignedLength,
				alignedLength, Bitmap.Config.RGB_565);
		Float left = (float) (alignedLength - bitmap.getWidth()) / 2;
		Float top = (float) (alignedLength - bitmap.getHeight()) / 2;
		new Canvas(alignedBitmap).drawBitmap(bitmap, left, top, new Paint());
		bitmap.recycle();

		synchronized (CACHE_FILE_ACCESS_LOCK) {
			FileOutputStream fileOutputStream = null;
			try {
				fileOutputStream = new FileOutputStream(file);
				alignedBitmap.compress(CompressFormat.PNG, 100,
						fileOutputStream);
			} finally {
				Closeables.closeQuietly(fileOutputStream);
				alignedBitmap.recycle();
			}
		}
		return file;
	}

	public void setCommonLogic(CommonLogic commonLogic) {
		this.commonLogic.dispose();
		this.commonLogic = commonLogic;
	}
}