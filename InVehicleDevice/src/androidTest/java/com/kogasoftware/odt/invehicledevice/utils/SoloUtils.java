package com.kogasoftware.odt.invehicledevice.utils;

import com.robotium.solo.Solo;

public class SoloUtils {
	public static final Solo.Config LONGER_TIMEOUT = new Solo.Config();
	static {
		LONGER_TIMEOUT.timeout_large = 120 * 1000;
		LONGER_TIMEOUT.timeout_small = 60 * 1000;
	}
}
