package com.kogasoftware.odt.invehicledevice.ui.modalview.navigation;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationManager;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.FloatMath;
import android.util.Log;
import android.view.WindowManager;

import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import com.javadocmd.simplelatlng.LatLng;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.event.EnterDrivePhaseEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.LocationReceivedEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.MapZoomLevelChangedEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.OrientationChangedEvent;
import com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.frametask.FrameTask;
import com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.frametask.MapBuildFrameTask;
import com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.frametask.NextPlatformFrameTask;
import com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.frametask.SelfFrameTask;
import com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.tilepipeline.TilePipeline;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.Platform;
import com.kogasoftware.odt.webapi.model.ServiceUnitStatusLog;

public class NavigationRenderer implements GLSurfaceView.Renderer {
	private static final String TAG = NavigationRenderer.class.getSimpleName();
	public static final int WORLD_WIDTH = 256;
	public static final int WORLD_HEIGHT = 256;
	public static final int MAX_ZOOM_LEVEL = 16;
	public static final int MIN_ZOOM_LEVEL = 9;

	public static PointF getPoint(LatLng latLng) {
		double x = latLng.getLongitude() * NavigationRenderer.WORLD_WIDTH
				/ 360d;
		double y = SphericalMercator.lat2y(latLng.getLatitude())
				* NavigationRenderer.WORLD_HEIGHT / 360d;
		return new PointF((float) x, (float) y);
	}

	public static PointF getDisplayPoint(LatLng latLng) {
		double x = 0;
		double y = 0;
		return new PointF((float) x, (float) y);
	}

	private final MotionSmoother rotationSmoother = new LazyMotionSmoother(
			500.0, 0.02, 0.00005);
	private final MotionSmoother latitudeSmoother = new SimpleMotionSmoother();
	private final MotionSmoother longitudeSmoother = new SimpleMotionSmoother();
	private final List<FrameTask> backgroundFrameTasks = new LinkedList<FrameTask>();
	private final List<FrameTask> frameTasks = new LinkedList<FrameTask>();
	private final Queue<FrameTask> addedFrameTasks = new ConcurrentLinkedQueue<FrameTask>();
	private final Queue<FrameTask> removedFrameTasks = new ConcurrentLinkedQueue<FrameTask>();
	private final NextPlatformFrameTask nextPlatformFrameTask;
	private final SelfFrameTask selfFrameTask;
	private final TilePipeline tilePipeline;

	private CommonLogic commonLogic = new CommonLogic();
	private long framesBy10seconds = 0l;
	private long lastReportMillis = 0l;
	private int width = 0;
	private int height = 0;
	private int zoomLevel = 12;

	private final AtomicReference<Optional<Integer>> syncNextZoomLevel = new AtomicReference<Optional<Integer>>(
			Optional.<Integer> absent()); // 描画中にzoomの値が変更されないようにするための変数
	private boolean autoZoomLevel = true; // 自動ズームするかどうか
	private final AtomicReference<Optional<Boolean>> syncNextAutoZoomLevel = new AtomicReference<Optional<Boolean>>(
			Optional.<Boolean> absent()); // 描画中にautoZoomの値が変更されないようにするための変数
	private final WindowManager windowManager;

	public NavigationRenderer(Context context, TilePipeline tilePipeline) {
		this.tilePipeline = tilePipeline;
		windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		tilePipeline.changeZoomLevel(zoomLevel);
		addedFrameTasks.add(new MapBuildFrameTask(context, tilePipeline));
		selfFrameTask = new SelfFrameTask(context.getResources());
		addedFrameTasks.add(selfFrameTask);
		nextPlatformFrameTask = new NextPlatformFrameTask(
				context.getResources());
		nextPlatformFrameTask.setLatLng(new LatLng(0, 0));
		addedFrameTasks.add(nextPlatformFrameTask);

		// 〒701-4302 岡山県瀬戸内市牛窓町牛窓３９１１−３７ (東備バス（株）)
		double defaultLatitude = 34.617781;
		double defaultLongitude = 134.161429;

		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		if (locationManager != null) {
			Location location = locationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (location != null
					&& !(location.getLatitude() == 0 && location.getLatitude() == 0)) {
				defaultLatitude = location.getLatitude();
				defaultLongitude = location.getLongitude();
			}
		}
		latitudeSmoother.addMotion(defaultLatitude);
		longitudeSmoother.addMotion(defaultLongitude);
	}

