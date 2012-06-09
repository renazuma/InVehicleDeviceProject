package com.kogasoftware.odt.invehicledevice.ui.modalview.navigation;

public class SimpleMotionSmoother extends MotionSmoother {
	private double orientation = 0.0;

	@Override
	protected void calculateAndAddMotion(double orientation, long millis) {
		this.orientation = orientation;
	}

	@Override
	protected double calculateAndGetSmoothMotion(long millis) {
		return orientation;
	}
}
