package com.kogasoftware.odt.invehicledevice.ui.modalview.navigation;

import android.graphics.Bitmap;
import android.graphics.Point;

import com.google.common.primitives.Floats;

class RemoveTileFrameTask extends FrameTask {
	private final TileKey tileKey;
	private final int textureId;
	private final float initialAlpha;
	private long addedMillis = 0;

	public RemoveTileFrameTask(TileKey tileKey, int textureId, float alpha) {
		this.tileKey = tileKey;
		this.textureId = textureId;
		this.initialAlpha = alpha;
	}

	@Override
	public void onAdd(FrameState frameState) {
		addedMillis = frameState.getMilliSeconds();
	}

	@Override
	public void onRemove(FrameState frameState) {
		if (textureId != -1) {
			Texture.delete(frameState.getGL(), textureId);
		}
	}

	@Override
	void onDraw(FrameState frameState) {
		float alpha = Floats.max(initialAlpha
				- (frameState.getMilliSeconds() - addedMillis) / 2000f, 0);
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
	private final Bitmap bitmap; // TODO:recycleされるかもしれない
	private int textureId = -1; // TODO:Optionalを検討
	private long addedMillis = 0;
	private float alpha = 0;

	public TileFrameTask(TileKey tileKey, Bitmap bitmap) {
		this.tileKey = tileKey;
		this.bitmap = bitmap;
	}

	@Override
	public void onAdd(FrameState frameState) {
		textureId = Texture.generate(frameState.getGL());
		if (!bitmap.isRecycled()) {
			Texture.update(frameState.getGL(), textureId, bitmap);
		}
		bitmap.recycle();
		addedMillis = frameState.getMilliSeconds();
	}

	@Override
	public void onRemove(FrameState frameState) {
		if (textureId != -1) {
			frameState.addFrameTask(new RemoveTileFrameTask(tileKey, textureId,
					alpha));
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
		alpha = Floats.min(
				(frameState.getMilliSeconds() - addedMillis) / 1000f, 1);
		Point point = tileKey.getCenterPixel();
		// float scale = (TileKey.TILE_LENGTH + 5) / TileKey.TILE_LENGTH;
		// float scale = 1.5f;
		float scale = 1f;
		Texture.draw(frameState.getGL(), textureId, point.x, point.y,
				TileKey.TILE_LENGTH + 1, TileKey.TILE_LENGTH + 1, 0, scale,
				scale, alpha);
	}
}
