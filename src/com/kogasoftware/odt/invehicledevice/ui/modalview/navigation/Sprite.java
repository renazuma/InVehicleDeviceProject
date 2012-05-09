package com.kogasoftware.odt.invehicledevice.navigation;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import com.google.android.maps.GeoPoint;
import com.google.common.math.DoubleMath;

public abstract class Sprite extends FrameTask {
	/**
	 * Sprite.draw() メソッドで描画対象をどこに描くかを指定するため，引数として渡すクラス
	 * 
	 * @author ksc
	 * 
	 */
	public static class DrawParams {
		public Boolean useGeoPoint = false;

		public final FrameState frameState;
		public Double x = 0d;
		public Double y = 0d;
		public GeoPoint geoPoint = new GeoPoint(0, 0);
		public Double angle = 0d;
		public Double scale = 1d;
		public Double alpha = 1d;

		public DrawParams(FrameState frameState) {
			this.frameState = frameState;
		}

		public DrawParams alpha(Double alpha) {
			this.alpha = alpha;
			return this;
		}

		public DrawParams angle(Double angle) {
			this.angle = angle;
			return this;
		}

		public DrawParams geoPoint(GeoPoint geoPoint) {
			this.geoPoint = geoPoint;
			useGeoPoint = true;
			return this;
		}

		public DrawParams scale(Double scale) {
			this.scale = scale;
			return this;
		}

		public DrawParams x(Double x) {
			this.x = x;
			useGeoPoint = false;
			return this;
		}

		public DrawParams y(Double y) {
			this.y = y;
			useGeoPoint = false;
			return this;
		}
	}

	protected Bitmap bitmap = null;
	protected Integer textureId = null;
	protected Integer originalBitmapWidth = 0;
	protected Integer originalBitmapHeight = 0;
	protected Integer bitmapWidth = 0;
	protected Integer bitmapHeight = 0;

	public void draw(DrawParams drawParams) {
		if (drawParams.useGeoPoint) {
			draw(drawParams.frameState, drawParams.geoPoint, drawParams.angle,
					drawParams.scale, drawParams.alpha);
		} else {
			draw(drawParams.frameState, drawParams.x, drawParams.y,
					drawParams.angle, drawParams.scale, drawParams.alpha);
		}
	}

	public void draw(FrameState frameState, Double x, Double y) {
		draw(frameState, x, y, 0d);
	}

	public void draw(FrameState frameState, Double x, Double y, Double angle) {
		draw(frameState, x, y, angle, 1d, 1d);
	}

	public void draw(FrameState frameState, Double x, Double y, Double angle,
			Double scale) {
		draw(frameState, x, y, angle, scale, 1d);
	}

	/**
	 * ビットマップをOpenGLに描画する
	 * 
	 * @param gl
	 * @param x
	 * @param y
	 * @param angle
	 * @param scale
	 */
	public void draw(FrameState frameState, Double x, Double y, Double angle,
			Double scale, Double alpha) {
		loadBitmap(frameState.getGL());
		Texture.draw(frameState.getGL(), textureId, x.floatValue(),
				y.floatValue(), bitmapWidth, bitmapHeight,
				(float) Math.toDegrees(angle), scale.floatValue(),
				scale.floatValue(), alpha.floatValue());
	}

	public void draw(FrameState frameState, GeoPoint geoPoint) {
		draw(frameState, geoPoint, 0d);
	}

	public void draw(FrameState frameState, GeoPoint geoPoint, Double angle) {
		draw(frameState, geoPoint, angle, 1d);
	}

	public void draw(FrameState frameState, GeoPoint geoPoint, Double angle,
			Double scale) {
		draw(frameState, geoPoint, angle, scale, 1d);
	}

	public void draw(FrameState frameState, GeoPoint geoPoint, Double angle,
			Double scale, Double alpha) {

		PointF point = frameState.convertGeoPointToPointF(geoPoint);
		draw(frameState, (double) point.x, (double) point.y, angle, scale,
				alpha);
	}

	/**
	 * setBitmapで準備されたビットマップをOpenGLにロードする
	 * 
	 * @param gl
	 */
	public void loadBitmap(GL10 gl) {
		if (textureId == null) {
			textureId = Texture.generate(gl);
		}
		if (bitmap == null) {
			return;
		}
		originalBitmapWidth = bitmap.getWidth();
		originalBitmapHeight = bitmap.getHeight();
		Integer alignedLength = (int) Math.pow(2, Math.ceil(DoubleMath
				.log2(Math.max(originalBitmapWidth, originalBitmapHeight))));
		if (bitmapWidth.equals(alignedLength)
				&& bitmapHeight.equals(alignedLength)) {
			Texture.update(gl, bitmap, textureId);
		} else {
			bitmapWidth = alignedLength;
			bitmapHeight = alignedLength;
			Bitmap alignedBitmap = Bitmap.createBitmap(bitmapWidth,
					bitmapHeight, Bitmap.Config.ARGB_8888);
			Float left = (float) (bitmapWidth - originalBitmapWidth) / 2;
			Float top = (float) (bitmapHeight - originalBitmapHeight) / 2;
			new Canvas(alignedBitmap)
			.drawBitmap(bitmap, left, top, new Paint());
			Texture.update(gl, alignedBitmap, textureId);
			alignedBitmap.recycle();
		}
		bitmap = null; // 明示的に参照を外す。TODO recycle()を検討
	}

	public void onDispose(GL10 gl) {
		Texture.delete(gl, textureId);
	}

	protected void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
}
