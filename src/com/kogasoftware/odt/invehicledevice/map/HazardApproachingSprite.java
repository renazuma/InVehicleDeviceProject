package com.kogasoftware.odt.invehicledevice.map;

import java.io.InputStream;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;

import com.google.android.maps.GeoPoint;
import com.google.common.io.Closeables;
import com.kogasoftware.odt.invehicledevice.R;

public class HazardApproachingSprite extends Sprite {
	enum Direction {
		NONE(0, ""), FORWARD(R.drawable.hazard_from_forward, "前方"), BACK(
				R.drawable.hazard_from_back, "後方"), RIGHT(
				R.drawable.hazard_from_right, "右"), LEFT(
				R.drawable.hazard_from_left, "左");

		private final String message;
		private final Integer resourceId;

		Direction(Integer resourceId, String message) {
			this.message = message;
			this.resourceId = resourceId;
		}

		public String getMessage() {
			return message;
		}

		public Integer getResourceId() {
			return resourceId;
		}
	}

	private Hazard hazard = new Hazard();
	private Direction direction = Direction.NONE;

	public HazardApproachingSprite(Context context) {
		super(context);
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

		Direction newDirection = Direction.BACK;
		// 方向を計算
		PointF pointF = frameState.convertGeoPointToPointF(geoPoint);
		if (Math.abs(pointF.x) > Math.abs(pointF.y)) {
			if (pointF.x > 0) {
				newDirection = Direction.RIGHT;
			} else {
				newDirection = Direction.LEFT;
			}
		} else {
			if (pointF.y >= 0) {
				newDirection = Direction.FORWARD;
			} else {
				newDirection = Direction.BACK;
			}
		}

		if (direction != newDirection) {
			InputStream inputStream = context.getResources().openRawResource(
					newDirection.getResourceId());
			Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
			Bitmap mutableBitmap = bitmap.copy(bitmap.getConfig(), true);
			Closeables.closeQuietly(inputStream);

			setBitmap(mutableBitmap);
			direction = newDirection;
		}

		Double alpha = Math.abs(Math.sin(frameState.getMilliSeconds() / 2000d)) / 2 + 0.5;
		alpha = 0.8;

		DrawParams drawParams = new DrawParams(frameState).scale(0.5).alpha(
				alpha);
		switch (direction) {
		case FORWARD:
			drawParams.y(-150d);
			break;
		case BACK:
			drawParams.y(150d);
			break;
		case LEFT:
			drawParams.x(180d);
			break;
		case RIGHT:
			drawParams.x(-180d);
			break;
		}
		draw(drawParams);
	}

	void setHazard(Hazard hazard) {
		this.hazard = hazard;
	}
}
