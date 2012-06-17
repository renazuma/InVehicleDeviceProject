package com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.tilepipeline;

import java.io.File;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.microedition.khronos.opengles.GL10;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.ObjectUtils.Null;
import org.apache.commons.lang3.tuple.Pair;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.event.ExitEvent;
import com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.Textures;
import com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.tilepipeline.PipeQueue.OnDropListener;

public class TilePipeline {
	private static final String TAG = TilePipeline.class.getSimpleName();
	private static final Integer MAX_TEXTURE_TRANSFER_COUNT = 10;
	private final Set<Tile> processingTiles = new CopyOnWriteArraySet<Tile>();
	private final OnDropListener<Tile> onDropListener = new OnDropListener<Tile>() {
		@Override
		public void onDrop(Tile key) {
			processingTiles.remove(key);
			// Log.v(TAG, "onDrop " + key);
		}
	};
	private volatile int zoomLevel = 0;
	private volatile int nextZoomLevel = 0;
	private final Comparator<Tile> comparator = new Comparator<Tile>() {
		@Override
		public int compare(Tile lhs, Tile rhs) {
			if (lhs.equals(rhs)) {
				return 0;
			}
			int random = lhs.toString().compareTo(rhs.toString());
			if (lhs.getZoom() == rhs.getZoom()) {
				return random;
			}
			if (rhs.getZoom() == zoomLevel) {
				return -1;
			} else if (lhs.getZoom() == zoomLevel) {
				return 1;
			}
			if (rhs.getZoom() == nextZoomLevel) {
				return -1;
			} else if (lhs.getZoom() == nextZoomLevel) {
				return 1;
			}
			return random;
		}
	};
	private final PipeQueue<Tile, Null> startPipeQueue = new PipeQueue<Tile, Null>(
			100, onDropListener, comparator);
	private final PipeQueue<Tile, TileBitmapFile> filePipeQueue = new PipeQueue<Tile, TileBitmapFile>(
			100, onDropListener, comparator);
	private final PipeQueue<Tile, Bitmap> bitmapPipeQueue = new BitmapPipeQueue<Tile>(
			16, onDropListener, comparator);
	private final PipeQueue<Tile, Integer> texturePipeQueue = new PipeQueue<Tile, Integer>(
			25, onDropListener, comparator);
	private final PipeExchanger<Tile, Null, TileBitmapFile> webTilePipe;
	private final PipeExchanger<Tile, TileBitmapFile, Bitmap> fileTilePipe;
	private CommonLogic commonLogic = new CommonLogic();

	public TilePipeline(Context context) {
		File outputDirectory = context.getExternalFilesDir("tile");
		webTilePipe = new WebTilePipe(startPipeQueue, filePipeQueue,
				onDropListener, outputDirectory);
		fileTilePipe = new FileTilePipe(filePipeQueue, bitmapPipeQueue,
				onDropListener);
	}

	public void start(Tile tile) {
		if (processingTiles.add(tile)) {
			// Log.v(TAG, "add " + tile);
			startPipeQueue.addAndDrop(tile, ObjectUtils.NULL);
		}
	}

	public void remove(GL10 gl, Tile tile) {
		onDropListener.onDrop(tile);
		processingTiles.remove(tile);
		for (Integer textureId : texturePipeQueue.remove(tile).asSet()) {
			Textures.delete(gl, textureId);
		}
	}

	public void transferGL(GL10 gl) {
		// 読み込まれたBitmapをTextureに変換
		Integer i = 0;
		for (Pair<Tile, Bitmap> pair : bitmapPipeQueue.poll().asSet()) {
			Bitmap bitmap = pair.getValue();
			Tile tile = pair.getKey();
			if (bitmap.isRecycled()) {
				Log.e(TAG, "bitmap is recycled. tile=" + tile);
				onDropListener.onDrop(tile);
				continue;
			}
			Integer textureId = Textures.generate(gl);
			Textures.update(gl, textureId, bitmap);
			bitmap.recycle();
			for (Pair<Tile, Integer> droped : texturePipeQueue.add(tile,
					textureId).asSet()) {
				onDropListener.onDrop(droped.getKey());
				Textures.delete(gl, droped.getValue());
			}

			i++;
			if (i > MAX_TEXTURE_TRANSFER_COUNT) {
				break;
			}
		}
	}

	public void changeZoomLevel(int zoomLevel) {
		this.zoomLevel = zoomLevel;
		startPipeQueue.clear();
		webTilePipe.clear();
		filePipeQueue.clear();
		fileTilePipe.clear();
		bitmapPipeQueue.clear();
		texturePipeQueue.clear();
		processingTiles.clear();
	}

	public Optional<Integer> pollOrStartLoad(Tile tile) {
		for (Integer textureId : texturePipeQueue.getIfPresent(tile).asSet()) {
			return Optional.of(textureId);
		}
		start(tile);
		return Optional.absent();
	}

	public void setCommonLogic(CommonLogic commonLogic) {
		this.commonLogic.dispose();
		this.commonLogic = commonLogic;
		webTilePipe.setCommonLogic(commonLogic);
		fileTilePipe.setCommonLogic(commonLogic);
		commonLogic.registerEventListener(this);
	}

	@Subscribe
	public void close(ExitEvent exitEvent) {
		webTilePipe.close();
		fileTilePipe.close();
	}

	public Set<Tile> getPresentTiles() {
		return new HashSet<Tile>(texturePipeQueue.getKeys());
	}
}
