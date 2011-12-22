package com.kogasoftware.viridian;


abstract public class MapSynchronizer {

	public interface Accessor {
		public void run(MapSnapshot mapSnapshot);
	}

	public static MapSynchronizer getInstance() {
		// return new SimpleBitmapSynchronizer();
		// return new DoubleBufferBitmapSynchronizer();
		return new MultiBufferMapSynchronizer();
	}

	abstract public Boolean isDirty();

	abstract public void read(Accessor accessor);

	abstract public void write(Accessor accessor);

}
