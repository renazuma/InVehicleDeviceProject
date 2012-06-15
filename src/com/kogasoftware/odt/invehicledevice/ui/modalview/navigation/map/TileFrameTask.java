package com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.map;

import android.graphics.Point;

import com.google.common.primitives.Floats;
import com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.FrameState;
import com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.FrameTask;
import com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.Texture;

class RemoveTileFrameTask extends FrameTask {
	private final TileKey tileKey;
	private final int textureId;
	private final float initialAlpha;
	private final long createdMillis;

	public RemoveTileFrameTask(TileKey tileKey, int textureId, float alpha,
			long createdMillis) {
		this.tileKey = tileKey;
		this.textureId = textureId;
		this.initialAlpha = alpha;
		this.createdMillis = createdMillis;
	}

	@Override
	public void onDraw(FrameState frameState) {
		float alpha = Floats.max(initialAlpha
				- (frameState.getMilliSeconds() - createdMillis) / 2000f, 0);
		alpha *= 0.8;
		Point point = tileKey.getCenterPixel();
		float scale = 1f;
		Texture.draw(frameState.getGL(), textureId, point.x, point.y,
				TileKey.TILE_LENGTH + 1, TileKey.TILE_LENGTH + 1, 0, scale,
				scale, alpha);
		if (alpha <= 0) {
			frameState.removeFrameTask(this);
		}
	}
}

public class TileFrameTask extends FrameTask {
	private final TileKey tileKey;
	private final int textureId;
	private final long createdMillis;
	private float alpha = 0;

	public TileFrameTask(TileKey tileKey, int textureId, long createdMillis) {
		this.tileKey = tileKey;
		this.textureId = textureId;
		this.createdMillis = createdMillis;
	}

	@Override
	public void onAdd(FrameState frameState) {
	}

	@Override
	public void onRemove(FrameState frameState) {
		frameState.addFrameTask(new RemoveTileFrameTask(tileKey, textureId,
				alpha, frameState.getMilliSeconds()));
	}

	@Override
	public void onDraw(FrameState frameState) {
		if (frameState.getZoom() != tileKey.getZoom()) {
			return;
		}
		alpha = Floats.min(
				(frameState.getMilliSeconds() - createdMillis) / 1000f, 1);
		Point point = tileKey.getCenterPixel();
		// float scale = (TileKey.TILE_LENGTH + 5) / TileKey.TILE_LENGTH;
		// float scale = 1.5f;
		float scale = 1f;
		Texture.draw(frameState.getGL(), textureId, point.x, point.y,
				TileKey.TILE_LENGTH + 1, TileKey.TILE_LENGTH + 1, 0, scale,
				scale, alpha);
	}
}
