package com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.map;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.util.Log;

import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;

public abstract class TilePipe<F, T> implements Closeable {
	private static final String TAG = TilePipe.class.getSimpleName();
	protected static final Integer NUM_LOADERS = 3;
	protected final PipeQueue<TilePair<F>> fromPipeQueue;
	protected final PipeQueue<TilePair<T>> toPipeQueue;
	protected final ExecutorService loaders = Executors
			.newFixedThreadPool(NUM_LOADERS);
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
			TilePair<F> from = fromPipeQueue.acquire();
			TilePair<T> to = null;
			try {
				to = load(from);
			} catch (IOException e) {
				Log.w(TAG, e);
			} finally {
				fromPipeQueue.remove(from);
			}
			if (to != null) {
				toPipeQueue.add(to);
			}
		}
	}

	protected abstract TilePair<T> load(TilePair<F> from) throws IOException;

	public TilePipe(PipeQueue<TilePair<F>> fromPipeQueue,
			PipeQueue<TilePair<T>> toPipeQueue) {
		this.fromPipeQueue = fromPipeQueue;
		this.toPipeQueue = toPipeQueue;
		for (Integer i = 0; i < NUM_LOADERS; ++i) {
			loaders.submit(new Loader());
		}
	}

	@Override
	public void close() {
		loaders.shutdownNow();
	}
}
