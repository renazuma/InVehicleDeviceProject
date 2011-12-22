package com.kogasoftware.viridian;

import android.content.Context;

public class MapSprite extends Sprite {
	public MapSprite(Context context) {
		super(context);
	}

	@Override
	void onDraw(FrameState frameState) {
		draw(new DrawParams(frameState).angle(frameState.getMapAngle()).scale(
				frameState.getMapPixelZoom()));
	}
}
