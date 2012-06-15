package com.kogasoftware.odt.invehicledevice.ui.modalview.navigation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import com.google.common.base.Optional;
import com.google.common.io.Closeables;
import com.google.common.math.DoubleMath;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.datasource.DataSource;
import com.kogasoftware.odt.invehicledevice.logic.datasource.EmptyDataSource;
import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;
import com.kogasoftware.odt.webapi.WebAPIException;

/**
 * 地図タイルファイルの Web → ファイル → メモリ → テクスチャ のロードのパイプラインを管理するクラス
 */
public class TilePipeline {
	private static final String TAG = TilePipeline.class.getSimpleName();
	private static final Object CACHE_FILE_ACCESS_LOCK = new Object();
	private static final Integer MAX_TEXTURE_TRANSFER_COUNT = 10;
	private volatile CommonLogic commonLogic = new CommonLogic();
	private DataSource dataSource = new EmptyDataSource();
	private final File outputDirectory;
	private final Set<TileKey> processingTileKeys = new CopyOnWriteArraySet<TileKey>();
	private final NavigableMap<TileKey, Integer> connections = new ConcurrentSkipListMap<TileKey, Integer>();
	private final NavigableMap<TileKey, File> files = new ConcurrentSkipListMap<TileKey, File>();
	private final NavigableMap<TileKey, Bitmap> bitmaps = new ConcurrentSkipListMap<TileKey, Bitmap>();
	private final NavigableMap<TileKey, TileFrameTask> tileFrameTasks = new ConcurrentSkipListMap<TileKey, TileFrameTask>();
	private final BlockingQueue<TileKey> startupTileKeys = new LinkedBlockingQueue<TileKey>();

	private ExecutorService webToFileLoaders = Executors.newFixedThreadPool(10);
	private ExecutorService fileToBitmapLoaders = Executors
			.newFixedThreadPool(10);
	private ExecutorService startupLoaders = Executors.newFixedThreadPool(10);

	private class GetMapTileCallback implements WebAPICallback<Bitmap> {
		private final TileKey tileKey;

		public GetMapTileCallback(TileKey tileKey) {
			this.tileKey = tileKey;
		}

		@Override
		public void onException(int reqkey, WebAPIException ex) {
			dataSource.cancel(reqkey);
		}

		@Override
		public void onFailed(int reqkey, int statusCode, String response) {
			dataSource.cancel(reqkey);
		}

		@Override
		public void onSucceed(int reqkey, int statusCode, Bitmap bitmap) {
			try {
				alignAndSaveBitmap(tileKey, bitmap, false);
			} catch (FileNotFoundException e) {
				Log.w(TAG, e);
			}
		}
	}

	private class StartupLoader implements Runnable {
		@Override
		public void run() {
			try {
				while (true) {
					TileKey tileKey = startupTileKeys.take();
					File file = new File(outputDirectory, tileKey.toFileName());
					if (file.exists()) {
						files.put(tileKey, file);
					} else {
						Integer reqkey = dataSource.getMapTile(
								tileKey.getCenter(), tileKey.getZoom(),
								new GetMapTileCallback(tileKey));
						connections.put(tileKey, reqkey);
					}
				}
			} catch (InterruptedException e) {
			}
		}
	}

	private class WebToFileLoader implements Runnable {
		@Override
		public void run() {
		}
	}

	private class FileToBitmapLoader implements Runnable {
		@Override
		public void run() {
		}
	}

	public TilePipeline(Context context) throws IOException {
		outputDirectory = context.getExternalFilesDir("tile");
		if (outputDirectory == null) {
			throw new IOException(
					"context.getExternalFilesDir(\"tile\") is null");
		}
		if (!outputDirectory.exists() && !outputDirectory.mkdirs()) {
			throw new IOException("!\"" + outputDirectory + "\".mkdirs()");
		}
	}

