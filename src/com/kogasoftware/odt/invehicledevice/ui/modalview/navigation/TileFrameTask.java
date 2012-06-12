package com.kogasoftware.odt.invehicledevice.ui.modalview.navigation;

import android.graphics.Bitmap;
import android.graphics.Point;

public class TileFrameTask extends FrameTask {
	private final TileKey tileKey;
	private final Bitmap bitmap; // TODO:recycleされるかもしれない
	private int textureId = -1; // TODO:Optionalを検討

	public TileFrameTask(TileKey tileKey, Bitmap bitmap) {
		this.tileKey = tileKey;
		this.bitmap = bitmap;
	}

	@Override
	public void onAdd(FrameState frameState) {
		textureId = Texture.generate(frameState.getGL());
		Texture.update(frameState.getGL(), textureId, bitmap);
		bitmap.recycle();
	}

	@Override
	public void onRemove(FrameState frameState) {
		if (textureId != -1) {
			Texture.delete(frameState.getGL(), textureId);
		}
		if (!bitmap.isRecycled()) {
			bitmap.recycle();
		}
	}

	@Override
	void onDraw(FrameState frameState) {
		if (frameState.getZoom() != tileKey.getZoom()) {
			return;
		}
		Point point = tileKey.getCenterPixel();
		// float scale = (TileKey.TILE_LENGTH + 5) / TileKey.TILE_LENGTH;
		// float scale = 1.5f;
		float scale = 1f;
		Texture.draw(frameState.getGL(), textureId, point.x, point.y,
				TileKey.TILE_LENGTH + 1, TileKey.TILE_LENGTH + 1, 0, scale,
				scale, 1f);
	}
}
