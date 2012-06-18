package com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.frametask;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;

import com.javadocmd.simplelatlng.LatLng;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.FrameState;
import com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.NavigationRenderer;
import com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.Textures;

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

	public LatLng getLatLng() {
		return latLng;
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
		// 緯度0経度0は海なので、未初期化と判断して良い
		if (latLng.getLatitude() == 0 && latLng.getLongitude() == 0) {
			return;
		}

		PointF point = NavigationRenderer.getPoint(latLng);
		float scale = 1.5f / frameState.getTotalZoom();
		float alpha = 0.8f;
		Textures.draw(frameState.getGL(), textureId, point.x, point.y, width,
				height, -frameState.getAngle(), scale, scale, alpha);
	}
}