	public void transferGL(GL10 gl, FrameState frameState) {
		// 読み込まれたBitmapをTextureに変換
		for (Integer i = 0; i < MAX_TEXTURE_TRANSFER_COUNT; ++i) {
			Entry<TileKey, Bitmap> entry = bitmaps.pollFirstEntry();
			if (entry == null) {
				break;
			}
			Bitmap bitmap = entry.getValue();
			TileKey tileKey = entry.getKey();
			Integer textureId = Texture.generate(gl);
			Texture.update(gl, textureId, bitmap);
			bitmap.recycle();
			tileFrameTasks.put(tileKey, new TileFrameTask(tileKey, textureId,
					frameState.getMilliSeconds()));
		}
	}

	/**
	 * 指定されたzoomLevel以外のデータをパイプラインから消去する
	 */
	public void changeZoomLevel(int zoomLevel) {
	}

	/**
	 * 指定されたTileFrameTaskがメモリに存在したら取得し、存在しない場合はロードを開始する
	 */
	public Optional<TileFrameTask> pollOrStartLoad(TileKey tileKey) {
		TileFrameTask tileFrameTask = tileFrameTasks.get(tileKey);
		if (tileFrameTask != null) {
			tileFrameTasks.remove(tileKey);
			processingTileKeys.remove(tileKey);
			return Optional.of(tileFrameTask);
		}
		startLoad(tileKey);
		return Optional.absent();
	}

	public void setCommonLogic(CommonLogic commonLogic) {
		this.commonLogic.dispose();
		this.commonLogic = commonLogic;
		dataSource = commonLogic.getDataSource();
	}

	public Bitmap getBitmap(TileKey key) throws IOException,
			InterruptedException {
		File file = getFile(key);
		Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
		if (bitmap == null) {
			throw new IOException("BitmapFactory.decodeFile(" + file
					+ ") failed");
		}
		return alignAndSaveBitmap(key, bitmap, false);
	}

	public Bitmap alignAndSaveBitmap(TileKey key, Bitmap bitmap,
			boolean alwaysSave) throws FileNotFoundException {
		File file = new File(outputDirectory, key.toFileName());
		int alignedLength = (int) Math.pow(
				2,
				Math.floor(DoubleMath.log2(Math.max(bitmap.getWidth(),
						bitmap.getHeight()))));
		if (bitmap.getWidth() == alignedLength
				&& bitmap.getHeight() == alignedLength) {
			if (!alwaysSave) {
				return bitmap;
			}
		} else {
			Bitmap alignedBitmap = Bitmap.createBitmap(alignedLength,
					alignedLength, Bitmap.Config.RGB_565);
			Float left = (float) (alignedLength - bitmap.getWidth()) / 2;
			Float top = (float) (alignedLength - bitmap.getHeight()) / 2;
			new Canvas(alignedBitmap)
					.drawBitmap(bitmap, left, top, new Paint());
			bitmap.recycle();
			bitmap = alignedBitmap;
		}

		synchronized (CACHE_FILE_ACCESS_LOCK) {
			FileOutputStream fileOutputStream = null;
			try {
				fileOutputStream = new FileOutputStream(file);
				bitmap.compress(CompressFormat.PNG, 100, fileOutputStream);
			} finally {
				Closeables.closeQuietly(fileOutputStream);
				bitmap.recycle();
			}
		}
		return bitmap;
	}

	public void startLoad(TileKey key) {
		if (processingTileKeys.add(key)) {
			startupTileKeys.add(key);
		}
	}

	public File getFile(TileKey key) throws IOException, InterruptedException {
		File file = new File(outputDirectory, key.toFileName());
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
						dataSource.cancel(reqkey);
					}

					@Override
					public void onFailed(int reqkey, int statusCode,
							String response) {
						dataSource.cancel(reqkey);
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
			throw new IOException("bitmap == null");
		}
		alignAndSaveBitmap(key, bitmap, true).recycle();
		return file;
	}

	public void pause() {
		webToFileLoaders.shutdownNow();
		fileToBitmapLoaders.shutdownNow();
	}

	public void resume() {
		webToFileLoaders = Executors.newFixedThreadPool(10);
		fileToBitmapLoaders = Executors.newFixedThreadPool(10);
	}
}
