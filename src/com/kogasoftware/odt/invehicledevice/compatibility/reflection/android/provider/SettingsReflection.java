package com.kogasoftware.odt.invehicledevice.compatibility.reflection.android.provider;

import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.compatibility.reflection.Reflections;

public class SettingsReflection {
	public static class SystemReflection {
		/**
		 * "http://developer.android.com/reference/android/provider/Settings.System.html#USER_ROTATION"
		 */
		public static final Optional<String> USER_ROTATION = Reflections
				.readStaticField(android.provider.Settings.System.class,
						"USER_ROTATION", String.class);
	}
}
