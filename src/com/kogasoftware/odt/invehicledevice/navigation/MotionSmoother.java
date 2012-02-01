package com.kogasoftware.odt.invehicledevice.navigation;

abstract public class MotionSmoother {

	public void addMotion(Double motion) {
		addMotion(motion, System.currentTimeMillis());
	}

	public void addMotion(Double motion, Long millis) {
		synchronized (this) {
			calculateAndAddMotion(motion, millis);
		}
	}

	abstract protected void calculateAndAddMotion(Double motion, Long millis);

	public Double getSmoothMotion() {
		return getSmoothMotion(System.currentTimeMillis());
	}

	public Double getSmoothMotion(Long millis) {
		synchronized (this) {
			return calculateAndGetSmoothMotion(millis);
		}
	}

	abstract protected Double calculateAndGetSmoothMotion(Long millis);
}
