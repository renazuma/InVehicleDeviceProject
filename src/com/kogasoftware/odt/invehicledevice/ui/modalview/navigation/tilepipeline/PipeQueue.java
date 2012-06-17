package com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.tilepipeline;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.Semaphore;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Optional;

public class PipeQueue<K, V> {
	public static interface OnDropListener<K> {
		void onDrop(K key);
	}

	private final Semaphore semaphore = new Semaphore(0);
	private final Object elementsLock = new Object();
	private final int limit;
	private final OnDropListener<K> onDropListener;
	private final SortedSet<Pair<K, V>> elements;

	public PipeQueue(int limit, OnDropListener<K> onDropListener,
			final Comparator<K> comparator) {
		this.limit = limit;
		this.onDropListener = onDropListener;
		elements = new TreeSet<Pair<K, V>>(new Comparator<Pair<K, V>>() {
			@Override
			public int compare(Pair<K, V> lhs, Pair<K, V> rhs) {
				return comparator.compare(lhs.getKey(), rhs.getKey());
			}
		});
	}

	public Optional<Pair<K, V>> add(K key, V value) {
		synchronized (elementsLock) {
			semaphore.release();
			Pair<K, V> element = Pair.of(key, value);

			// サイズがlimit以下なら、追加して終了
			if (elements.size() < limit) {
				elements.add(element);
				return Optional.absent();
			}

			// サイズがlimit以上の場合
			Pair<K, V> first = elements.first();
			if (elements.comparator().compare(element, first) > 0) {
				// 新しい要素が最初の要素より優先度が高い場合、最初の要素を削除して新規追加
				elements.remove(first);
				elements.add(element);
				return Optional.of(first);
			} else {
				// 新しい要素よりも優先度が低い場合、追加失敗
				return Optional.of(element);
			}
		}
	}

	public void addAndDrop(K key, V value) {
		synchronized (elementsLock) {
			for (Pair<K, V> pair : add(key, value).asSet()) {
				onDropListener.onDrop(pair.getKey());
				disposeValue(pair.getValue());
			}
		}
	}

	protected void disposeValue(V value) {
	}

	public Optional<Pair<K, V>> poll() {
		synchronized (elementsLock) {
			if (elements.size() > 0) {
				Pair<K, V> pair = elements.last();
				elements.remove(pair);
				return Optional.of(pair);
			} else {
				return Optional.absent();
			}
		}
	}

	public Pair<K, V> take() throws InterruptedException {
		while (true) {
			semaphore.acquire();
			synchronized (elementsLock) {
				for (Pair<K, V> pair : poll().asSet()) {
					return pair;
				}
			}
		}
	}

	public Optional<V> getIfPresent(K key) {
		synchronized (elementsLock) {
			for (Pair<K, V> pair : elements) {
				if (pair.getKey().equals(key)) {
					return Optional.of(pair.getValue());
				}
			}
			return Optional.absent();
		}
	}

	public Set<K> getKeys() {
		synchronized (elementsLock) {
			Set<K> set = new HashSet<K>();
			for (Pair<K, V> pair : elements) {
				set.add(pair.getKey());
			}
			return set;
		}
	}

	public void clear() {
		synchronized (elementsLock) {
			for (Pair<K, V> pair : elements) {
				onDropListener.onDrop(pair.getKey());
				disposeValue(pair.getValue());
			}
			elements.clear();
		}
	}

	public void sort() {
		synchronized (elementsLock) {
			Set<Pair<K, V>> temp = new HashSet<Pair<K, V>>(elements);
			elements.clear();
			elements.addAll(temp);
		}
	}

	public Optional<V> remove(K key) {
		synchronized (elementsLock) {
			for (Pair<K, V> pair : new HashSet<Pair<K, V>>(elements)) {
				if (pair.getKey().equals(key)) {
					elements.remove(pair);
					return Optional.of(pair.getValue());
				}
			}
			return Optional.absent();
		}
	}
}
