package com.kogasoftware.odt.invehicledevice.ui.fragment.navigation;

public abstract class MotionSmoother {

	public void addMotion(double motion) {
		addMotion(motion, System.currentTimeMillis());
	}

	public void addMotion(double motion, long millis) {
		synchronized (this) {
			calculateAndAddMotion(motion, millis);
		}
	}

	protected abstract void calculateAndAddMotion(double motion, long millis);

	public double getSmoothMotion() {
		return getSmoothMotion(System.currentTimeMillis());
	}

	public double getSmoothMotion(long millis) {
		synchronized (this) {
			return calculateAndGetSmoothMotion(millis);
		}
	}

	protected abstract double calculateAndGetSmoothMotion(long millis);
}
