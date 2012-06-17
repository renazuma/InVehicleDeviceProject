package com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.tilepipeline;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

public class PipeQueue<K, V> {
	private final BlockingQueue<Pair<K, V>> queue;
	private final Predicate<K> isValid;

	public PipeQueue(int limit, Predicate<K> isValid) {
		this.isValid = isValid;
		queue = new LinkedBlockingQueue<Pair<K, V>>(limit);
	}

	public PipeQueue(int limit) {
		this(limit, Predicates.<K> alwaysTrue());
	}

	public boolean add(K key, V value) {
		// 有効かどうかの確認
		if (!isValid.apply(key)) {
			return false;
		}
		try {
			queue.add(Pair.of(key, value));
			return true;
		} catch (IllegalStateException e) {
			// Queue full
			// TODO:ブロックする処理で書き換え
			return false;
		}
	}

	protected void disposeValue(V value) {
	}

	public void remove(Pair<K, V> element) {
		queue.remove(element);
	}

	public Optional<Pair<K, V>> poll() {
		return Optional.fromNullable(queue.poll());
	}

	public Pair<K, V> take() throws InterruptedException {
		return queue.take();
	}

	public Optional<V> getIfPresent(K key) {
		for (Pair<K, V> pair : new ArrayList<Pair<K, V>>(queue)) {
			if (pair.getKey().equals(key)) {
				return Optional.of(pair.getValue());
			}
		}
		return Optional.absent();
	}

	public Set<K> getKeys() {
		Set<K> set = new HashSet<K>();
		for (Pair<K, V> pair : new ArrayList<Pair<K, V>>(queue)) {
			set.add(pair.getKey());
		}
		return set;
	}
}
