package com.kogasoftware.odt.invehicledevice.test.unit.compatibility.reflection;

import android.test.AndroidTestCase;

import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.compatibility.reflection.Reflections;

public class ReflectionsTestCase extends AndroidTestCase {
	public static void assertPresentIfInstance(Boolean result, Object cast,
			Class<?> castClass) {
		if (result) {
			assertEquals(Optional.of(cast),
					Reflections.presentIfInstance(cast, castClass));
		} else {
			assertFalse(Reflections.presentIfInstance(cast, castClass)
					.isPresent());
		}
	}

	public void testPresentIfInstance() {
		assertPresentIfInstance(true, "foo", String.class);
		assertPresentIfInstance(true, "foo", Object.class);
		assertPresentIfInstance(false, "foo", Integer.class);
		assertPresentIfInstance(true, 1, Integer.class);
		assertPresentIfInstance(true, 1, Object.class);
		assertPresentIfInstance(false, 1, String.class);
	}
}