	/**
	 * 描画のために毎フレーム呼び出される
	 */
	@Override
	public void onDrawFrame(GL10 gl) {
		final long millis = System.currentTimeMillis();
		float cameraZoom = 2f;
		boolean zoomLevelChanged = false;

		// ズームを修正
		for (Integer nextZoomLevel : syncNextZoomLevel.getAndSet(
				Optional.<Integer> absent()).asSet()) {
			if (!nextZoomLevel.equals(zoomLevel)) {
				zoomLevel = nextZoomLevel;
				zoomLevelChanged = true;
			}
		}
		float totalZoom = cameraZoom * (1 << zoomLevel);

		// 自動ズームかどうかを修正
		for (Boolean nextAutoZoomLevel : syncNextAutoZoomLevel.getAndSet(
				Optional.<Boolean> absent()).asSet()) {
			if (!nextAutoZoomLevel.equals(autoZoomLevel)) {
				autoZoomLevel = nextAutoZoomLevel;
			}
		}

		// 現在地を取得
		LatLng selfLatLng = new LatLng(
				latitudeSmoother.getSmoothMotion(millis),
				longitudeSmoother.getSmoothMotion(millis));
		selfFrameTask.setLatLng(selfLatLng);
		// LatLng vehicleLatLng = new LatLng(35.707085, 139.771739);
		// LatLng vehicleLatLng = new LatLng(0, 0);
		LatLng centerLatLng = selfLatLng;
		PointF centerPoint = getPoint(centerLatLng);
		PointF selfPoint = getPoint(selfLatLng);

		// 現在の方向を取得
		float angle = (float) (-rotationSmoother.getSmoothMotion(millis));

		double pixelDistanceRate = 0.0;

		if (nextPlatformFrameTask.getLatLng().equals(new LatLng(0, 0))) {
			// 目的地が存在しない場合
		} else if (autoZoomLevel) {
			// 自動ズームが有効な場合
			centerPoint.y += height / 5.5 / totalZoom; // 中心を上に修正
			PointF nextPlatformPoint = getPoint(nextPlatformFrameTask
					.getLatLng());
			// 目的地が現在地より下にある場合、中心を下に修正して目的地が見やすいようにする
			float vehicleRY = selfPoint.x * FloatMath.sin(angle)
					+ selfPoint.y * FloatMath.cos(angle);
			float nextPlatformRY = nextPlatformPoint.x * FloatMath.sin(angle)
					+ nextPlatformPoint.y * FloatMath.cos(angle);

			boolean hasExtraY = false;
			try {
				// ArrayIndexOutOfBoundsExceptionが発生することがある。詳細はコミットログ参照。
				hasExtraY = (vehicleRY > nextPlatformRY);
			} catch (ArrayIndexOutOfBoundsException e) {
				Log.w(TAG, "vehicleRY=" + vehicleRY + ", nextPlatfomrRY="
						+ nextPlatformRY, e);
			}
			if (hasExtraY) {
				float extraY = Math.min(vehicleRY - nextPlatformRY,
						(float) height / 2 / totalZoom);
				centerPoint.y -= extraY;
			}

			// 現在地と目的地のピクセル距離を計算
			double dx = (selfPoint.x - nextPlatformPoint.x);
			double dy = (selfPoint.y - nextPlatformPoint.y);
			double pixelDistance = Math.sqrt(dx * dx + dy * dy) * totalZoom;
			// 縦横で短い方を基準にしたピクセル距離の割合を計算
			pixelDistanceRate = pixelDistance / Math.min(width, height);

			// ピクセル距離に応じてズームを修正
			if (pixelDistanceRate < 0.22) {
				// 近い場合、拡大する
				setZoomLevel(zoomLevel + 1);
			} else if (pixelDistanceRate > 0.55) {
				// 遠い場合、縮小する
				setZoomLevel(zoomLevel - 1);
			}
		} else {
			// 自動ズームが無効の場合
			centerLatLng = nextPlatformFrameTask.getLatLng();
			centerPoint = getPoint(centerLatLng);
			// 中心を自車方向にずらす
			double dy = selfPoint.y - centerPoint.y;
			double dx = selfPoint.x - centerPoint.x;
			if (dx != 0d && dy != 0d) {
				double rad = Math.atan2(dy, dx);
				double distance = Math.min(width, height) / 5.5 / totalZoom;
				centerPoint.x += Math.cos(rad) * distance;
				centerPoint.y += Math.sin(rad) * distance;
			}
		}

		// 表示状況を取得
		framesBy10seconds++;
		if (millis - lastReportMillis > 10 * 1000) {
			Log.d(TAG, "onDrawFrame() fps=" + (double) framesBy10seconds / 10
					+ ", lat=" + selfLatLng.getLatitude() + ", lon="
					+ selfLatLng.getLongitude() + ", zoom=" + zoomLevel
					+ ", angle=" + angle + ", center=(" + centerPoint.x + ","
					+ centerPoint.y + ") pdr=" + pixelDistanceRate);
			framesBy10seconds = 0l;
			lastReportMillis = millis;
		}

		// 描画用の情報を作成
		FrameState frameState = new FrameState(gl, millis, angle, centerLatLng,
				zoomLevel, addedFrameTasks, removedFrameTasks, cameraZoom,
				width, height);

		// 追加予約、削除予約されているFrameTaskをリストに追加削除
		while (!addedFrameTasks.isEmpty() || !removedFrameTasks.isEmpty()) {
			FrameTask newFrameTask = addedFrameTasks.poll();
			if (newFrameTask != null) {
				newFrameTask.onAdd(frameState);
				if (newFrameTask.getClass().isAnnotationPresent(
						FrameTask.Background.class)) {
					backgroundFrameTasks.add(newFrameTask);
				} else {
					frameTasks.add(newFrameTask);
				}
			}
			FrameTask deleteFrameTask = removedFrameTasks.poll();
			if (deleteFrameTask != null) {
				deleteFrameTask.onRemove(frameState);
				backgroundFrameTasks.remove(deleteFrameTask);
			}
		}

		// 描画用バッファをクリア
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		// 射影行列を現在地にあわせて修正
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();

		// 平行投影用のパラメータをセット
		float left = -width / 2f / totalZoom + centerPoint.x;
		float right = width / 2f / totalZoom + centerPoint.x;
		float bottom = -height / 2f / totalZoom + centerPoint.y;
		float top = height / 2f / totalZoom + centerPoint.y;
		GLU.gluOrtho2D(gl, left, right, bottom, top);

		// モデル変換行列
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();

		if (autoZoomLevel) {
			// 自分自身を中心に全体を回転する
			gl.glTranslatef(selfPoint.x, selfPoint.y, 0);
			gl.glRotatef((float) Math.toDegrees(angle), 0, 0, 1);
			gl.glTranslatef(-selfPoint.x, -selfPoint.y, 0);
		} else {
			// 目的地を中心に全体を回転する
			gl.glTranslatef(centerPoint.x, centerPoint.y, 0);
			gl.glRotatef((float) Math.toDegrees(angle), 0, 0, 1);
			gl.glTranslatef(-centerPoint.x, -centerPoint.y, 0);
		}

		// FrameTaskをひとつずつ描画
		for (FrameTask frameTask : backgroundFrameTasks) {
			if (zoomLevelChanged) {
				frameTask.onChangeZoom(frameState);
			}
			frameTask.onDraw(frameState);
		}
		for (FrameTask frameTask : frameTasks) {
			if (zoomLevelChanged) {
				frameTask.onChangeZoom(frameState);
			}
			frameTask.onDraw(frameState);
		}
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
		this.width = width;
		this.height = height;

		Log.i(TAG, "onSurfaceChanged() width=" + width + " height=" + height);

		// ビューポートをサイズに合わせてセットしなおす
		gl.glViewport(0, 0, width, height);
	}

