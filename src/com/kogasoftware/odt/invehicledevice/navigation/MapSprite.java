package com.kogasoftware.odt.invehicledevice.navigation;


public class MapSprite extends Sprite {
	@Override
	void onDraw(FrameState frameState) {
		draw(new DrawParams(frameState).angle(frameState.getMapAngle()).scale(
				frameState.getMapPixelZoom()));
	}
}
