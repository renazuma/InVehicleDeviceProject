package com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.tilepipeline;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
import com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.Textures;

public class TilePipeline {
	private static final Integer MAX_TEXTURE_TRANSFER_COUNT = 10;

	class OnRemoveTileListener {
		public void onRemoveTile(Tile tile) {
			processingTiles.remove(tile);
		}
	}

	private final Set<Tile> processingTiles = new CopyOnWriteArraySet<Tile>();
	private final Predicate<Tile> isProcessing = Predicates.in(processingTiles);
	private final PipeQueue<Tile, Null> startPipeQueue = new PipeQueue<Tile, Null>(
			100, isProcessing);
	private final PipeQueue<Tile, TileBitmapFile> filePipeQueue = new PipeQueue<Tile, TileBitmapFile>(
			100, isProcessing);
	private final PipeQueue<Tile, Bitmap> bitmapPipeQueue = new BitmapPipeQueue<Tile>(
			16, isProcessing);
	private final Map<Tile, Integer> textures = new HashMap<Tile, Integer>();
	// private final Map<Tile, Integer> textures = ImmutableMap.of();
	private final PipeExchanger<Tile, Null, TileBitmapFile> webTilePipe;
	private final PipeExchanger<Tile, TileBitmapFile, Bitmap> fileTilePipe;
	private CommonLogic commonLogic = new CommonLogic();

	public TilePipeline(Context context) {
		File outputDirectory = context.getExternalFilesDir("tile");
		webTilePipe = new WebTilePipe(startPipeQueue, filePipeQueue,
				isProcessing, outputDirectory);
		fileTilePipe = new FileTilePipe(filePipeQueue, bitmapPipeQueue,
				isProcessing);
	}

	public void start(Tile tile) {
		if (processingTiles.add(tile)) {
			startPipeQueue.add(tile, ObjectUtils.NULL);
		}
	}

	public void remove(GL10 gl, Tile tile) {
		processingTiles.remove(tile);
		Integer textureId = textures.remove(tile);
		if (textureId != null) {
			Textures.delete(gl, textureId);
		}
	}

	public void transferGL(GL10 gl) {
		// 読み込まれたBitmapをTextureに変換
		Integer i = 0;
		for (Pair<Tile, Bitmap> pair : bitmapPipeQueue.poll().asSet()) {
			Bitmap bitmap = pair.getValue();
			if (textures.size() > 10) {
				bitmap.recycle();
				break;
			}
			Tile tile = pair.getKey();
			Integer textureId = Textures.generate(gl);
			Textures.update(gl, textureId, bitmap);
			bitmap.recycle();
			textures.put(tile, textureId);

			i++;
			if (i > MAX_TEXTURE_TRANSFER_COUNT) {
				break;
			}
		}
	}

	public void changeZoomLevel(int zoomLevel) {
	}

	public Optional<Integer> pollOrStartLoad(Tile tile) {
		Integer textureId = textures.get(tile);
		if (textureId != null) {
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
		return new HashSet<Tile>(textures.keySet());
	}
}
