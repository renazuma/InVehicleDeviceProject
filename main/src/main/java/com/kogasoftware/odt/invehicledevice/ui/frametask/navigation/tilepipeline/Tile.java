package com.kogasoftware.odt.invehicledevice.ui.frametask.navigation.tilepipeline;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import android.graphics.PointF;

import com.google.common.base.Optional;
import com.javadocmd.simplelatlng.LatLng;
import com.kogasoftware.odt.invehicledevice.ui.fragment.navigation.SphericalMercator;

public class Tile implements Serializable {
	private static final long serialVersionUID = -2858195330177966613L;
	public static final int WIDTH = 256;
	public static final int HEIGHT = 256;
	private final int x;
	private final int y;
	private final int zoom;

	public Tile(int x, int y, int zoom) {
		this.x = x;
		this.y = y;
		this.zoom = zoom;
	}

	public Tile(LatLng latLng, int zoom) {
		x = (int) Math.floor(((180.0 + latLng.getLongitude()) / 360.0)
				* Math.pow(2, zoom));
		y = (int) Math.floor(((180.0 - SphericalMercator.lat2y(latLng
				.getLatitude())) / 360.0) * Math.pow(2, zoom));
		this.zoom = zoom;
	}

	@Override
	public String toString() {
		return "(" + x + "," + y + "," + zoom + ")";
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public PointF getCenterPixel() {
		float px = WIDTH / (float) (1 << zoom) * (x + 0.5f) - WIDTH / 2;
		float py = HEIGHT / 2 - HEIGHT / (float) (1 << zoom) * (y + 0.5f);
		return new PointF(px, py);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(x).append(y).append(zoom)
				.toHashCode();
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof Tile) {
			Tile other = (Tile) object;
			return new EqualsBuilder().append(x, other.x).append(y, other.y)
					.append(zoom, other.zoom).isEquals();
		} else {
			return false;
		}
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

	public Optional<Tile> getRelativeTile(int extraX, int extraY) {
		int totalSideTiles = 1 << zoom;
		int newX = x + extraX;
		int newY = y + extraY;
		if (newX < 0 || totalSideTiles <= newX) {
			return Optional.absent();
		}
		if (newY < 0 || totalSideTiles <= newY) {
			return Optional.absent();
		}
		return Optional.of(new Tile(newX, newY, zoom));
	}
}