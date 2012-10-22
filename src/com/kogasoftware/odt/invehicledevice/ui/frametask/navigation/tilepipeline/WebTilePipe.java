package com.kogasoftware.odt.invehicledevice.ui.frametask.navigation.tilepipeline;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.ObjectUtils.Null;

import android.graphics.Bitmap;
import android.util.Log;

import com.kogasoftware.odt.apiclient.ApiClientCallback;
import com.kogasoftware.odt.apiclient.ApiClientException;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.ui.frametask.navigation.tilepipeline.PipeQueue.OnDropListener;

public class WebTilePipe extends PipeExchanger<Tile, Null, TileBitmapFile> {
	private static final String TAG = WebTilePipe.class.getSimpleName();
	private final File outputDirectory;
	protected static final Integer NUM_LOADERS = 10;
	protected final Map<Tile, Integer> loadingReqKeys = new ConcurrentHashMap<Tile, Integer>();

	public WebTilePipe(InVehicleDeviceService service,
			PipeQueue<Tile, Null> fromPipeQueue,
			PipeQueue<Tile, TileBitmapFile> toPipeQueue,
			OnDropListener<Tile> onDropListener, File outputDirectory) {
		super(service, fromPipeQueue, toPipeQueue, onDropListener);
		this.outputDirectory = outputDirectory;
	}

	@Override
	public void clear() {
		for (Tile tile : new HashSet<Tile>(loadingReqKeys.keySet())) {
			Integer reqkey = loadingReqKeys.remove(tile);
			if (reqkey != null) {
				service.getApiClient().abort(reqkey);
			}
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
		int reqkey = service.getApiClient().getMapTile(tile.getCenter(),
				tile.getZoom(), new ApiClientCallback<Bitmap>() {
					@Override
					public void onException(int reqkey, ApiClientException ex) {
						service.getApiClient().abort(reqkey);
						Log.i(TAG, "onException reqkey=" + reqkey, ex);
						countDownLatch.countDown();
					}

					@Override
					public void onFailed(int reqkey, int statusCode,
							String response) {
						service.getApiClient().abort(reqkey);
						Log.i(TAG, "onFailed reqkey=" + reqkey + " statusCode="
								+ statusCode + " response=" + response);
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
