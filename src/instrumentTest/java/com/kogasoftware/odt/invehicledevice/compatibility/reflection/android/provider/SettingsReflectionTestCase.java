package com.kogasoftware.odt.invehicledevice.compatibility.reflection.android.provider;

import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.compatibility.reflection.android.provider.SettingsReflection.SystemReflection;

import android.os.Build;
import android.test.AndroidTestCase;

public class SettingsReflectionTestCase extends AndroidTestCase {
	public void testSystemUserRotation() {
		if (Build.VERSION.SDK_INT >= 11) {
			assertEquals(Optional.of("user_rotation"),
					SystemReflection.USER_ROTATION);
		} else {
			assertFalse(SystemReflection.USER_ROTATION.isPresent());
		}
	}
}
