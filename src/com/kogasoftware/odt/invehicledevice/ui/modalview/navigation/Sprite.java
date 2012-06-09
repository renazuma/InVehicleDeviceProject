package com.kogasoftware.odt.invehicledevice.ui.modalview.navigation;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import com.google.common.math.DoubleMath;
import com.javadocmd.simplelatlng.LatLng;

public abstract class Sprite extends FrameTask {
	/**
	 * Sprite.draw() メソッドで描画対象をどこに描くかを指定するため，引数として渡すクラス
	 */
	public static class DrawParams {
		public Boolean useLatLng = false;

		public final FrameState frameState;
		public Double x = 0d;
		public Double y = 0d;
		public LatLng latLng = new LatLng(0, 0);
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

		public DrawParams latLng(LatLng latLng) {
			this.latLng = latLng;
			useLatLng = true;
			return this;
		}

		public DrawParams scale(Double scale) {
			this.scale = scale;
			return this;
		}

		public DrawParams x(Double x) {
			this.x = x;
			useLatLng = false;
			return this;
		}

		public DrawParams y(Double y) {
			this.y = y;
			useLatLng = false;
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
		if (drawParams.useLatLng) {
			draw(drawParams.frameState, drawParams.latLng, drawParams.angle,
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

	public void draw(FrameState frameState, LatLng latLng) {
		draw(frameState, latLng, 0d);
	}

	public void draw(FrameState frameState, LatLng latLng, Double angle) {
		draw(frameState, latLng, angle, 1d);
	}

	public void draw(FrameState frameState, LatLng latLng, Double angle,
			Double scale) {
		draw(frameState, latLng, angle, scale, 1d);
	}

	public void draw(FrameState frameState, LatLng latLng, Double angle,
			Double scale, Double alpha) {

		PointF point = frameState.convertLatLngToPointF(latLng);
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
			Texture.update(gl, textureId, bitmap);
		} else {
			bitmapWidth = alignedLength;
			bitmapHeight = alignedLength;
			Bitmap alignedBitmap = Bitmap.createBitmap(bitmapWidth,
					bitmapHeight, Bitmap.Config.ARGB_8888);
			Float left = (float) (bitmapWidth - originalBitmapWidth) / 2;
			Float top = (float) (bitmapHeight - originalBitmapHeight) / 2;
			new Canvas(alignedBitmap)
					.drawBitmap(bitmap, left, top, new Paint());
			Texture.update(gl, textureId, alignedBitmap);
			alignedBitmap.recycle();
		}
		bitmap = null; // 明示的に参照を外す。TODO recycle()を検討
	}

	public void onDispose(GL10 gl) {
		// bitmap.recycle();
		Texture.delete(gl, textureId);
	}

	protected void setBitmap(Bitmap newBitmap) {
		if (bitmap != null) {
			bitmap.recycle();
		}
		bitmap = newBitmap;
	}
}
