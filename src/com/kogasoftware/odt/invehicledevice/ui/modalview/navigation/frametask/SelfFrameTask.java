package com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.frametask;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.FrameState;
import com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.NavigationRenderer;
import com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.Textures;

public class SelfFrameTask extends FrameTask {
	private final Bitmap bitmap; // TODO:recycleされるかもしれない
	private int textureId = -1; // TODO:Optionalを検討
	private final int width;
	private final int height;

	public SelfFrameTask(Resources resources) {
		bitmap = BitmapFactory.decodeResource(resources, R.drawable.self);
		width = bitmap.getWidth();
		height = bitmap.getHeight();
	}

	@Override
	public void onAdd(FrameState frameState) {
		textureId = Textures.generate(frameState.getGL());
		if (!bitmap.isRecycled()) {
			Textures.update(frameState.getGL(), textureId, bitmap);
		}
		bitmap.recycle();
	}

	@Override
	public void onRemove(FrameState frameState) {
		if (!bitmap.isRecycled()) {
			bitmap.recycle();
		}
	}

	@Override
	public void onDraw(FrameState frameState) {
		PointF point = NavigationRenderer.getPoint(frameState.getLatLng());
		float scale = 1f / frameState.getTotalZoom();
		float alpha = 0.4f;
		Textures.draw(frameState.getGL(), textureId, point.x, point.y, width,
				height, -frameState.getAngle(), scale, scale, alpha);
	}
}