package com.kogasoftware.odt.invehicledevice.ui.modalview.navigation;

import android.graphics.Point;

public class TileFrameTask extends FrameTask {
	private final TileKey tileKey;
	private final int textureId;

	public TileFrameTask(TileKey tileKey, int textureId) {
		this.tileKey = tileKey;
		this.textureId = textureId;
	}

	@Override
	public void onRemove(FrameState frameState) {
		Texture.delete(frameState.getGL(), textureId);
	}

	@Override
	void onDraw(FrameState frameState) {
		if (frameState.getZoom() != tileKey.getZoom()) {
			return;
		}
		Point point = tileKey.getCenterPixel();
		Texture.draw(frameState.getGL(), textureId, point.x, point.y,
				TileKey.TILE_LENGTH, TileKey.TILE_LENGTH, 0, 1f, 1f, 1f);
	}
}
