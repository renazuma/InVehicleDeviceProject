package com.kogasoftware.odt.invehicledevice.ui.modalview;

import java.io.File;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;

import com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.FrameState;
import com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.Texture;
import com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.TileKey;

public class TilePipeline {
	private static final Integer MAX_TEXTURE_TRANSFER_COUNT = 10;
	private final PipeQueue<TilePair<Void>> startPipeQueue = new PipeQueue<TilePair<Void>>();
	private final PipeQueue<TilePair<File>> filePipeQueue = new PipeQueue<TilePair<File>>();
	private final PipeQueue<TilePair<Bitmap>> bitmapPipeQueue = new PipeQueue<TilePair<Bitmap>>();
	private final PipeQueue<TilePair<Integer>> texturePipeQueue = new PipeQueue<TilePair<Integer>>();
	private final WebTilePipe webTilePipe;
	private final FileTilePipe fileTilePipe;
	private final Set<TileKey> processingTileKeys = new CopyOnWriteArraySet<TileKey>();

	public TilePipeline() {
		webTilePipe = new WebTilePipe(startPipeQueue, filePipeQueue);
		fileTilePipe = new FileTilePipe(filePipeQueue, bitmapPipeQueue);
	}
	
	public void start(TileKey tileKey) {
		if (processingTileKeys.add(tileKey)) {
			startPipeQueue.add(new TilePair<Void>(tileKey, null));
		}
	}

	public void transferGL(GL10 gl, FrameState frameState) {
		// 読み込まれたBitmapをTextureに変換
		Integer i = 0;
		for (TilePair<Bitmap> pair : bitmapPipeQueue.get().asSet()) {
			Bitmap bitmap = pair.getValue();
			TileKey tileKey = pair.getTileKey();
			Integer textureId = Texture.generate(gl);
			Texture.update(gl, textureId, bitmap);
			bitmapPipeQueue.remove(pair);
			bitmap.recycle();
			texturePipeQueue.add(new TilePair<Integer>(tileKey, textureId));

			i++;
			if (i > MAX_TEXTURE_TRANSFER_COUNT) {
				break;
			}
		}
	}
}
