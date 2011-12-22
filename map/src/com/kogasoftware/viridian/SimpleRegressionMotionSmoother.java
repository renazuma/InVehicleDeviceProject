package com.kogasoftware.viridian;


/**
 * 一次の最小二乗法を使って回転をスムーズにする
 * 
 * @author ksc
 * 
 */
public class SimpleRegressionMotionSmoother extends MotionSmoother {
	@Override
	protected void calculateAndAddMotion(Double orientation, Long millis) {
	}

	@Override
	protected Double calculateAndGetSmoothMotion(Long millis) {
		return 0.0;
	}
	//	static private class Point {
	//		final private Double degree;
	//		final private Double millis;
	//
	//		public Point(Double degree, Double millis) {
	//			this.degree = degree;
	//			this.millis = millis;
	//		}
	//	}
	//
	//	private final LinkedList<Point> list = new LinkedList<Point>();
	//
	//	public static final Integer MAX_LIST_SIZE = 10;
	//	public static final Float MAX_MILLIS_GAP = 15000f;
	//
	//	@Override
	//	protected void calculateAndAddDegree(Float degree, Long millis) {
	//		synchronized (list) {
	//			list.add(new Point((double) degree, (double) millis));
	//			while (list.size() > MAX_LIST_SIZE) {
	//				list.poll();
	//			}
	//			for (Point point : list) {
	//				if (point.millis + MAX_MILLIS_GAP < millis) {
	//					list.remove(point);
	//				} else {
	//					break;
	//				}
	//			}
	//		}
	//	}
	//
	//	@Override
	//	protected Float calculateAndGetDegree(Long millis) {
	//		SimpleRegression regression = new SimpleRegression();
	//		synchronized (list) {
	//			for (Point point : list) {
	//				regression.addData(point.millis, point.degree);
	//			}
	//		}
	//		Float result = (float) regression.predict(millis);
	//		if (result.isNaN()) {
	//			return 0.0f;
	//		}
	//		return result;
	//	}
}
