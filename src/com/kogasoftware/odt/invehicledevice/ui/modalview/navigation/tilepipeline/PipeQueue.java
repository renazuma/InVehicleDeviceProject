package com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.tilepipeline;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Semaphore;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

public class PipeQueue<K, V> {
	private final int limit;
	private final Semaphore semaphore = new Semaphore(0);
	private final Object queueLock = new Object();
	private final Queue<Pair<K, V>> queue = new LinkedList<Pair<K, V>>();
	private final Set<K> reserved = new HashSet<K>();
	private final Predicate<K> isValid;

	public PipeQueue(int limit, Predicate<K> isValid) {
		this.limit = limit;
		this.isValid = isValid;
	}

	public PipeQueue(int limit) {
		this(limit, Predicates.<K> alwaysTrue());
	}

	public boolean add(K key, V value) {
		// 有効かどうかの確認
		if (!isValid.apply(key)) {
			return false;
		}

		synchronized (queueLock) {
			semaphore.release();
			if (queue.size() >= limit) {
				return false;
			}
			queue.add(Pair.of(key, value));
		}

		return true;
	}

	public void remove(Predicate<Pair<K, V>> predicate) {
		synchronized (queueLock) {
			for (Iterator<Pair<K, V>> iterator = queue.iterator(); iterator
					.hasNext();) {
				Pair<K, V> pair = iterator.next();
				if (!predicate.apply(pair)) {
					continue;
				}
				iterator.remove();
				reserved.remove(pair.getKey());
				disposeValue(pair.getValue());
			}
		}
	}

	protected void disposeValue(V value) {
	}

	public void remove(Pair<K, V> element) {
		remove(Predicates.equalTo(element));
	}

	public Optional<Pair<K, V>> reserveIfPresent() {
		synchronized (queueLock) {
			// TODO:非効率
			for (Pair<K, V> element : queue) {
				if (reserved.contains(element)) {
					continue;
				}
				reserved.add(element.getKey());
				return Optional.of(element);
			}
		}
		return Optional.absent();
	}

	public List<Pair<K, V>> reserve(Predicate<K> predicate) {
		List<Pair<K, V>> result = new LinkedList<Pair<K, V>>();
		synchronized (queueLock) {
			for (Pair<K, V> element : queue) {
				if (reserved.contains(element)) {
					continue;
				}
				if (!isValid.apply(element.getKey())) {
					continue;
				}
				if (!predicate.apply(element.getKey())) {
					continue;
				}
				reserved.add(element.getKey());
				result.add(element);
			}
		}
		return result;
	}

	public Pair<K, V> reserve() throws InterruptedException {
		while (true) {
			semaphore.acquire(); // デッドロックになるため、このacquireはsynchronizedの中で行われないよう注意する
			for (Pair<K, V> element : reserveIfPresent().asSet()) {
				return element;
			}
		}
	}
}
