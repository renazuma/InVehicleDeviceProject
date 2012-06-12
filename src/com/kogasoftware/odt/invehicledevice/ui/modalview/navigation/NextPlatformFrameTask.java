package com.kogasoftware.odt.invehicledevice.ui.modalview.navigation;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;

import com.javadocmd.simplelatlng.LatLng;
import com.kogasoftware.odt.invehicledevice.R;

@FrameTask.Front
public class NextPlatformFrameTask extends FrameTask {
	private final Bitmap bitmap; // TODO:recycleされるかもしれない
	private int textureId = -1; // TODO:Optionalを検討
	private final int width;
	private final int height;
	private volatile LatLng latLng = new LatLng(0, 0);

	public NextPlatformFrameTask(Resources resources) {
		bitmap = BitmapFactory.decodeResource(resources,
				R.drawable.next_platform);
		width = bitmap.getWidth();
		height = bitmap.getHeight();
	}

	public void setLatLng(LatLng latLng) {
		this.latLng = latLng;
	}

	@Override
	public void onAdd(FrameState frameState) {
		textureId = Texture.generate(frameState.getGL());
		if (!bitmap.isRecycled()) {
			Texture.update(frameState.getGL(), textureId, bitmap);
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
	void onDraw(FrameState frameState) {
		PointF point = NavigationRenderer
				.getPoint(latLng, frameState.getZoom());
		float scale = 0.4f;
		float alpha = 0.8f;
		Texture.draw(frameState.getGL(), textureId, point.x, point.y, width,
				height, -frameState.getAngle(), scale, scale, alpha);
	}
}
