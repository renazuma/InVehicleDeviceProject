package com.kogasoftware.odt.invehicledevice;

import java.io.InputStream;
import java.util.List;

import org.apache.log4j.Logger;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.maps.GeoPoint;
import com.google.common.io.Closeables;
import com.kogasoftware.odt.invehicledevice.R;

public class HazardPointSprite extends Sprite {
	private static final Logger logger = Logger
			.getLogger(HazardPointSprite.class);

	private Hazard hazard = new Hazard();

	public HazardPointSprite(Context context) {
		super(context);
		InputStream inputStream = context.getResources().openRawResource(
				R.drawable.hazard);
		Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
		Closeables.closeQuietly(inputStream);
		setBitmap(bitmap);
	}

	@Override
	void onDraw(FrameState frameState) {
		List<TimeLocation> list = hazard.getTimeLocations();
		if (list.isEmpty()) {
			return;
		}
		TimeLocation timeLocation = list.get(0);
		GeoPoint geoPoint = new GeoPoint(
				(int) (timeLocation.getLatitude() * 1E6),
				(int) (timeLocation.getLongitude() * 1E6));

		Double alpha = (Math.abs(Math.sin(frameState.getMilliSeconds() / 400d)) / 2 + 0.5);
		draw(new DrawParams(frameState).geoPoint(geoPoint).alpha(alpha)
				.scale(0.5));
		// logger.error(String.format("alpha=%.3f", alpha));
	}

	public void setHazard(Hazard hazard) {
		this.hazard = hazard;
	}
}
