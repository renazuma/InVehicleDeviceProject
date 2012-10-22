package com.kogasoftware.odt.invehicledevice.ui.fragment.navigation;

public class Utility {
	public static final Double MAX_NEAREST_RADIAN = Math.PI * 1000;

	public static Double getNearestRadian(Double base, Double next) {
		if (Math.abs(next) > MAX_NEAREST_RADIAN) {
			return (Math.abs(next) % (Math.PI * 2)) * (next >= 0 ? 1 : -1);
		}

		while (true) {
			Double diff = base - next;
			if (diff <= Math.PI) {
				break;
			}
			next += Math.PI * 2;
		}
		while (true) {
			Double diff = base - next;
			if (diff >= -Math.PI) {
				break;
			}
			next -= Math.PI * 2;
		}
		return next;
	}
	
	public static double locationToDouble(long loc) {
		return loc * 1.0e-6;
	}
	
	public static long locationToLong(double loc) {
		return (long)(loc * 1.0e6);		
	}
	
}
