package com.kogasoftware.odt.invehicledevice.ui.fragment.navigation;

import java.lang.ref.WeakReference;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationManager;
import android.opengl.GLException;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.Handler;
import android.util.FloatMath;
import android.util.Log;

import com.google.common.base.Optional;
import com.javadocmd.simplelatlng.LatLng;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Platform;
import com.kogasoftware.odt.invehicledevice.apiclient.model.ServiceUnitStatusLog;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.logic.OperationScheduleLogic;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.logic.ServiceUnitStatusLogLogic;
import com.kogasoftware.odt.invehicledevice.ui.fragment.navigation.frametask.FrameTask;
import com.kogasoftware.odt.invehicledevice.ui.fragment.navigation.frametask.MapBuildFrameTask;
import com.kogasoftware.odt.invehicledevice.ui.fragment.navigation.frametask.NextPlatformFrameTask;
import com.kogasoftware.odt.invehicledevice.ui.fragment.navigation.frametask.SelfFrameTask;
import com.kogasoftware.odt.invehicledevice.ui.frametask.navigation.tilepipeline.TilePipeline;

public class NavigationRenderer implements GLSurfaceView.Renderer {
	public interface OnChangeMapZoomLevelListener {
		void onChangeMapZoomLevel(int zoomLevel);
	}

	private static final String TAG = NavigationRenderer.class.getSimpleName();

	public static final int WORLD_WIDTH = 256;
	public static final int WORLD_HEIGHT = 256;
	public static final int MAX_ZOOM_LEVEL = 16;
	public static final int MIN_ZOOM_LEVEL = 9;

	public static PointF getDisplayPoint(LatLng latLng) {
		double x = 0;
		double y = 0;
		return new PointF((float) x, (float) y);
	}

	public static PointF getPoint(LatLng latLng) {
		double x = latLng.getLongitude() * NavigationRenderer.WORLD_WIDTH
				/ 360d;
		double y = SphericalMercator.lat2y(latLng.getLatitude())
				* NavigationRenderer.WORLD_HEIGHT / 360d;
		return new PointF((float) x, (float) y);
	}

	protected final Set<OnChangeMapZoomLevelListener> onChangeMapZoomLevelListeners = new CopyOnWriteArraySet<OnChangeMapZoomLevelListener>();
	protected final MotionSmoother rotationSmoother;
	protected final MotionSmoother latitudeSmoother = new SimpleMotionSmoother();
	protected final MotionSmoother longitudeSmoother = new SimpleMotionSmoother();
	protected final List<FrameTask> backgroundFrameTasks = new LinkedList<FrameTask>();
	protected final List<FrameTask> frameTasks = new LinkedList<FrameTask>();
	protected final Queue<FrameTask> addedFrameTasks = new ConcurrentLinkedQueue<FrameTask>();
	protected final Queue<FrameTask> removedFrameTasks = new ConcurrentLinkedQueue<FrameTask>();
	protected final NextPlatformFrameTask nextPlatformFrameTask;
	protected final SelfFrameTask selfFrameTask;
	protected final OperationScheduleLogic operationScheduleLogic;
	protected final ServiceUnitStatusLogLogic serviceUnitStatusLogLogic;
	protected WeakReference<GLSurfaceView> glSurfaceViewWeakReference = new WeakReference<GLSurfaceView>(
			null);
	protected long framesBy10seconds = 0l;
	protected long lastReportMillis = 0l;
	protected int width = 0;
	protected int height = 0;
	protected int zoomLevel = 12;

	protected final AtomicReference<Optional<Integer>> syncNextZoomLevel = new AtomicReference<Optional<Integer>>(
			Optional.<Integer> absent()); // 描画中にzoomの値が変更されないようにするための変数
	protected boolean autoZoomLevel = true; // 自動ズームするかどうか
	protected final AtomicReference<Optional<Boolean>> syncNextAutoZoomLevel = new AtomicReference<Optional<Boolean>>(
			Optional.<Boolean> absent()); // 描画中にautoZoomの値が変更されないようにするための変数
	protected final InVehicleDeviceService service;
	protected final Handler uiHandler;

