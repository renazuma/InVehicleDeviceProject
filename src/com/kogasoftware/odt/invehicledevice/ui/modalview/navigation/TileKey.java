package com.kogasoftware.odt.invehicledevice.ui.modalview.navigation;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import android.graphics.PointF;

import com.javadocmd.simplelatlng.LatLng;

public class TileKey {
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

	public PointF getOffsetPixels(LatLng from) {
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
		return x + "_" + y + "_" + zoom;
	}

	public LatLng getCenter() {
		double longitude = 360.0 / Math.pow(2, zoom) * (x + 0.5) - 180.0;
		double latitude = -SphericalMercator.y2lat(360.0 / Math.pow(2, zoom)
				* (y + 0.5) - 180.0);
		return new LatLng(latitude, longitude);
	}

	public Integer getZoom() {
		return zoom;
	}
}
