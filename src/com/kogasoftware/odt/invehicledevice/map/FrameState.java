package com.kogasoftware.odt.invehicledevice.map;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Point;
import android.graphics.PointF;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Projection;

public class FrameState {
	private final GL10 gl;
	private final Long milliSeconds;
	private final Double mapAngle;
	private final GeoPoint mapCenter;
	private final Double mapPixelZoom;
	private final Projection mapProjection;
	private final Integer latitudeSpan;
	private final Integer longitudeSpan;
	private final MapView mapView;

	public FrameState(GL10 gl, Long milliSeconds, Double mapAngle,
			GeoPoint mapCenter,
			Double mapPixelZoom, MapView mapView) {
		this.gl = gl;
		this.milliSeconds = milliSeconds;
		this.mapAngle = mapAngle;
		this.latitudeSpan = mapView.getLatitudeSpan();
		this.longitudeSpan = mapView.getLongitudeSpan();
		this.mapCenter = mapCenter;
		this.mapPixelZoom = mapPixelZoom;
		this.mapProjection = mapView.getProjection();
		this.mapView = mapView;
	}

	/**
	 * GeoPointを描画先の座標へ変換
	 * 
	 * @param frameState
	 * @param geoPoint
	 * @return
	 */
	PointF convertGeoPointToPointF(GeoPoint geoPoint) {

		GeoPoint oldGeoPoint = geoPoint; // TODO 変数名
		Point nearRoundedPoint = new Point(); // Projection.toPixels()によって丸められた点
		Point farRoundedPoint = new Point(); // Projection.toPixels()と逆方向に丸められた点
		GeoPoint oldCenter = mapCenter; // 描画対象の地図の中心

		// -------------------------------------------------------
		// Google Maps地図の状態に依存するコードのため，処理は最小にする.ここから.
		// 現在の地図の中心取得
		GeoPoint newCenter = mapView.getMapCenter();
		// 描画対象の点を，現在の地図の点に変換
		GeoPoint newGeoPoint = new GeoPoint(oldGeoPoint.getLatitudeE6()
				- oldCenter.getLatitudeE6() + newCenter.getLatitudeE6(),
				oldGeoPoint.getLongitudeE6() - oldCenter.getLongitudeE6()
				+ newCenter.getLongitudeE6());
		Projection projection = mapView.getProjection();
		projection.toPixels(newGeoPoint, nearRoundedPoint);
		// toPixels()により整数値に丸められてしまう.丸められたデータに対するGeoPointを取得
		GeoPoint nearRoundedGeoPoint = projection.fromPixels(
				nearRoundedPoint.x, nearRoundedPoint.y);
		// toPixels()と逆向きに丸めたデータに対するGeoPointを取得
		if (nearRoundedGeoPoint.getLongitudeE6() < newGeoPoint.getLongitudeE6()) {
			farRoundedPoint.x = nearRoundedPoint.x + 1;
		} else {
			farRoundedPoint.x = nearRoundedPoint.x - 1;
		}
		if (nearRoundedGeoPoint.getLatitudeE6() < newGeoPoint.getLatitudeE6()) {
			farRoundedPoint.y = nearRoundedPoint.y - 1;
		} else {
			farRoundedPoint.y = nearRoundedPoint.y + 1;
		}
		GeoPoint farRoundedGeoPoint = projection.fromPixels(farRoundedPoint.x,
				farRoundedPoint.y);
		// Google Maps地図の状態に依存するコードのため，処理は最小にする．ここまで.
		// -------------------------------------------------------

		// 回転前の座標に対する差分を計算
		Double dx = (newGeoPoint.getLongitudeE6() - nearRoundedGeoPoint
				.getLongitudeE6())
				/ (double) Math.abs(nearRoundedGeoPoint.getLongitudeE6()
						- farRoundedGeoPoint.getLongitudeE6());

		Double dy = (newGeoPoint.getLatitudeE6() - nearRoundedGeoPoint
				.getLatitudeE6())
				/ (double) Math.abs(nearRoundedGeoPoint.getLatitudeE6()
						- farRoundedGeoPoint.getLatitudeE6());
		// 回転前の座標を計算
		Double rx = nearRoundedPoint.x + dx;
		Double ry = nearRoundedPoint.y - dy;

		String log = newGeoPoint.getLatitudeE6() + ","
				+ nearRoundedGeoPoint.getLatitudeE6() + ","
				+ farRoundedGeoPoint.getLatitudeE6() + "," + ry + ","
				+ nearRoundedPoint.y + "," + farRoundedPoint.y + "," +
				/* ------------------------------- */
				newGeoPoint.getLongitudeE6() + ","
				+ nearRoundedGeoPoint.getLongitudeE6() + ","
				+ farRoundedGeoPoint.getLongitudeE6() + "," + rx + ","
				+ +nearRoundedPoint.x + "," + farRoundedPoint.x + "," +
				/* ------------------------------- */
				"";
		// Log.i("SpriteDraw", log);

		// OpenGL上の地図テクスチャの座標に変換
		ry = (double) Property.MAP_TEXTURE_HEIGHT / 2 - ry;
		rx = rx - (double) Property.MAP_TEXTURE_WIDTH / 2;

		// 回転する
		Double fromAngle = Math.atan2(ry, rx);
		Double toAngle = fromAngle + mapAngle;
		Double length = Math.sqrt(rx * rx + ry * ry);
		Double x = length * Math.cos(toAngle) * mapPixelZoom;
		Double y = length * Math.sin(toAngle) * mapPixelZoom;

		return new PointF(x.floatValue(), y.floatValue());
	}

	public GL10 getGL() {
		return gl;
	}

	public Double getMapAngle() {
		return mapAngle;
	}

	public GeoPoint getMapCenter() {
		return mapCenter;
	}

	public Integer getMapLatitudeSpan() {
		return latitudeSpan;
	}

	public Integer getMapLongitudeSpan() {
		return longitudeSpan;
	}

	public Double getMapPixelZoom() {
		return mapPixelZoom;
	}

	public Projection getMapProjection() {
		return mapProjection;
	}

	public Long getMilliSeconds() {
		return milliSeconds;
	}
}
