package com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.map;

import java.io.IOException;

import android.graphics.Bitmap;

import com.google.common.base.Predicate;

public class FileTilePipe extends PipeExchanger<Tile, TileBitmapFile, Bitmap> {
	public FileTilePipe(PipeQueue<Tile, TileBitmapFile> fromPipeQueue,
			PipeQueue<Tile, Bitmap> toPipeQueue, Predicate<Tile> isValid) {
		super(fromPipeQueue, toPipeQueue, isValid);
	}

	@Override
	protected Bitmap load(Tile tile, TileBitmapFile from) throws IOException {
		return from.getBitmap();
	}

	@Override
	public void cancel(Tile key) {
	}
}