	public NavigationRenderer(InVehicleDeviceService service,
			TilePipeline tilePipeline, Handler uiHandler,
			Optional<OperationSchedule> optionalOperationSchedule,
			Double orientationDegree) {
		this.service = service;
		this.uiHandler = uiHandler;
		rotationSmoother = new LazyMotionSmoother(500.0, 0.02, 0.00005,
				Math.toRadians(orientationDegree));

		operationScheduleLogic = new OperationScheduleLogic(service);
		serviceUnitStatusLogLogic = new ServiceUnitStatusLogLogic(service);
		tilePipeline.changeZoomLevel(zoomLevel);
		addedFrameTasks.add(new MapBuildFrameTask(service, tilePipeline));
		selfFrameTask = new SelfFrameTask(service.getResources());
		addedFrameTasks.add(selfFrameTask);
		nextPlatformFrameTask = new NextPlatformFrameTask(
				service.getResources());
		nextPlatformFrameTask.setLatLng(new LatLng(0, 0));
		addedFrameTasks.add(nextPlatformFrameTask);

		// 〒701-4302 岡山県瀬戸内市牛窓町牛窓３９１１−３７ (東備バス（株）)
		double defaultLatitude = 34.617781;
		double defaultLongitude = 134.161429;

		LocationManager locationManager = (LocationManager) service
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
		updatePlatform(optionalOperationSchedule);
	}

	public void addOnChangeMapZoomLevelListener(
			OnChangeMapZoomLevelListener listener) {
		onChangeMapZoomLevelListeners.add(listener);
	}

	public void changeOrientation(Double orientationDegree) {
		double rad = Math.toRadians(orientationDegree);
		double from = rotationSmoother.getSmoothMotion();
		double to = Utility.getNearestRadian(from, rad);
		Log.v(TAG, "changeOrientation got=" + rad + " from=" + from + " to="
				+ to);
		rotationSmoother.addMotion(to);
	}

