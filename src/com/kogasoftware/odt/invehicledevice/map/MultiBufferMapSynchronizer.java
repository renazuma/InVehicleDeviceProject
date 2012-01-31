package com.kogasoftware.odt.invehicledevice.map;

import java.util.LinkedList;
import java.util.Queue;

import android.graphics.Bitmap;

/**
 * 複数のバッファを使って表示をスムーズにした
 * 
 * @author saturday06
 * 
 */
public class MultiBufferMapSynchronizer extends MapSynchronizer {
	static private class MapSnapshotAndTime {
		MapSnapshot mapSnapshot = new MapSnapshot();
	}

	private final Object lock = new Object();
	private final Integer NUM_BUFFERS = 5;
	private final Queue<MapSnapshotAndTime> writeQueue = new LinkedList<MapSnapshotAndTime>();
	private final Queue<MapSnapshotAndTime> readQueue = new LinkedList<MapSnapshotAndTime>();

	private volatile Boolean dirty = true;
	private MapSnapshotAndTime readingBitmap = null;

	public MultiBufferMapSynchronizer() {
		for (Integer index = 0; index < NUM_BUFFERS; ++index) {
			MapSnapshotAndTime m = new MapSnapshotAndTime();
			m.mapSnapshot.bitmap = Bitmap.createBitmap(
					MapRenderer.MAP_TEXTURE_WIDTH,
					MapRenderer.MAP_TEXTURE_HEIGHT, Bitmap.Config.RGB_565);
			System.currentTimeMillis();
			writeQueue.add(m);
		}
		readingBitmap = writeQueue.poll();
	}

	@Override
	public Boolean isDirty() {
		synchronized (lock) {
			return dirty;
		}
	}

	@Override
	public void read(Accessor accessor) {
		synchronized (lock) {
			if (readQueue.size() > 0) {
				writeQueue.add(readingBitmap);
				readingBitmap = readQueue.poll();
			}
		}
		accessor.run(readingBitmap.mapSnapshot);
		synchronized (lock) {
			if (readQueue.size() == 0) {
				dirty = false;
			}
		}
	}

	@Override
	public void write(Accessor accessor) {
		MapSnapshotAndTime arg = null;
		synchronized (lock) {
			if (writeQueue.size() == 0) {
				arg = readQueue.poll();
			} else {
				arg = writeQueue.poll();
			}
		}
		accessor.run(arg.mapSnapshot);
		synchronized (lock) {
			readQueue.add(arg);
			dirty = true;
		}
	}
}
