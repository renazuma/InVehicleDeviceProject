package com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.tilepipeline;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.microedition.khronos.opengles.GL10;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.ObjectUtils.Null;
import org.apache.commons.lang3.tuple.Pair;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.event.ExitEvent;
import com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.FrameState;
import com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.Texture;

public class TilePipeline {
	private static final Integer MAX_TEXTURE_TRANSFER_COUNT = 10;
	private final Set<Tile> processingTiles = new CopyOnWriteArraySet<Tile>();
	private final Predicate<Tile> isProcessing = Predicates.in(processingTiles);
	private final PipeQueue<Tile, Null> startPipeQueue = new PipeQueue<Tile, Null>(
			1000, isProcessing);
	private final PipeQueue<Tile, TileBitmapFile> filePipeQueue = new PipeQueue<Tile, TileBitmapFile>(
			1000, isProcessing);
	private final PipeQueue<Tile, Bitmap> bitmapPipeQueue = new BitmapPipeQueue<Tile>(
			32, isProcessing);
	private final PipeQueue<Tile, Integer> texturePipeQueue = new PipeQueue<Tile, Integer>(
			32);
	private final PipeExchanger<Tile, Null, TileBitmapFile> webTilePipe;
	private final PipeExchanger<Tile, TileBitmapFile, Bitmap> fileTilePipe;
	private CommonLogic commonLogic = new CommonLogic();

	public TilePipeline(Context context) throws IOException {
		File outputDirectory = context.getExternalFilesDir("tile");
		webTilePipe = new WebTilePipe(startPipeQueue, filePipeQueue,
				isProcessing, outputDirectory);
		fileTilePipe = new FileTilePipe(filePipeQueue, bitmapPipeQueue,
				isProcessing);
	}

	public void start(Tile tile) throws InterruptedException {
		if (processingTiles.add(tile)) {
			startPipeQueue.add(tile, ObjectUtils.NULL);
		}
	}

	public void remove(Tile tile) {
		processingTiles.remove(tile);
	}

	public void transferGL(GL10 gl, FrameState frameState) {
		// 読み込まれたBitmapをTextureに変換
		Integer i = 0;
		for (Pair<Tile, Bitmap> pair : bitmapPipeQueue.reserveIfPresent()
				.asSet()) {
			Bitmap bitmap = pair.getValue();
			Tile tile = pair.getKey();
			Integer textureId = Texture.generate(gl);
			Texture.update(gl, textureId, bitmap);
			bitmapPipeQueue.remove(pair);
			bitmap.recycle();
			texturePipeQueue.add(tile, textureId);

			i++;
			if (i > MAX_TEXTURE_TRANSFER_COUNT) {
				break;
			}
		}
	}

	public void changeZoomLevel(int zoomLevel) {
	}

	public Optional<TileFrameTask> pollOrStartLoad(Tile tile) {
		return Optional.absent();
	}

	public void setCommonLogic(CommonLogic commonLogic) {
		this.commonLogic.dispose();
		this.commonLogic = commonLogic;
		commonLogic.registerEventListener(this);
	}

	@Subscribe
	public void close(ExitEvent exitEvent) {
		webTilePipe.close();
		fileTilePipe.close();
	}
}
