package com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.map;

import android.graphics.Point;

import com.google.common.primitives.Floats;
import com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.FrameState;
import com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.Texture;
import com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.frametask.FrameTask;

class RemoveTileFrameTask extends FrameTask {
	private final Tile tile;
	private final int textureId;
	private final float initialAlpha;
	private final long createdMillis;

	public RemoveTileFrameTask(Tile tile, int textureId, float alpha,
			long createdMillis) {
		this.tile = tile;
		this.textureId = textureId;
		this.initialAlpha = alpha;
		this.createdMillis = createdMillis;
	}

	@Override
	public void onDraw(FrameState frameState) {
		float alpha = Floats.max(initialAlpha
				- (frameState.getMilliSeconds() - createdMillis) / 2000f, 0);
		alpha *= 0.8;
		Point point = tile.getCenterPixel();
		float scale = 1f;
		Texture.draw(frameState.getGL(), textureId, point.x, point.y,
				Tile.TILE_LENGTH + 1, Tile.TILE_LENGTH + 1, 0, scale,
				scale, alpha);
		if (alpha <= 0) {
			frameState.removeFrameTask(this);
		}
	}
}

public class TileFrameTask extends FrameTask {
	private final Tile tile;
	private final int textureId;
	private final long createdMillis;
	private float alpha = 0;

	public TileFrameTask(Tile tile, int textureId, long createdMillis) {
		this.tile = tile;
		this.textureId = textureId;
		this.createdMillis = createdMillis;
	}

	@Override
	public void onAdd(FrameState frameState) {
	}

	@Override
	public void onRemove(FrameState frameState) {
		frameState.addFrameTask(new RemoveTileFrameTask(tile, textureId,
				alpha, frameState.getMilliSeconds()));
	}

	@Override
	public void onDraw(FrameState frameState) {
		if (frameState.getZoom() != tile.getZoom()) {
			return;
		}
		alpha = Floats.min(
				(frameState.getMilliSeconds() - createdMillis) / 1000f, 1);
		Point point = tile.getCenterPixel();
		// float scale = (Tile.TILE_LENGTH + 5) / Tile.TILE_LENGTH;
		// float scale = 1.5f;
		float scale = 1f;
		Texture.draw(frameState.getGL(), textureId, point.x, point.y,
				Tile.TILE_LENGTH + 1, Tile.TILE_LENGTH + 1, 0, scale,
				scale, alpha);
	}
}
