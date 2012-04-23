package com.kogasoftware.odt.invehicledevice.event;

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
	private final Handler uiHandler = new Handler(Looper.getMainLooper());

	public void dispose() {
		synchronized (registerAndDisposeLock) {
			disposed.set(true);
			for (Object object : registeredObjects) {
				try {
					unregister(object);
				} catch (IllegalArgumentException e) {
					Log.w(TAG, e);
				}
			}
			registeredObjects.clear();
		}
	}

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
}
