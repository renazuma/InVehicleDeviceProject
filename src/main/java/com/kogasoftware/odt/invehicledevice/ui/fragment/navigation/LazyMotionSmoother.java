package com.kogasoftware.odt.invehicledevice.ui.fragment.navigation;

/**
 * 遅延を許して回転をスムーズにする
 * 
 * @author ksc
 * 
 */
public class LazyMotionSmoother extends MotionSmoother {
	// private static final String TAG =
	// LazyMotionSmoother.class.getSimpleName();
	private volatile long lastMillis = 0l;
	private volatile double startOrientation = 0.0;
	private volatile double targetOrientation = 0.0;
	private final double latency;
	private final double maxVelocity;
	private final double minVelocity;

	public LazyMotionSmoother() {
		this(300.0, Double.MAX_VALUE, 0.0, 0.0);
	}

	public LazyMotionSmoother(double latency) {
		this(latency, Double.MAX_VALUE, 0.0, 0.0);
	}

	public LazyMotionSmoother(double latency, double maxVelocity,
			double minVelocity, double defaultOrientation) {
		this.maxVelocity = maxVelocity;
		this.minVelocity = minVelocity;
		this.latency = latency;
		this.startOrientation = defaultOrientation;
		this.targetOrientation = defaultOrientation;
	}

	@Override
	protected void calculateAndAddMotion(double orientation, long millis) {
		if (lastMillis > millis) {
			return;
		}
		// メンバの更新
		startOrientation = getSmoothMotion(millis);
		targetOrientation = orientation;
		lastMillis = millis;
	}

	@Override
	protected double calculateAndGetSmoothMotion(long millis) {
		double result = 0.0;
		double elapsed = (millis - lastMillis);
		if (elapsed <= 0.0) {
			return startOrientation;
		}
		if (elapsed > latency) {
			elapsed = latency;
		}
		// 現在の角度の差
		double diffAngle = targetOrientation - startOrientation;
		// 角速度
		double angularVelocity = diffAngle / latency;
		// 角速度を丸める
		if (Math.abs(angularVelocity) > maxVelocity) {
			angularVelocity = maxVelocity * (angularVelocity > 0 ? 1 : -1);
		}
		if (Math.abs(angularVelocity) < minVelocity) {
			angularVelocity = minVelocity * (angularVelocity > 0 ? 1 : -1);
		}
		// 追加する角度
		double extraAngle = angularVelocity * elapsed;
		if (Math.abs(extraAngle) > Math.abs(diffAngle)) {
			// 追加する角度が現在の差より大きい場合
			result = targetOrientation;
		} else {
			result = startOrientation + extraAngle;
		}

		// if (BuildConfig.DEBUG) {
		// String f = String
		// .format("diff=% 3dms, v=% 8.3f, add=% 8.3f, result=% 8.3f, target=% 8.3f",
		// millis - lastMillis, angularVelocity, extraAngle,
		// result, targetOrientation);
		// Log.v(TAG, f);
		// }
		return result;
	}
}
