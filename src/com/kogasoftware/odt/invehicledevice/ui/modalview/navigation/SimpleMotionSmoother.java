package com.kogasoftware.odt.invehicledevice.ui.modalview.navigation;


public class SimpleMotionSmoother extends MotionSmoother {
	private Double orientation = 0.0;

	@Override
	protected void calculateAndAddMotion(Double orientation, Long millis) {
		this.orientation = orientation;
	}

	@Override
	protected Double calculateAndGetSmoothMotion(Long millis) {
		return orientation;
	}
}
