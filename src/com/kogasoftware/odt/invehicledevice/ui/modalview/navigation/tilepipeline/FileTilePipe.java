package com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.tilepipeline;

import java.io.IOException;

import android.graphics.Bitmap;

import com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.tilepipeline.PipeQueue.OnDropListener;

public class FileTilePipe extends PipeExchanger<Tile, TileBitmapFile, Bitmap> {
	public FileTilePipe(PipeQueue<Tile, TileBitmapFile> fromPipeQueue,
			PipeQueue<Tile, Bitmap> toPipeQueue,
			OnDropListener<Tile> onDropListener) {
		super(fromPipeQueue, toPipeQueue, onDropListener);
	}

	@Override
	protected Bitmap load(Tile tile, TileBitmapFile from) throws IOException {
		return from.getBitmap();
	}

	@Override
	public void clear() {
	}
}
