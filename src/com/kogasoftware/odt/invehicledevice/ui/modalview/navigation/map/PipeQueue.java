package com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.map;

import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Semaphore;

import com.google.common.base.Optional;

public class PipeQueue<T> {
	private final Semaphore semaphore = new Semaphore(0);
	private final Object queueLock = new Object();
	private final Queue<T> queue = new ConcurrentLinkedQueue<T>();
	private final Set<T> reserved = new CopyOnWriteArraySet<T>();

	public void add(T element) {
		synchronized (queueLock) {
			queue.add(element);
			semaphore.release();
		}
	}

	public void remove(T element) {
		synchronized (queueLock) {
			queue.remove(element);
			reserved.remove(element);
		}
	}
	
	public Optional<T> get() {
		synchronized (queueLock) {
			for (T element : queue) {
				if (reserved.contains(element)) {
					continue;
				}
				reserved.add(element);
				return Optional.of(element);
			}
		}
		return Optional.absent();
	}

	public T reserve() throws InterruptedException {
		while (true) {
			semaphore.acquire();
			for (T element : get().asSet()) {
				return element;
			}
		}
	}
}
