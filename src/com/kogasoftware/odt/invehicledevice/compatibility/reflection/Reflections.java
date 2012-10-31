package com.kogasoftware.odt.invehicledevice.compatibility.reflection;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.reflect.FieldUtils;

import android.util.Log;

import com.google.common.base.Optional;

public class Reflections {
	private static final String TAG = Reflections.class.getSimpleName();

	/**
	 * キャスト可能な場合はキャスト後の値、出来ない場合はabsent()を返す。
	 */
	public static <T> Optional<T> presentIfInstance(Object object,
			Class<T> checkClass) {
		if (checkClass.isInstance(object)) {
			return Optional.<T> of(checkClass.cast(object));
		} else {
			return Optional.<T> absent();
		}
	}

	/**
	 * リフレクション関連の例外処理を行う。
	 */
	public static abstract class ExceptionWrapper<T> {
		protected abstract Optional<T> process() throws ClassNotFoundException,
				IllegalAccessException, InvocationTargetException,
				NoSuchFieldException, NoSuchMethodException;

		public Optional<T> call() {
			try {
				return process();
			} catch (ClassNotFoundException e) {
				// do nothing
			} catch (SecurityException e) {
				// TODO: この例外が発生する可能性調査
				Log.w(TAG, e);
			} catch (NoSuchFieldException e) {
				// do nothing
			} catch (NoSuchMethodException e) {
				// do nothing
			} catch (IllegalArgumentException e) {
				// do nothing
			} catch (IllegalAccessException e) {
				Log.w(TAG, e);
			} catch (InvocationTargetException e) {
				Log.w(TAG, e);
			}
			return Optional.absent();
		}
	}

	/**
	 * FieldUtils.readStaticFieldを実行し、失敗したらabsent()を返す。
	 */
	public static <T> Optional<T> readStaticField(final Class<?> targetClass,
			final String fieldName, final Class<T> valueClass) {
		return new ExceptionWrapper<T>() {
			@Override
			protected Optional<T> process() throws NoSuchFieldException,
					IllegalAccessException {
				return presentIfInstance(
						FieldUtils.readStaticField(targetClass, fieldName),
						valueClass);
			}
		}.call();
	}
}
