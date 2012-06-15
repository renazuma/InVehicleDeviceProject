package com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.map;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import android.graphics.Point;
import android.graphics.PointF;

import com.google.common.base.Optional;
import com.javadocmd.simplelatlng.LatLng;
import com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.SphericalMercator;

public class TileKey implements Serializable {
	private static final long serialVersionUID = -2858195330177966613L;
	public static final int TILE_LENGTH = 256;
	private final int x;
	private final int y;
	private final int zoom;

	public TileKey(int x, int y, int zoom) {
		this.x = x;
		this.y = y;
		this.zoom = zoom;
	}

	public TileKey(LatLng latLng, int zoom) {
		x = (int) Math.floor(((180.0 + latLng.getLongitude()) / 360.0)
				* Math.pow(2, zoom));
		y = (int) Math.floor(((180.0 - SphericalMercator.lat2y(latLng
				.getLatitude())) / 360.0) * Math.pow(2, zoom));
		this.zoom = zoom;
	}

	public Point getCenterPixel() {
		int totalSideTiles = 1 << zoom;
		int px = TILE_LENGTH / 2 * (x * 2 - totalSideTiles + 1);
		int py = -(TILE_LENGTH / 2 * (y * 2 - totalSideTiles + 1));
		return new Point(px, py);
	}

	public PointF xgetOffsetPixels(LatLng from) {
		double longitudePixels = (getCenter().getLongitude() - from
				.getLongitude()) * 256 * Math.pow(2, zoom) / 360;

		double sphericalLatitudeTo = 360.0 / Math.pow(2, zoom) * (y + 0.5)
				- 180.0;
		double sphericalLatitudeFrom = SphericalMercator.lat2y(from
				.getLatitude());
		double sphericalLatitudeDistance = sphericalLatitudeTo
				- sphericalLatitudeFrom;
		double latitudePixels = sphericalLatitudeDistance * 256
				* Math.pow(2, zoom) / 360;

		return new PointF((float) latitudePixels, (float) longitudePixels);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(x).append(y).append(zoom)
				.toHashCode();
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof TileKey) {
			TileKey other = (TileKey) object;
			return new EqualsBuilder().append(x, other.x).append(y, other.y)
					.append(zoom, other.zoom).isEquals();
		} else {
			return false;
		}
	}

	public String toFileName() {
		return zoom + "_" + x + "_" + y + ".png";
	}

	public LatLng getCenter() {
		double longitude = 360.0 / Math.pow(2, zoom) * (x + 0.5) - 180.0;
		double latitude = -SphericalMercator.y2lat(360.0 / Math.pow(2, zoom)
				* (y + 0.5) - 180.0);
		return new LatLng(latitude, longitude);
	}

	public int getZoom() {
		return zoom;
	}

	public Optional<TileKey> getRelativeTileKey(int extraX, int extraY) {
		int totalSideTiles = 1 << zoom;
		int newX = x + extraX;
		int newY = y + extraY;
		if (newX < 0 || totalSideTiles <= newX) {
			return Optional.absent();
		}
		if (newY < 0 || totalSideTiles <= newY) {
			return Optional.absent();
		}
		return Optional.of(new TileKey(newX, newY, zoom));
	}
}
