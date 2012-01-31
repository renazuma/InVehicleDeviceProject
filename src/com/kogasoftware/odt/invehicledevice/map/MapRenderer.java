package com.kogasoftware.odt.invehicledevice.map;

import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.common.io.Closeables;
import com.kogasoftware.odt.invehicledevice.LogTag;

/**
 * Mapを表示する。全てのpublicメソッドはsynchronized(this)でジャイアントロックを行う
 * 
 * @author ksc
 * 
 */
public class MapRenderer implements GLSurfaceView.Renderer {
	public static final Integer MAP_TEXTURE_HEIGHT = 512;
	public static final Integer MAP_TEXTURE_WIDTH = 512;

	private static final String T = LogTag.get(MapRenderer.class);

	public static Bitmap getBitmapResource(Context context, int id) {
		InputStream inputStream = context.getResources().openRawResource(id);
		Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
		Closeables.closeQuietly(inputStream);
		return bitmap;
	}

	private final MapSprite mapSprite;
	private final MapSynchronizer bitmapSynchronizer;
	private final MapView mapView;
	private final Queue<FrameTask> frameTaskQueue = new LinkedList<FrameTask>();
	private final MotionSmoother rotationSmoother = new LazyMotionSmoother(
			500.0, 0.02, 0.00005);

	private Long framesBy10s = 0l;
	private Integer width = 0;
	private Integer height = 0;
	private Integer parentWidth = 0;
	private Integer parentHeight = 0;
	private Long lastReportMillis = 0l;
	private Boolean layoutChanged = false;
	private Double zoom = 1.0;
	private Long bitmapLastUpdated = 0l;
	private GeoPoint lastMapCenter = new GeoPoint(0, 0);

	public void addFrameTask(FrameTask newFrameTask) {
		synchronized (this) {
			newFrameTask.onAdd(frameTaskQueue);
			frameTaskQueue.add(newFrameTask);
		}
	}

	public MapRenderer(Context context, MapSynchronizer bitmapSynchronizer,
			MapView mapView) {
		synchronized (this) {
			this.mapView = mapView;
			this.bitmapSynchronizer = bitmapSynchronizer;
			mapSprite = new MapSprite(context);
			frameTaskQueue.add(mapSprite);
			frameTaskQueue.add(new GeoPointDroidSprite(context, new GeoPoint(
					35703365, 139770004)));
			frameTaskQueue.add(new MyLocationSprite(context));
		}
	}

	/**
	 * 描画のために毎フレーム呼び出される
	 */
	@Override
	public void onDrawFrame(final GL10 gl) {
		synchronized (this) {
			if (layoutChanged) {
				layoutChanged = false;
				onLayoutChanged(gl);
			}

			// fpsの計算
			final Long millis = System.currentTimeMillis();
			framesBy10s++;
			if (millis - lastReportMillis > 10000) {
				Log.d(T, "fps=" + (double) framesBy10s / 10);
				framesBy10s = 0l;
				lastReportMillis = millis;
			}

			// 描画用バッファをクリア
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

			final Double radian = Utility.getNearestRadian(0.0, 2 * Math.PI
					- rotationSmoother.getSmoothMotion());

			// 地図データの読み取り
			bitmapSynchronizer.read(new MapSynchronizer.Accessor() {
				@Override
				public void run(MapSnapshot mapSnapshot) {
					if (bitmapSynchronizer.isDirty()
							|| millis > bitmapLastUpdated + 2000 /* TODO */) {
						bitmapLastUpdated = millis;
						mapSprite.setBitmap(mapSnapshot.bitmap);
						mapSprite.loadBitmap(gl);
						GeoPoint center = mapSnapshot.center;
						lastMapCenter = new GeoPoint(center.getLatitudeE6(),
								center.getLongitudeE6());
					}
				}
			});

			FrameState frameState = new FrameState(gl, millis, radian,
					lastMapCenter, zoom, mapView);

			for (Iterator<FrameTask> iterator = frameTaskQueue.iterator(); iterator
					.hasNext();) {
				FrameTask frameTask = iterator.next();
				if (frameTask.isRemoved()) {
					frameTask.onDispose(frameState);
					iterator.remove();
				} else {
					frameTask.onDraw(frameState);
				}
			}
		}

	}

