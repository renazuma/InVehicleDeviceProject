package com.kogasoftware.odt.invehicledevice.test.unit.compatibility.reflection;

import java.lang.reflect.InvocationTargetException;

import android.test.AndroidTestCase;

import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.compatibility.reflection.Reflections;
import com.kogasoftware.odt.invehicledevice.compatibility.reflection.Reflections.ExceptionWrapper;

import static org.mockito.Mockito.mock;

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

	static class Foo {
		@SuppressWarnings("unused")
		private static Double D = 1.125;
		public static Integer I = 1;
		public static String S = "foo_string";
	};

	static class Bar {
		public static Double D = 1.25;
		public static String S = "bar_string";
	};

	public void testReadStaticField() {
		assertEquals(Optional.of(Foo.I),
				Reflections.readStaticField(Foo.class, "I", Integer.class));
		assertEquals(Optional.of(Foo.I),
				Reflections.readStaticField(Foo.class, "I", Number.class));
		assertEquals(Optional.absent(),
				Reflections.readStaticField(Foo.class, "I", String.class));

		assertEquals(Optional.of(Foo.S),
				Reflections.readStaticField(Foo.class, "S", String.class));
		assertEquals(Optional.of(Foo.S),
				Reflections.readStaticField(Foo.class, "S", String.class));
		assertEquals(Optional.absent(),
				Reflections.readStaticField(Foo.class, "S", Integer.class));

		assertEquals(Optional.absent(),
				Reflections.readStaticField(Foo.class, "D", Double.class));
		assertEquals(Optional.absent(),
				Reflections.readStaticField(Foo.class, "D", Number.class));
		assertEquals(Optional.absent(),
				Reflections.readStaticField(Foo.class, "D", String.class));

		assertEquals(Optional.absent(),
				Reflections.readStaticField(Bar.class, "I", Integer.class));
		assertEquals(Optional.absent(),
				Reflections.readStaticField(Bar.class, "I", Number.class));
		assertEquals(Optional.absent(),
				Reflections.readStaticField(Bar.class, "I", String.class));

		assertEquals(Optional.of(Bar.S),
				Reflections.readStaticField(Bar.class, "S", String.class));
		assertEquals(Optional.of(Bar.S),
				Reflections.readStaticField(Bar.class, "S", String.class));
		assertEquals(Optional.absent(),
				Reflections.readStaticField(Bar.class, "S", Integer.class));

		assertEquals(Optional.of(Bar.D),
				Reflections.readStaticField(Bar.class, "D", Double.class));
		assertEquals(Optional.of(Bar.D),
				Reflections.readStaticField(Bar.class, "D", Number.class));
		assertEquals(Optional.absent(),
				Reflections.readStaticField(Bar.class, "D", String.class));
	}

	public void testExceptionWrapper() {
		{
			Optional<Integer> o = new ExceptionWrapper<Integer>() {
				@Override
				protected Optional<Integer> process() {
					return Optional.of(1);
				}
			}.call();
			assertEquals(Optional.of(1), o);
		}

		{
			Optional<Integer> o = new ExceptionWrapper<Integer>() {
				@Override
				protected Optional<Integer> process()
						throws ClassNotFoundException {
					throw new ClassNotFoundException();
				}
			}.call();
			assertEquals(Optional.absent(), o);
		}

		{
			Optional<Integer> o = new ExceptionWrapper<Integer>() {
				@Override
				protected Optional<Integer> process() {
					throw new SecurityException();
				}
			}.call();
			assertEquals(Optional.absent(), o);
		}

		{
			Optional<Integer> o = new ExceptionWrapper<Integer>() {
				@Override
				protected Optional<Integer> process()
						throws NoSuchFieldException {
					throw new NoSuchFieldException();
				}
			}.call();
			assertEquals(Optional.absent(), o);
		}

		{
			Optional<Integer> o = new ExceptionWrapper<Integer>() {
				@Override
				protected Optional<Integer> process()
						throws NoSuchMethodException {
					throw new NoSuchMethodException();
				}
			}.call();
			assertEquals(Optional.absent(), o);
		}

		{
			Optional<Integer> o = new ExceptionWrapper<Integer>() {
				@Override
				protected Optional<Integer> process()
						throws IllegalArgumentException {
					throw new IllegalArgumentException();
				}
			}.call();
			assertEquals(Optional.absent(), o);
		}

		{
			Optional<Integer> o = new ExceptionWrapper<Integer>() {
				@Override
				protected Optional<Integer> process()
						throws IllegalAccessException {
					throw new IllegalAccessException();
				}
			}.call();
			assertEquals(Optional.absent(), o);
		}

		{
			Optional<Integer> o = new ExceptionWrapper<Integer>() {
				@Override
				protected Optional<Integer> process()
						throws InvocationTargetException {
					throw mock(InvocationTargetException.class);
				}
			}.call();
			assertEquals(Optional.absent(), o);
		}
		
		try {
			new ExceptionWrapper<Integer>() {
				@Override
				protected Optional<Integer> process() {
					throw new RuntimeException();
				}
			}.call();
			fail();
		} catch (RuntimeException e) {
		}
	}
}