	/**
	 * 描画のために毎フレーム呼び出される
	 */
	@Override
	public void onDrawFrame(GL10 gl) {
		if (checkAndRequestRender()) {
			return;
		}

		final long millis = System.currentTimeMillis();
		if (saveBitmapRequestSemaphore.drainPermits() > 0) {
			saveBitmapBuffer(gl);
			resumed.set(false);
			return;
		}

		float cameraZoom = 2f;
		boolean zoomLevelChanged = false;

		// ズームを修正
		for (Integer nextZoomLevel : syncNextZoomLevel.getAndSet(
				Optional.<Integer> absent()).asSet()) {
			if (!nextZoomLevel.equals(zoomLevel)) {
				Log.d(TAG, "zoomLevelChanged: " + zoomLevel + " -> "
						+ nextZoomLevel);
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
			float vehicleRY = selfPoint.x * FloatMath.sin(angle) + selfPoint.y
					* FloatMath.cos(angle);
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
			if (pixelDistanceRate < 0.27) {
				// 近い場合、拡大する
				setZoomLevel(zoomLevel + 1);
			} else if (pixelDistanceRate > 0.56) {
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

	private Boolean checkAndRequestRender() {
		if (!resumed.get()) {
			return true;
		}
		GLSurfaceView glSurfaceView = glSurfaceViewWeakReference.get();
		if (glSurfaceView == null) {
			return true;
		}
		glSurfaceView.requestRender();
		return false;
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
		synchronized (bitmapLock) {
			bitmapWidth = width;
			bitmapHeight = height;
			bitmapBuffer = new short[bitmapWidth * bitmapHeight];
			bitmapShortBuffer = ShortBuffer.wrap(bitmapBuffer);
			bitmapShortBuffer.position(0);
			bitmapSource = new int[bitmapWidth * bitmapHeight];
		}

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
		gl.glClearColor(0, 0, 0, 0);
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

	public void setAutoZoomLevel(boolean newAutoZoomLevel) {
		syncNextAutoZoomLevel.set(Optional.of(newAutoZoomLevel));
	}

	public void setZoomLevel(final int newZoomLevel) {
		if (newZoomLevel > MAX_ZOOM_LEVEL || newZoomLevel < MIN_ZOOM_LEVEL
				|| zoomLevel == newZoomLevel) {
			return;
		}

		uiHandler.post(new Runnable() {
			@Override
			public void run() {
				for (OnChangeMapZoomLevelListener listener : new ArrayList<OnChangeMapZoomLevelListener>(
						onChangeMapZoomLevelListeners)) {
					listener.onChangeMapZoomLevel(newZoomLevel);
				}
			}
		});
		syncNextZoomLevel.set(Optional.of(newZoomLevel));
	}

	public void updateLocation() {
		ServiceUnitStatusLog serviceUnitStatusLog = serviceUnitStatusLogLogic
				.getWithReadLock();
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

	public void updatePlatform(
			Optional<OperationSchedule> optionalOperationSchedule) {
		for (OperationSchedule operationSchedule : optionalOperationSchedule
				.asSet()) {
			for (Platform platform : operationSchedule.getPlatform().asSet()) {
				nextPlatformFrameTask.setLatLng(new LatLng(platform
						.getLatitude().doubleValue(), platform.getLongitude()
						.doubleValue()));
			}
		}
	}

	public void setGLSurfaceView(
			WeakReference<GLSurfaceView> glSurfaceViewWeakReference) {
		this.glSurfaceViewWeakReference = glSurfaceViewWeakReference;
	}

	private final AtomicBoolean resumed = new AtomicBoolean(false);

	public void onResume() {
		Log.i(TAG, "onResume");
		resumed.set(true);
		checkAndRequestRender();
	}

	public void onPause() {
		Log.i(TAG, "onPause");
		resumed.set(false);
	}

	protected final Semaphore saveBitmapRequestSemaphore = new Semaphore(0);
	protected final Semaphore saveBitmapCompleteSemaphore = new Semaphore(0);
	private final Object bitmapLock = new Object();
	private int bitmapWidth = 0;
	private int bitmapHeight = 0;
	private short[] bitmapBuffer = new short[0];
	private int bitmapSource[] = new int[0];
	private ShortBuffer bitmapShortBuffer = ShortBuffer.allocate(0);

	private void saveBitmapBuffer(GL10 gl) {
		synchronized (bitmapLock) {
			try {
				gl.glReadPixels(0, 0, bitmapWidth, bitmapHeight, GL10.GL_RGB,
						GL10.GL_UNSIGNED_SHORT_5_6_5, bitmapShortBuffer);
				saveBitmapCompleteSemaphore.release();
			} catch (GLException e) {
				Log.w(TAG, e);
			}
		}
	}

	public Optional<Bitmap> createBitmapAndPause() {
		saveBitmapRequestSemaphore.release();
		try {
			if (!saveBitmapCompleteSemaphore.tryAcquire(500,
					TimeUnit.MILLISECONDS)) {
				return Optional.absent();
			}
		} catch (InterruptedException e) {
			return Optional.absent();
		}
		synchronized (bitmapLock) {
			int h = bitmapHeight;
			int w = bitmapWidth;

			int offset1, offset2;
			for (int i = 0; i < h; i++) {
				offset1 = i * w;
				offset2 = (h - i - 1) * w;
				for (int j = 0; j < w; j++) {
					short texturePixel = bitmapBuffer[offset1 + j];
					int red = (texturePixel & 0x1f) << 3;
					int green = ((texturePixel >> 5) & 0x3f) << 2;
					int blue = ((texturePixel >> 11) & 0x1f) << 3;
					int pixel = blue << 16 | green << 8 | red;
					bitmapSource[offset2 + j] = pixel;
				}
			}
			return Optional.of(Bitmap.createBitmap(bitmapSource, w, h,
					Bitmap.Config.RGB_565));
		}
	}
}
