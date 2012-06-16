package com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.map;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.tuple.Pair;

import android.util.Log;

import com.google.common.base.Predicate;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;

public abstract class PipeExchanger<K, F, T> implements Closeable {
	private static final String TAG = PipeExchanger.class.getSimpleName();
	protected static final Integer NUM_LOADERS = 3;
	protected final PipeQueue<K, F> fromPipeQueue;
	protected final PipeQueue<K, T> toPipeQueue;
	protected final ExecutorService loaders = Executors
			.newFixedThreadPool(NUM_LOADERS);
	protected final Predicate<Tile> isValid;
	protected CommonLogic commonLogic = new CommonLogic();

	public void setCommonLogic(CommonLogic commonLogic) {
		this.commonLogic.dispose();
		this.commonLogic = commonLogic;
	}

	protected class Loader implements Runnable {
		@Override
		public void run() {
			try {
				loopLoad();
			} catch (InterruptedException e) {
			}
		}
	}

	protected void loopLoad() throws InterruptedException {
		while (true) {
			Pair<K, F> fromPair = fromPipeQueue.reserve();
			T to = null;
			try {
				to = load(fromPair.getKey(), fromPair.getValue());
			} catch (IOException e) {
				Log.w(TAG, e);
			} finally {
				fromPipeQueue.remove(fromPair);
			}
			if (to != null) {
				toPipeQueue.add(fromPair.getKey(), to);
			}
		}
	}

	protected abstract T load(K key, F from) throws IOException,
			InterruptedException;

	public abstract void cancel(K key);

	public PipeExchanger(PipeQueue<K, F> fromPipeQueue,
			PipeQueue<K, T> toPipeQueue, Predicate<Tile> isValid) {
		this.fromPipeQueue = fromPipeQueue;
		this.toPipeQueue = toPipeQueue;
		this.isValid = isValid;
		for (Integer i = 0; i < NUM_LOADERS; ++i) {
			loaders.submit(new Loader());
		}
	}

	@Override
	public void close() {
		loaders.shutdownNow();
	}
}
