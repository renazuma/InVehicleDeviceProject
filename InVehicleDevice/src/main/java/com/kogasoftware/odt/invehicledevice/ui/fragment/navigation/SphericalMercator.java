package com.kogasoftware.odt.invehicledevice.ui.fragment.navigation;

/**
 * @see http://wiki.openstreetmap.org/wiki/Mercator#Java
 */
public class SphericalMercator {
	/**
	 * SphericalMercatorの、概ね-180から+180の値を、-85から+85程度の緯度へ変換
	 * 
	 * @param aY
	 * @return
	 */
	public static double y2lat(double aY) {
		return Math.toDegrees(2 * Math.atan(Math.exp(Math.toRadians(aY)))
				- Math.PI / 2);
	}

	/**
	 * 緯度（-90から+90）からSphericalMercatorの、概ね-180から+180の値に変換
	 * ただし、緯度の範囲は-85から+85くらいを指定しないと利用可能な値が得られない
	 * 
	 * @param aLat
	 * @return
	 */
	public static double lat2y(double aLat) {
		return Math.toDegrees(Math.log(Math.tan(Math.PI / 4
				+ Math.toRadians(aLat) / 2)));
	}
}
