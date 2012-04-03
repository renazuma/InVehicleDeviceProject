package com.kogasoftware.odt.invehicledevice.navigation;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.graphics.Bitmap;

/**
 * 複数のバッファを使って表示をスムーズにした
 * 
 * @author ksc
 * 
 */
public class MapSynchronizer {
	static public interface Accessor {
		public void run(MapSnapshot mapSnapshot);
	}

	private final Object lock = new Object();
	private final Integer NUM_BUFFERS = 5;
	private final Queue<MapSnapshot> writeQueue = new ConcurrentLinkedQueue<MapSnapshot>();
	private final Queue<MapSnapshot> readQueue = new ConcurrentLinkedQueue<MapSnapshot>();

	private volatile Boolean dirty = true;
	private volatile MapSnapshot readingSnapshot = null;

	public MapSynchronizer() {
		create();
	}

	public Boolean isDirty() {
		synchronized (lock) {
			return dirty;
		}
	}

	public void read(Accessor accessor) {
		synchronized (lock) {
			if (readQueue.size() > 0) {
				writeQueue.add(readingSnapshot);
				readingSnapshot = readQueue.poll();
			}
		}
		if (readingSnapshot == null) {
			return;
		}
		accessor.run(readingSnapshot);
		synchronized (lock) {
			if (readQueue.size() == 0) {
				dirty = false;
			}
		}
	}

	public void write(Accessor accessor) {
		MapSnapshot arg = null;
		synchronized (lock) {
			if (writeQueue.size() == 0) {
				arg = readQueue.poll();
			} else {
				arg = writeQueue.poll();
			}
		}
		if (arg == null) {
			return;
		}
		accessor.run(arg);
		synchronized (lock) {
			readQueue.add(arg);
			dirty = true;
		}
	}

	public void create() {
		synchronized (lock) {
			destroy();
			for (Integer index = 0; index < NUM_BUFFERS; ++index) {
				MapSnapshot m = new MapSnapshot();
				if (m.bitmap != null) {
					m.bitmap.recycle();
				}
				m.bitmap = Bitmap.createBitmap(MapRenderer.MAP_TEXTURE_WIDTH,
						MapRenderer.MAP_TEXTURE_HEIGHT, Bitmap.Config.RGB_565);
				System.currentTimeMillis();
				writeQueue.add(m);
			}
			readingSnapshot = writeQueue.poll();
		}
	}

	public void destroy() {
		synchronized (lock) {
			while (true) {
				MapSnapshot s = readQueue.poll();
				if (s == null) {
					break;
				}
				s.bitmap.recycle();
			}
			while (true) {
				MapSnapshot s = writeQueue.poll();
				if (s == null) {
					break;
				}
				s.bitmap.recycle();
			}
			if (readingSnapshot != null) {
				Bitmap temp = readingSnapshot.bitmap;
				readingSnapshot = null;
				temp.recycle();
			}
		}
	}
}
