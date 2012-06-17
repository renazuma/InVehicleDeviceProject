package com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.frametask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.FrameState;
import com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.Textures;
import com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.tilepipeline.TilePipeline;

public class MapBuildFrameTask extends FrameTask {
	private final TilePipeline tilePipeline;
	private final Bitmap defaultBitmap;

	public MapBuildFrameTask(Context context, TilePipeline tilePipeline) {
		this.tilePipeline = tilePipeline;
		defaultBitmap = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.default_texture);
	}

	@Override
	public void onAdd(FrameState frameState) {
		if (defaultBitmap.isRecycled()) {
			return;
		}
		frameState.removeFrameTask(this);
		int textureId = Textures.generate(frameState.getGL());
		Textures.update(frameState.getGL(), textureId, defaultBitmap);
		defaultBitmap.recycle();
		frameState.addFrameTask(new MapFrameTask(tilePipeline, textureId));
	}
}
