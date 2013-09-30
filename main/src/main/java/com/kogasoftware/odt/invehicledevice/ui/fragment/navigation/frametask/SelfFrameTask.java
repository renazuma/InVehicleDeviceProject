package com.kogasoftware.odt.invehicledevice.ui.fragment.navigation.frametask;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;

import com.javadocmd.simplelatlng.LatLng;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.ui.fragment.navigation.FrameState;
import com.kogasoftware.odt.invehicledevice.ui.fragment.navigation.NavigationRenderer;
import com.kogasoftware.odt.invehicledevice.ui.fragment.navigation.Textures;

public class SelfFrameTask extends FrameTask {
	private final Bitmap bitmap; // TODO:recycleされるかもしれない
	private int textureId = -1; // TODO:Optionalを検討
	private final int width;
	private final int height;
	private final LatLng latLng = new LatLng(0, 0);

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

	public void setLatLng(LatLng latLng) {
		this.latLng.setLatitudeLongitude(latLng.getLatitude(),
				latLng.getLongitude());
	}

	@Override
	public void draw(FrameState frameState) {
		PointF point = NavigationRenderer.getPoint(latLng);
		float scale = 1f / frameState.getTotalZoom();
		float alpha = 0.4f;
		Textures.draw(frameState.getGL(), textureId, point.x, point.y, width,
				height, -frameState.getAngle(), scale, scale, alpha);
	}
}