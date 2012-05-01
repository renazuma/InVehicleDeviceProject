package com.kogasoftware.odt.invehicledevice.navigation;

public abstract class MotionSmoother {

	public void addMotion(Double motion) {
		addMotion(motion, System.currentTimeMillis());
	}

	public void addMotion(Double motion, Long millis) {
		synchronized (this) {
			calculateAndAddMotion(motion, millis);
		}
	}

	protected abstract void calculateAndAddMotion(Double motion, Long millis);

	public Double getSmoothMotion() {
		return getSmoothMotion(System.currentTimeMillis());
	}

	public Double getSmoothMotion(Long millis) {
		synchronized (this) {
			return calculateAndGetSmoothMotion(millis);
		}
	}

	protected abstract Double calculateAndGetSmoothMotion(Long millis);
}
