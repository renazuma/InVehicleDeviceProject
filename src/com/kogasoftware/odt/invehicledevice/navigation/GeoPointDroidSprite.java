package com.kogasoftware.odt.invehicledevice.navigation;

import java.io.InputStream;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.maps.GeoPoint;
import com.google.common.io.Closeables;
import com.kogasoftware.odt.invehicledevice.R;

public class GeoPointDroidSprite extends Sprite {
	private final GeoPoint geoPoint;

	public GeoPointDroidSprite(Resources resources, GeoPoint geoPoint) {
		this.geoPoint = geoPoint;

		InputStream inputStream = resources.openRawResource(R.drawable.droid);
		Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
		Closeables.closeQuietly(inputStream);
		setBitmap(bitmap);
	}

	@Override
	public void onDraw(FrameState frameState) {
		draw(frameState, geoPoint);
	}
}
