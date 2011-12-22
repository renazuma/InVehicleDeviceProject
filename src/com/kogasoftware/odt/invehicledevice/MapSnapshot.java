package com.kogasoftware.odt.invehicledevice;

import android.graphics.Bitmap;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

public class MapSnapshot {
	public GeoPoint center = new GeoPoint(0, 0);
	public Bitmap bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565);
	public Integer latitudeSpan = 0;
	public Integer longitudeSpan = 0;

	public MapSnapshot() {
	}

	public MapSnapshot(Bitmap bitmap, MapView mapView) {
		this.bitmap = bitmap;
		this.center = mapView.getMapCenter();
		this.latitudeSpan = mapView.getLatitudeSpan();
		this.longitudeSpan = mapView.getLongitudeSpan();
	}
}
