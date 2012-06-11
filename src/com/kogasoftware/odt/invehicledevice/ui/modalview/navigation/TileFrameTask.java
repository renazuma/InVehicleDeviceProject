package com.kogasoftware.odt.invehicledevice.ui.modalview.navigation;

import android.graphics.PointF;

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
		PointF offset = tileKey.getOffsetPixels(frameState.getLatLng());
		Texture.draw(frameState.getGL(), textureId, offset.x, offset.y, 256, 256, frameState.getAngle(), 1f, 1f, 1f);
	}
}
