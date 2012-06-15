package com.kogasoftware.odt.invehicledevice.ui.modalview;

import java.io.File;
import java.io.IOException;

import android.graphics.Bitmap;

public class FileTilePipe extends TilePipe<File, Bitmap> {
	public FileTilePipe(PipeQueue<TilePair<File>> fromPipeQueue,
			PipeQueue<TilePair<Bitmap>> toPipeQueue) {
		super(fromPipeQueue, toPipeQueue);
	}

	@Override
	protected TilePair<Bitmap> load(TilePair<File> from) throws IOException {
		return null;
	}
}
