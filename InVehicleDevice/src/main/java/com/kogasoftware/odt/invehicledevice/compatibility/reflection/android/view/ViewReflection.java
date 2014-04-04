package com.kogasoftware.odt.invehicledevice.compatibility.reflection.android.view;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import android.view.View;

import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.compatibility.reflection.Reflections;
import com.kogasoftware.odt.invehicledevice.compatibility.reflection.Reflections.ExceptionWrapper;

public class ViewReflection {
	/**
	 * "http://developer.android.com/reference/android/view/View.html#SYSTEM_UI_FLAG_VISIBLE"
	 */
	public static final Optional<Integer> SYSTEM_UI_FLAG_VISIBLE = Reflections
			.readStaticField(View.class, "SYSTEM_UI_FLAG_VISIBLE",
					Integer.class);

	/**
	 * "http://developer.android.com/reference/android/view/View.html#SYSTEM_UI_FLAG_LOW_PROFILE"
	 */
	public static final Optional<Integer> SYSTEM_UI_FLAG_LOW_PROFILE = Reflections
			.readStaticField(View.class, "SYSTEM_UI_FLAG_LOW_PROFILE",
					Integer.class);

	/**
	 * "http://developer.android.com/reference/android/view/View.html#STATUS_BAR_VISIBLE"
	 */
	public static final Optional<Integer> STATUS_BAR_VISIBLE = Reflections
			.readStaticField(View.class, "STATUS_BAR_VISIBLE",
					Integer.class);

	/**
	 * "http://developer.android.com/reference/android/view/View.html#STATUS_BAR_HIDDEN"
	 */
	public static final Optional<Integer> STATUS_BAR_HIDDEN = Reflections
			.readStaticField(View.class, "STATUS_BAR_HIDDEN",
					Integer.class);

	/**
	 * "http://developer.android.com/reference/android/view/View.html#setSystemUiVisibility()"
	 */
	public static Optional<Object> setSystemUiVisibility(final View view,
			final int visibility) {
		return new ExceptionWrapper<Object>() {
			@Override
			protected Optional<Object> process() throws IllegalAccessException,
					NoSuchMethodException, InvocationTargetException {
				View.class.getMethod("setSystemUiVisibility", int.class)
						.invoke(view, visibility);
				return Optional.of(new Object());
			}
		}.call();
	}

	/**
	 * "http://developer.android.com/reference/android/view/View.html#getSystemUiVisibility()"
	 */
	public static Optional<Integer> getSystemUiVisibility(final View view) {
		return new ExceptionWrapper<Integer>() {
			@Override
			protected Optional<Integer> process()
					throws IllegalAccessException, NoSuchMethodException,
					InvocationTargetException {
				return Reflections.presentIfInstance(
						View.class.getMethod("getSystemUiVisibility").invoke(
								view), Integer.class);
			}
		}.call();
	}

	/**
	 * "http://developer.android.com/reference/android/view/View.OnSystemUiVisibilityChangeListener.html"
	 */
	public static interface OnSystemUiVisibilityChangeListenerReflection {
		void onSystemUiVisibilityChange(int visibility);
	}

	/**
	 * "http://developer.android.com/reference/android/view/View.OnSystemUiVisibilityChangeListener.html"
	 */
	public static Optional<Object> newOnSystemUiVisibilityChangeListener(
			final OnSystemUiVisibilityChangeListenerReflection listener) {
		final InvocationHandler invocationHandler = new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args)
					throws Throwable {
				if (method.getName().equals("onSystemUiVisibilityChange")
						&& args.length == 1 && args[0] instanceof Integer) {
					listener.onSystemUiVisibilityChange((Integer) args[0]);
					return null;
				} else if (method.getName().equals("equals")
						&& args.length == 1) {
					return args[0] == proxy;
				}
				return method.invoke(listener, args);
			}
		};
		return new ExceptionWrapper<Object>() {
			@Override
			protected Optional<Object> process() throws IllegalAccessException,
					NoSuchMethodException, InvocationTargetException,
					ClassNotFoundException {
				Class<?> listenerClass = Class
						.forName("android.view.View$OnSystemUiVisibilityChangeListener");
				return Optional.of(Proxy.newProxyInstance(
						listenerClass.getClassLoader(),
						new Class<?>[] { listenerClass }, invocationHandler));
			}
		}.call();
	}

	/**
	 * "http://developer.android.com/reference/android/view/View.html#setOnSystemUiVisibilityChangeListener%28View.OnSystemUiVisibilityChangeListener%29"
	 */
	public static Optional<Object> setOnSystemUiVisibilityChangeListener(
			final View view,
			final OnSystemUiVisibilityChangeListenerReflection listener) {
		return new ExceptionWrapper<Object>() {
			@Override
			protected Optional<Object> process() throws IllegalAccessException,
					NoSuchMethodException, InvocationTargetException,
					ClassNotFoundException {
				Class<?> listenerClass = Class
						.forName("android.view.View$OnSystemUiVisibilityChangeListener");
				for (Object listenerProxy : newOnSystemUiVisibilityChangeListener(
						listener).asSet()) {
					View.class.getMethod(
							"setOnSystemUiVisibilityChangeListener",
							listenerClass).invoke(view, listenerProxy);
					return Optional.of(new Object());
				}
				return Optional.absent();
			}
		}.call();
	}
}
