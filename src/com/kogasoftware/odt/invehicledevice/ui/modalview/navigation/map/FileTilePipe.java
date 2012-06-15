package com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.map;

import java.io.File;
import java.io.IOException;

import android.graphics.Bitmap;

public class FileTilePipe extends
		PipeExchanger<TilePair<File>, TilePair<Bitmap>> {
	public FileTilePipe(PipeQueue<TilePair<File>> fromPipeQueue,
			PipeQueue<TilePair<Bitmap>> toPipeQueue) {
		super(fromPipeQueue, toPipeQueue);
	}

	@Override
	protected TilePair<Bitmap> load(TilePair<File> from) throws IOException {
		return null;
	}
}
