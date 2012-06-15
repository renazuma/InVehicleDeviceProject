package com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.map;


public class TilePair<V> {
	private final TileKey tileKey;
	private final V value;

	public TilePair(TileKey tileKey, V value) {
		this.tileKey = tileKey;
		this.value = value;
	}

	public V getValue() {
		return value;
	}

	public TileKey getTileKey() {
		return tileKey;
	}
}
