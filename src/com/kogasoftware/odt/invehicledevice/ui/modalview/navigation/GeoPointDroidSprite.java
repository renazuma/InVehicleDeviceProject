package com.kogasoftware.odt.invehicledevice.ui.modalview.navigation;

import java.io.InputStream;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.common.io.Closeables;
import com.javadocmd.simplelatlng.LatLng;
import com.kogasoftware.odt.invehicledevice.R;

public class GeoPointDroidSprite extends Sprite {
	private final LatLng latLng;

	public GeoPointDroidSprite(Resources resources, LatLng latLng) {
		this.latLng = latLng;

		InputStream inputStream = resources.openRawResource(R.drawable.droid);
		Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
		Closeables.closeQuietly(inputStream);
		setBitmap(bitmap);
	}

	@Override
	public void onDraw(FrameState frameState) {
		draw(frameState, latLng);
	}
}
