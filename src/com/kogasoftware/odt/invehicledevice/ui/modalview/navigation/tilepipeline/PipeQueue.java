package com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.tilepipeline;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Optional;

public class PipeQueue<K, V> {
	public static interface OnDropListener<K> {
		void onDrop(K key);
	}

	private final BlockingQueue<Pair<K, V>> queue;
	private final OnDropListener<K> onDropListener;

	public PipeQueue(int limit, OnDropListener<K> onDropListener) {
		queue = new LinkedBlockingQueue<Pair<K, V>>(limit);
		this.onDropListener = onDropListener;
	}

	public void add(K key, V value) {
		try {
			queue.add(Pair.of(key, value));
		} catch (IllegalStateException e) {
			// Queue full
			// TODO:ブロックする処理で書き換え
			onDropListener.onDrop(key);
		}
	}

	protected void disposeValue(V value) {
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

	public void clear() {
		List<Pair<K, V>> list = new LinkedList<Pair<K, V>>();
		queue.drainTo(list);
		for (Pair<K, V> pair : list) {
			onDropListener.onDrop(pair.getKey());
		}
	}
}