	/**
	 * サーフェスが生成される際・または再生成される際に呼び出される
	 */
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		Log.i(TAG, "onSurfaceCreated()");

		// ディザを無効化
		gl.glDisable(GL10.GL_DITHER);
		// カラーとテクスチャ座標の補間精度を、最も効率的なものに指定
		// gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
		// バッファ初期化時のカラー情報をセット
		gl.glClearColor(1, 1, 1, 1);
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
		// ブレンディングを有効化
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		// 2Dテクスチャを有効に
		gl.glEnable(GL10.GL_TEXTURE_2D);
		// 頂点配列を使うことを宣言
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		// テクスチャ座標配列を使うことを宣言
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		// テクスチャの透明度の合成を有効にする
		gl.glTexEnvx(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
				GL10.GL_MODULATE);
	}

	/**
	 * 現在地を修正する
	 */
	@Subscribe
	public void changeLocation(LocationReceivedEvent event) {
		changeLocation();
	}

	public void changeLocation() {
		ServiceUnitStatusLog serviceUnitStatusLog = commonLogic
				.getServiceUnitStatusLog();
		double latitude = serviceUnitStatusLog.getLatitude().doubleValue();
		double longitude = serviceUnitStatusLog.getLongitude().doubleValue();
		if (latitude == 0 && longitude == 0) {
			return;
		}
		Log.i(TAG, "changeLocation lat=" + latitude + ", lon=" + longitude);
		long millis = System.currentTimeMillis();
		latitudeSmoother.addMotion(latitude, millis);
		longitudeSmoother.addMotion(longitude, millis);
	}

	/**
	 * 現在の方向を修正する
	 */
	@Subscribe
	public void changeOrientation(OrientationChangedEvent event) {
		changeOrientation(Math.toRadians(event.orientationDegree));
	}

	public void changeOrientation(double rad) {
		double from = rotationSmoother.getSmoothMotion();
		double to = Utility.getNearestRadian(from, rad);
		Log.v(TAG, "changeOrientation got=" + rad + " from=" + from + " to="
				+ to);
		rotationSmoother.addMotion(to);
	}

	@Subscribe
	public void setNextPlatform(EnterDrivePhaseEvent e) {
		setNextPlatform();
	}

	public void setNextPlatform() {
		for (OperationSchedule operationSchedule : commonLogic
				.getCurrentOperationSchedule().asSet()) {
			for (Platform platform : operationSchedule.getPlatform().asSet()) {
				nextPlatformFrameTask.setLatLng(new LatLng(platform
						.getLatitude().doubleValue(), platform.getLongitude()
						.doubleValue()));
			}
		}
	}

	public void setCommonLogic(CommonLogic commonLogic) {
		this.commonLogic = commonLogic;
		tilePipeline.setCommonLogic(commonLogic);
		setNextPlatform();
		changeLocation();
	}

	public void setZoomLevel(int newZoomLevel) {
		if (newZoomLevel > MAX_ZOOM_LEVEL || newZoomLevel < MIN_ZOOM_LEVEL
				|| zoomLevel == newZoomLevel) {
			return;
		}
		commonLogic.postEvent(new MapZoomLevelChangedEvent(newZoomLevel));
		syncNextZoomLevel.set(Optional.of(newZoomLevel));
	}

	public void setAutoZoomLevel(boolean newAutoZoomLevel) {
		syncNextAutoZoomLevel.set(Optional.of(newAutoZoomLevel));
	}

	public void onResumeActivity() {
	}

	public void onPauseActivity() {
	}
}
