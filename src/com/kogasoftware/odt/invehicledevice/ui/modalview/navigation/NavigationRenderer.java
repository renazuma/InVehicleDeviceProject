package com.kogasoftware.odt.invehicledevice.ui.modalview.navigation;

import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.Log;

import com.google.common.eventbus.Subscribe;
import com.google.common.io.Closeables;
import com.javadocmd.simplelatlng.LatLng;
import com.kogasoftware.odt.invehicledevice.logic.event.LocationReceivedEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.OrientationChangedEvent;

public class NavigationRenderer implements GLSurfaceView.Renderer {
	private static final String TAG = NavigationRenderer.class.getSimpleName();
	private final Queue<FrameTask> frameTaskQueue = new LinkedList<FrameTask>();
	private final MotionSmoother rotationSmoother = new LazyMotionSmoother(
			500.0, 0.02, 0.00005);
	private final MotionSmoother latitudeSmoother = new LazyMotionSmoother(
			500.0, 0.02, 0.00005);
	private final MotionSmoother longitudeSmoother = new LazyMotionSmoother(
			500.0, 0.02, 0.00005);
	private final LatLng currentLatLng = new LatLng(0, 0);
	private long framesBy10seconds = 0l;
	private long lastReportMillis = 0l;

	public NavigationRenderer(Resources resources) {
		frameTaskQueue.add(new GeoPointDroidSprite(resources, new LatLng(
				35.899975, 139.935788)));
		frameTaskQueue.add(new MyLocationSprite(resources));
	}

	private static Bitmap getBitmapResource(Context context, int id) {
		InputStream inputStream = context.getResources().openRawResource(id);
		Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
		Closeables.closeQuietly(inputStream);
		return bitmap;
	}

	/**
	 * 描画のために毎フレーム呼び出される
	 */
	@Override
	public void onDrawFrame(GL10 gl) {
		// フレームレートの計算
		final long millis = System.currentTimeMillis();
		framesBy10seconds++;
		if (millis - lastReportMillis > 10000) {
			Log.i(TAG, "onDrawFrame thread=" + Thread.currentThread().getId()
					+ ", fps=" + (double) framesBy10seconds / 10);
			framesBy10seconds = 0l;
			lastReportMillis = millis;
		}
		// 描画用バッファをクリア
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		// 現在の方向を取得
		double radian = Utility.getNearestRadian(0.0, 2 * Math.PI
				- rotationSmoother.getSmoothMotion());

		// 現在地を取得
		double latitude = latitudeSmoother.getSmoothMotion(millis);
		double longitude = longitudeSmoother.getSmoothMotion(millis);
		currentLatLng.setLatitudeLongitude(latitude, longitude);

		FrameState frameState = new FrameState(gl, millis, radian,
				currentLatLng);
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

		frameTaskQueue.addAll(frameState.getNewFrameTasks());
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
		Log.i(TAG, "onSurfaceChanged() thread="
				+ Thread.currentThread().getId() + " width=" + width
				+ " height=" + height);

		// ビューポートをサイズに合わせてセットしなおす
		gl.glViewport(0, 0, width, height);
		// 射影行列を選択
		gl.glMatrixMode(GL10.GL_PROJECTION);
		// 現在選択されている行列(射影行列)に、単位行列をセット
		gl.glLoadIdentity();

		// 平行投影用のパラメータをセット
		GLU.gluOrtho2D(gl, -width / 2f, width / 2f, -height / 2f, height / 2f);

		// カメラの位置をセット
		// Float eyeX = 0f;
		// Float eyeY = 0f;
		// Float eyeZ = 1f;
		// Float centerX = 0f;
		// Float centerY = 0f;
		// Float centerZ = 0f;
		// Float upX = 0f;
		// Float upY = 1f;
		// Float upZ = 0f;
		// GLU.gluLookAt(gl, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX,
		// upY, upZ);

		// zoom = (width + height)
		// / (Math.sqrt(2) * Math.min(MapRenderer.MAP_TEXTURE_HEIGHT,
		// MapRenderer.MAP_TEXTURE_WIDTH));
		// zoom *= 1.05; // TODO 角の黒い部分がはみ出すのを微調整している係数
	}

	/**
	 * サーフェスが生成される際・または再生成される際に呼び出される
	 */
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		Log.i(TAG, "onSurfaceCreated() thread="
				+ Thread.currentThread().getId());
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

	/**
	 * 現在地を修正する
	 */
	@Subscribe
	public void changeLocation(LocationReceivedEvent event) {
		Log.i(TAG, "changeLocation " + event.location);
		long millis = System.currentTimeMillis();
		latitudeSmoother.addMotion(event.location.getLatitude(), millis);
		longitudeSmoother.addMotion(event.location.getLongitude(), millis);
	}

	/**
	 * 現在の方向を修正する
	 */
	@Subscribe
	public void changeOrientation(OrientationChangedEvent event) {
		Log.i(TAG, "changeOrientation " + event.orientationDegree);
		rotationSmoother.addMotion(Math.toRadians(event.orientationDegree));
	}
}
