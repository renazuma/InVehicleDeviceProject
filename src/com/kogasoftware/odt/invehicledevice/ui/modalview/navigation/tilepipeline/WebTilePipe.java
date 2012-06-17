package com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.tilepipeline;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.ObjectUtils.Null;

import android.graphics.Bitmap;

import com.google.common.base.Predicate;
import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;
import com.kogasoftware.odt.webapi.WebAPIException;

public class WebTilePipe extends PipeExchanger<Tile, Null, TileBitmapFile> {
	private final File outputDirectory;
	private final Map<Tile, Integer> loadingReqKeys = new ConcurrentHashMap<Tile, Integer>();

	public WebTilePipe(PipeQueue<Tile, Null> fromPipeQueue,
			PipeQueue<Tile, TileBitmapFile> toPipeQueue,
			Predicate<Tile> isValid, File outputDirectory) {
		super(fromPipeQueue, toPipeQueue, isValid);
		this.outputDirectory = outputDirectory;
	}

	@Override
	public void cancel(Tile tile) {
		Integer reqkey = loadingReqKeys.remove(tile);
		if (reqkey != null) {
			commonLogic.getDataSource().cancel(reqkey);
		}
	}

	@Override
	protected TileBitmapFile load(final Tile tile, Null from)
			throws IOException, InterruptedException {
		TileBitmapFile tileBitmapFile = new TileBitmapFile(tile,
				outputDirectory);
		if (tileBitmapFile.exists()) {
			return tileBitmapFile;
		}
		final CountDownLatch countDownLatch = new CountDownLatch(1);
		final AtomicReference<Bitmap> outputBitmap = new AtomicReference<Bitmap>(
				null);
		int reqkey = commonLogic.getDataSource().getMapTile(tile.getCenter(),
				tile.getZoom(), new WebAPICallback<Bitmap>() {
					@Override
					public void onException(int reqkey, WebAPIException ex) {
						commonLogic.getDataSource().cancel(reqkey);
						countDownLatch.countDown();
					}

					@Override
					public void onFailed(int reqkey, int statusCode,
							String response) {
						commonLogic.getDataSource().cancel(reqkey);
						countDownLatch.countDown();
					}

					@Override
					public void onSucceed(int reqkey, int statusCode,
							Bitmap bitmap) {
						outputBitmap.set(bitmap);
						countDownLatch.countDown();
					}
				});
		loadingReqKeys.put(tile, reqkey);
		countDownLatch.await();
		loadingReqKeys.remove(tile);
		Bitmap bitmap = outputBitmap.get();
		if (bitmap == null) {
			throw new IOException("bitmap == null");
		}
		tileBitmapFile.alignAndSaveBitmap(bitmap, true).recycle();
		return tileBitmapFile;
	}
}
