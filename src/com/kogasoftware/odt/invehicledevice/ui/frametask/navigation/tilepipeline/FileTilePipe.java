package com.kogasoftware.odt.invehicledevice.ui.frametask.navigation.tilepipeline;

import java.io.IOException;

import android.graphics.Bitmap;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.ui.frametask.navigation.tilepipeline.PipeQueue.OnDropListener;

public class FileTilePipe extends PipeExchanger<Tile, TileBitmapFile, Bitmap> {
	public FileTilePipe(InVehicleDeviceService service,
			PipeQueue<Tile, TileBitmapFile> fromPipeQueue,
			PipeQueue<Tile, Bitmap> toPipeQueue,
			OnDropListener<Tile> onDropListener) {
		super(service, fromPipeQueue, toPipeQueue, onDropListener);
	}

	@Override
	public void clear() {
	}

	@Override
	protected Bitmap load(Tile tile, TileBitmapFile from) throws IOException {
		return from.getBitmap();
	}
}