	private void onLayoutChanged(GL10 gl) {
		// ビューポートをサイズに合わせてセットしなおす
		gl.glViewport(parentWidth - width, parentHeight - height, width, height);
		// 射影行列を選択
		gl.glMatrixMode(GL10.GL_PROJECTION);
		// 現在選択されている行列(射影行列)に、単位行列をセット
		gl.glLoadIdentity();

		// 平行投影用のパラメータをセット
		// GLU.gluOrtho2D(gl, 0.0f, width, 0.0f, height);

		// カメラの位置をセット
		float eyeX = 0;
		float eyeY = 0;
		float eyeZ = 1024; // TODO 定数
		float centerX = 0;
		float centerY = 0;
		float centerZ = 0;
		float upX = 0;
		float upY = 1;
		float upZ = 0;

		// 透視投影用の錐台のパラメーターにセット
		float zNear = 512; // TODO 定数
		float zFar = 1024 + 512; // TODO 定数

		float left = -(float) width / 2 / eyeZ * zNear;
		float right = (float) width / 2 / eyeZ * zNear;
		float bottom = -(float) height / 2 / eyeZ * zNear;
		float top = (float) height / 2 / eyeZ * zNear;

		gl.glFrustumf(left, right, bottom, top, zNear, zFar);
		GLU.gluLookAt(gl, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX,
				upY, upZ);

		float l = (MapRenderer.MAP_TEXTURE_HEIGHT < MapRenderer.MAP_TEXTURE_WIDTH) ? MapRenderer.MAP_TEXTURE_HEIGHT
				: MapRenderer.MAP_TEXTURE_WIDTH;
		zoom = (width + height) / (Math.sqrt(2) * l);
		zoom *= 1.05; // TODO
	}

	/**
	 * @Override サーフェイスのサイズ変更時に呼び出される
	 * @param gl
	 * @param width
	 *            変更後の幅
	 * @param height
	 *            変更後の高さ
	 */
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		synchronized (this) {
			this.width = width;
			this.height = height;
			layoutChanged = true;
		}
	}

	/**
	 * @Override サーフェイスが生成される際・または再生成される際に呼び出される
	 */
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		synchronized (this) {

			// ディザを無効化
			gl.glDisable(GL10.GL_DITHER);
			// カラーとテクスチャ座標の補間精度を、最も効率的なものに指定
			// gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
			gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
			// バッファ初期化時のカラー情報をセット
			gl.glClearColor(0, 0, 0, 1);
			// 片面表示を有効に
			gl.glEnable(GL10.GL_CULL_FACE);
			// カリング設定をCCWに
			gl.glFrontFace(GL10.GL_CCW);
			// 深度テスト
			gl.glDisable(GL10.GL_DEPTH_TEST);
			// gl.glEnable(GL10.GL_DEPTH_TEST);
			// フラットシェーディングにセット
			gl.glShadeModel(GL10.GL_FLAT);
			// gl.glShadeModel(GL10.GL_SMOOTH);
		}
	}

	public void setOrientation(Double radian) {
		synchronized (this) {
			rotationSmoother.addMotion(radian);
		}
	}

	public Point getLayout() {
		synchronized (this) {
			return new Point(width, height);
		}
	}

	public void setLayout(Integer width, Integer height, Integer parentWidth,
			Integer parentHeight) {
		synchronized (this) {
			this.width = width;
			this.height = height;
			this.parentWidth = parentWidth;
			this.parentHeight = parentHeight;
			layoutChanged = true;
		}
	}
}
