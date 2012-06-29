package com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.tilepipeline;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.tuple.Pair;

import android.util.Log;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.tilepipeline.PipeQueue.OnDropListener;

public abstract class PipeExchanger<K, F, T> implements Closeable {
	protected class Loader implements Runnable {
		@Override
		public void run() {
			try {
				loopLoad();
			} catch (InterruptedException e) {
			} finally {
				Log.i(getClass().getSimpleName(), "exit");
			}
		}
	}

	private static final String TAG = PipeExchanger.class.getSimpleName();
	protected static final Integer NUM_LOADERS = 3;
	protected final PipeQueue<K, F> fromPipeQueue;
	protected final PipeQueue<K, T> toPipeQueue;
	protected final ExecutorService loaders = Executors
			.newFixedThreadPool(NUM_LOADERS);
	protected final OnDropListener<K> onDropListener;
	protected final InVehicleDeviceService service;

	public PipeExchanger(InVehicleDeviceService service,
			PipeQueue<K, F> fromPipeQueue, PipeQueue<K, T> toPipeQueue,
			OnDropListener<K> onDropListener) {
		this.service = service;
		this.fromPipeQueue = fromPipeQueue;
		this.toPipeQueue = toPipeQueue;
		this.onDropListener = onDropListener;
		for (Integer i = 0; i < NUM_LOADERS; ++i) {
			loaders.submit(new Loader());
		}
	}

	public abstract void clear();

	@Override
	public void close() {
		loaders.shutdownNow();
	}

	protected abstract T load(K key, F from) throws IOException,
			InterruptedException;

	protected void loopLoad() throws InterruptedException {
		String T = getClass().getSimpleName();
		while (true) {
			Thread.sleep(100);
			Pair<K, F> fromPair = fromPipeQueue.take();
			Log.i(T, "start " + fromPair.getKey());
			long startTime = System.currentTimeMillis();
			T to = null;
			try {
				to = load(fromPair.getKey(), fromPair.getValue());
			} catch (IOException e) {
				Log.w(TAG, e);
			}
			String elapsed = " " + (System.currentTimeMillis() - startTime)
					+ "ms";
			if (to != null) {
				Log.i(T, "complete " + fromPair.getKey() + elapsed);
				toPipeQueue.addAndDrop(fromPair.getKey(), to);
			} else {
				Log.i(T, "error " + fromPair.getKey() + elapsed);
				onDropListener.onDrop(fromPair.getKey());
			}
		}
	}
}
