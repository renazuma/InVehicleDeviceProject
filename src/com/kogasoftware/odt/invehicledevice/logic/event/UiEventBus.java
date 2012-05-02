package com.kogasoftware.odt.invehicledevice.logic.event;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.common.eventbus.EventBus;

/**
 * com.google.common.eventbus.EventBusクラスに、UIスレッド上でハンドラを実行する機能を追加
 */
public class UiEventBus extends EventBus {
	private static final String TAG = UiEventBus.class.getSimpleName();
	private final List<Object> registeredObjects = new LinkedList<Object>();
	private final AtomicBoolean disposed = new AtomicBoolean(false);
	private final Object registerAndDisposeLock = new Object(); // registeredObjectsと実際にEventBusにregisterされたオブジェクトの整合性のためのロック
	private final Handler uiHandler;

	/**
	 * 実行対象ハンドラをMainLooperのHandlerを指定
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
	 * 登録された全てのObjectをunregisterし、以降の登録もできなくする
	 */
	public void dispose() {
		synchronized (registerAndDisposeLock) {
			disposed.set(true);
			for (Object object : new LinkedList<Object>(registeredObjects)
			/* unregister内でregisteredObjectsが変更される可能性があるためコピーしておく */) {
				try {
					unregister(object);
				} catch (IllegalArgumentException e) {
					Log.w(TAG, e);
				}
			}
			registeredObjects.clear();
		}
	}

	/**
	 * UIのスレッドでObjectをポストするように修正
	 */
	@Override
	public void post(final Object object) {
		if (disposed.get()) {
			return;
		}
		if (uiHandler.getLooper().getThread().getId() == Thread.currentThread()
				.getId()) {
			super.post(object);
			return;
		}
		uiHandler.post(new Runnable() {
			@Override
			public void run() {
				UiEventBus.this.post(object);
			}
		});
	}

	/**
	 * 登録と同時に、一括削除用に内部で独自にオブジェクトを保持しておくようにした
	 */
	@Override
	public void register(Object object) {
		synchronized (registerAndDisposeLock) {
			if (disposed.get()) {
				return;
			}
			registeredObjects.add(object);
			super.register(object);
		}
	}

	/**
	 * 内部で独自に保持しているオブジェクトを削除する
	 */
	@Override
	public void unregister(Object object) {
		synchronized (registerAndDisposeLock) {
			registeredObjects.remove(object);
			super.unregister(object);
		}
	}
}
