package com.kogasoftware.viridian;

import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.maps.GeoPoint;
import com.google.common.io.Closeables;

public class GeoPointDroidSprite extends Sprite {
	private final GeoPoint geoPoint;

	public GeoPointDroidSprite(Context context, GeoPoint geoPoint) {
		super(context);
		this.geoPoint = geoPoint;

		InputStream inputStream = context.getResources().openRawResource(
				R.drawable.droid);
		Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
		Closeables.closeQuietly(inputStream);
		setBitmap(bitmap);
	}

	@Override
	public void onDraw(FrameState frameState) {
		draw(frameState, geoPoint);
	}
}
