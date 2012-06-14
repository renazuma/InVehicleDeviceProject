package com.kogasoftware.odt.invehicledevice.logic.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.common.eventbus.EventBus;

/**
 * com.google.common.eventbus.EventBusクラスに以下の機能を追加
 * 
 * - UIスレッド上でイベント処理を実行する.<br />
 * - HighPriorityアノテーション付きのインスタンスは、他のオブジェクトよりも先にイベント処理する.<br />
 * - 登録されている全てのオブジェクトを破棄する.<br />
 */
public class UiEventBus {
	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface HighPriority {
	}

	private static final String TAG = UiEventBus.class.getSimpleName();
	private final List<Object> registeredObjects = new LinkedList<Object>();
	private final AtomicBoolean disposed = new AtomicBoolean(false);
	private final Object registerAndDisposeLock = new Object(); // registeredObjectsと実際にEventBusにregisterされたオブジェクトの整合性のためのロック
	private final Handler uiHandler;
	private final EventBus highPriorityEventBus = new EventBus();
	private final EventBus lowPriorityEventBus = new EventBus();

	/**
	 * 実行対象ハンドラとしてMainLooperのHandlerを指定
	 */
	public UiEventBus() {
		this(new Handler(Looper.getMainLooper()));
	}

	/**
	 * 実行対象ハンドラを指定
	 */
	public UiEventBus(Handler uiHandler) {
		this.uiHandler = uiHandler;
	}

	/**
	 * 登録されているオブジェクトから、与えられたクラスのインスタンスの数を返す
	 */
	public Integer countRegisteredClass(Class<?> c) {
		Integer count = 0;
		for (Object object : new LinkedList<Object>(registeredObjects)
		/* unregisterでregisteredObjectsが変更されるためコピーしておく */) {
			if (c.isInstance(object)) {
				count++;
			}
		}
		return count;
	}

	/**
	 * 登録された全てのObjectをunregisterし、以降の登録もできなくする
	 */
	public void dispose() {
		synchronized (registerAndDisposeLock) {
			disposed.set(true);
			for (Object object : new LinkedList<Object>(registeredObjects)
			/* unregisterでregisteredObjectsが変更されるためコピーしておく */) {
				try {
					unregister(object);
				} catch (IllegalArgumentException e) {
					Log.w(TAG, e);
				}
			}
			registeredObjects.clear();
		}
	}

	private EventBus getEventBusForObject(Object object) {
		if (object.getClass().isAnnotationPresent(HighPriority.class)) {
			return highPriorityEventBus;
		} else {
			return lowPriorityEventBus;
		}
	}

	/**
	 * UIのスレッドでObjectをポストする
	 */
	public void post(final Object object) {
		uiHandler.post(new Runnable() {
			@Override
			public void run() {
				if (disposed.get()) {
					return;
				}
				highPriorityEventBus.post(object);
				lowPriorityEventBus.post(object);
			}
		});
	}

	/**
	 * 登録と同時に、一括削除用に内部で独自にオブジェクトを保持
	 */
	public void register(Object object) {
		synchronized (registerAndDisposeLock) {
			if (disposed.get()) {
				return;
			}
			if (registeredObjects.contains(object)) {
				Log.w(TAG, "duplicated registration: " + object);
				return;
			}
			registeredObjects.add(object);
			getEventBusForObject(object).register(object);
		}
	}

	/**
	 * 内部で独自に保持しているオブジェクトを削除
	 */
	public void unregister(Object object) {
		synchronized (registerAndDisposeLock) {
			if (registeredObjects.remove(object)) {
				getEventBusForObject(object).unregister(object);
			}
		}
	}
}
