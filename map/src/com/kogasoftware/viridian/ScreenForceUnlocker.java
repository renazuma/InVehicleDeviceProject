package com.kogasoftware.viridian;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

public class ScreenForceUnlocker {
	private Boolean started = false;
	private final KeyguardLock keyguardLock;
	private final PowerManager powerManager;
	private final WakeLock wakeLock;
	private final KeyguardManager keyguardManager;

	public ScreenForceUnlocker(Context context) {
		powerManager = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK
				| PowerManager.ACQUIRE_CAUSES_WAKEUP
				| PowerManager.ON_AFTER_RELEASE, "SimpleTimer");
		keyguardManager = (KeyguardManager) context
				.getSystemService(Context.KEYGUARD_SERVICE);
		keyguardLock = keyguardManager.newKeyguardLock("KeyguardLock");
	}

	public void start() {
		// acquire wake lock
		wakeLock.acquire();

		// unlock screen
		if (keyguardManager.inKeyguardRestrictedInputMode()) {
			keyguardLock.disableKeyguard();
			started = true;
		} else {
			started = false;
		}
	}

	public void stop() {
		// release screen
		if (started) {
			keyguardLock.reenableKeyguard();
			started = false;
		}
		// release wake lock
		if (wakeLock.isHeld()) {
			wakeLock.release();
		}
	}
}
