package com.kogasoftware.odt.invehicledevice.ui.modalview.navigation;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationManager;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.Log;

import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.eventbus.Subscribe;
import com.javadocmd.simplelatlng.LatLng;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.event.EnterDrivePhaseEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.LocationReceivedEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.OrientationChangedEvent;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.Platform;
import com.kogasoftware.odt.webapi.model.ServiceUnitStatusLog;

public class NavigationRenderer implements GLSurfaceView.Renderer {
	private static final String TAG = NavigationRenderer.class.getSimpleName();
	public static final Integer MAX_TILE_CACHE_BYTES = 100 * 1024 * 1024;

	private final MotionSmoother rotationSmoother = new LazyMotionSmoother(
			500.0, 0.02, 0.00005);

	// private final MotionSmoother latitudeSmoother = new LazyMotionSmoother(
	// 500.0, 0.02, 0.00005);
	// private final MotionSmoother longitudeSmoother = new LazyMotionSmoother(
	// 500.0, 0.02, 0.00005);

	public static PointF getPoint(LatLng latLng, int zoom) {
		int totalPixels = (1 << zoom) * TileKey.TILE_LENGTH;
		double x = latLng.getLongitude() * totalPixels / 360d;
		double y = SphericalMercator.lat2y(latLng.getLatitude()) * totalPixels
				/ 360d;
		return new PointF((float) x, (float) y);
	}

	private final MotionSmoother latitudeSmoother = new SimpleMotionSmoother();
	private final MotionSmoother longitudeSmoother = new SimpleMotionSmoother();
	private final List<FrameTask> frameTasks = new LinkedList<FrameTask>();
	private final List<FrameTask> frontFrameTasks = new LinkedList<FrameTask>();
	private final Queue<FrameTask> addedFrameTasks = new ConcurrentLinkedQueue<FrameTask>();
	private final Queue<FrameTask> removedFrameTasks = new ConcurrentLinkedQueue<FrameTask>();
	private final Set<TileKey> loadingTileKeys = new CopyOnWriteArraySet<TileKey>();
	private final TileCache tileCache;
	private final LoadingCache<TileKey, TileFrameTask> tileFrameTaskCache;
	private final Integer NUM_THREADS = 10;
	private final NextPlatformFrameTask nextPlatformFrameTask;
	private final Map<TileKey, TileFrameTask> activeTileFrameTasks = new ConcurrentHashMap<TileKey, TileFrameTask>();
	private final CacheLoader<TileKey, TileFrameTask> textureCacheLoader = new CacheLoader<TileKey, TileFrameTask>() {
		@Override
		public TileFrameTask load(TileKey key) throws Exception {
			File file = tileCache.get(key);
			if (!file.exists()) {
				tileCache.invalidate(key);
				throw new IOException("!\"" + file + "\".exists()");
			}

			Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
			if (bitmap == null) {
				tileCache.invalidate(key);
				throw new IOException("BitmapFactory.decodeFile(" + file
						+ ") failed");
			}
			return new TileFrameTask(key, bitmap);
		}
	};

	private CommonLogic commonLogic = new CommonLogic();
	private long framesBy10seconds = 0l;
	private long lastReportMillis = 0l;
	private int width = 0;
	private int height = 0;
	private volatile ExecutorService executorService = Executors
			.newFixedThreadPool(NUM_THREADS);

	private int zoom = 15; // ズームレベル
	private final AtomicReference<Optional<Integer>> syncNextZoom = new AtomicReference<Optional<Integer>>(
			Optional.<Integer> absent()); // 描画中にzoomの値が変更されないようにするための変数
	private boolean autoZoom = true; // 自動ズームするかどうか
	private final AtomicReference<Optional<Boolean>> syncNextAutoZoom = new AtomicReference<Optional<Boolean>>(
			Optional.<Boolean> absent()); // 描画中にautoZoomの値が変更されないようにするための変数

	public NavigationRenderer(Context context) {
		// frameTasks.add(new GeoPointDroidSprite(context.getResources(),
		// new LatLng(35.899975, 139.935788)));
		// frameTasks.add(new MyLocationSprite(context.getResources()));
		try {
			tileCache = new TileCache(context, MAX_TILE_CACHE_BYTES);
		} catch (IOException e) {
			// この例外はリカバリー不可能
			// TODO: 起動時にSDがささっているかどうかのチェック
			Log.wtf(TAG, e);
			throw new RuntimeException(e);
		}
		tileFrameTaskCache = CacheBuilder.newBuilder().initialCapacity(32)
				.maximumSize(48).build(textureCacheLoader);

		nextPlatformFrameTask = new NextPlatformFrameTask(
				context.getResources());

		addedFrameTasks.add(new SelfFrameTask(context.getResources()));
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

		// ズームを修正
		for (Integer nextZoom : syncNextZoom.getAndSet(
				Optional.<Integer> absent()).asSet()) {
			if (!nextZoom.equals(zoom)) {
				tileFrameTaskCache.cleanUp();
				zoom = nextZoom;
			}
		}

		// 自動ズームかどうかを修正
		for (Boolean nextAutoZoom : syncNextAutoZoom.getAndSet(
				Optional.<Boolean> absent()).asSet()) {
			if (!nextAutoZoom.equals(autoZoom)) {
				autoZoom = nextAutoZoom;
			}
		}

		// 現在の方向を取得
		float angle = (float) -rotationSmoother.getSmoothMotion(millis);

		// 現在地を取得
		LatLng vehicleLatLng = new LatLng(
				latitudeSmoother.getSmoothMotion(millis),
				longitudeSmoother.getSmoothMotion(millis));
		LatLng centerLatLng = vehicleLatLng;

		// フレームレートの計算
		framesBy10seconds++;
		if (millis - lastReportMillis > 1000) {
			Log.i(TAG, "onDrawFrame() fps=" + (double) framesBy10seconds / 10
					+ ", lat=" + vehicleLatLng.getLatitude() + ", lon="
					+ vehicleLatLng.getLongitude() + ", zoom=" + zoom
					+ ", angle=" + angle);
			framesBy10seconds = 0l;
			lastReportMillis = millis;
		}

		// 上下左右で追加で表示に必要なタイルを準備
		int extraTiles = (int) Math.floor(Math.max(height, width) / cameraZoom
				/ TileKey.TILE_LENGTH / 2 + 1);
		TileKey centerTileKey = new TileKey(centerLatLng, zoom);
		Map<TileKey, TileFrameTask> inactiveTileFrameTasks = new HashMap<TileKey, TileFrameTask>(
				activeTileFrameTasks);
		Multimap<Double, TileKey> tileKeyByDistance = LinkedListMultimap
				.<Double, TileKey> create();
		for (int x = -extraTiles; x <= extraTiles; ++x) {
			for (int y = -extraTiles; y <= extraTiles; ++y) {
				for (TileKey tileKey : centerTileKey.getRelativeTileKey(x, y)
						.asSet()) {
					double distance = Math.pow((double) x * height / width, 2)
							+ y * y;
					tileKeyByDistance.put(distance, tileKey);
				}
			}
		}
		// 必要なタイルを中心に近い順にソートして追加
		for (Double distance : new TreeSet<Double>(tileKeyByDistance.keys())) {
			for (TileKey tileKey : tileKeyByDistance.get(distance)) {
				addTile(tileKey);
				inactiveTileFrameTasks.remove(tileKey);
			}
		}
		// 不要なタイルを削除予約
		for (TileKey inactiveTileKey : inactiveTileFrameTasks.keySet()) {
			activeTileFrameTasks.remove(inactiveTileKey);
		}
		removedFrameTasks.addAll(inactiveTileFrameTasks.values());

		// 描画用の情報を作成
		FrameState frameState = new FrameState(gl, millis, angle, centerLatLng,
				zoom, addedFrameTasks, removedFrameTasks);

		// 追加予約、削除予約されているFrameTaskをリストに追加削除
		while (!addedFrameTasks.isEmpty() || !removedFrameTasks.isEmpty()) {
			FrameTask newFrameTask = addedFrameTasks.poll();
			if (newFrameTask != null) {
				newFrameTask.onAdd(frameState);
				if (newFrameTask.getClass().isAnnotationPresent(
						FrameTask.Front.class)) {
					frontFrameTasks.add(newFrameTask);
				} else {
					frameTasks.add(newFrameTask);
				}
			}
			FrameTask deleteFrameTask = removedFrameTasks.poll();
			if (deleteFrameTask != null) {
				deleteFrameTask.onRemove(frameState);
				frameTasks.remove(deleteFrameTask);
			}
		}

		// 描画用バッファをクリア
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		// 射影行列を現在地にあわせて修正
		PointF center = getPoint(centerLatLng, zoom);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		// 現在選択されている行列(射影行列)に、単位行列をセット
		gl.glLoadIdentity();

		// 平行投影用のパラメータをセット
		float left = center.x + -width / 2f;
		float right = center.x + width / 2f;
		float bottom = center.y + -height / 2f;
		float top = center.y + height / 2f;
		GLU.gluOrtho2D(gl, left, right, bottom, top);

		// モデル全体の回転と拡大率を設定
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glTranslatef(center.x, center.y, 0);
		gl.glRotatef((float) Math.toDegrees(angle), 0, 0, 1);
		gl.glScalef(cameraZoom, cameraZoom, cameraZoom);
		gl.glTranslatef(-center.x, -center.y, 0);

		// FrameTaskをひとつずつ描画
		for (FrameTask frameTask : frameTasks) {
			frameTask.onDraw(frameState);
		}
		for (FrameTask frameTask : frontFrameTasks) {
			frameTask.onDraw(frameState);
		}
	}

	private void addTile(final TileKey tileKey) {
		if (activeTileFrameTasks.containsKey(tileKey)) {
			return;
		}

		// テクスチャが表示されていない場合
		TileFrameTask tileFrameTask = tileFrameTaskCache.getIfPresent(tileKey);
		if (tileFrameTask != null) {
			// キャッシュにあった場合、追加
			activeTileFrameTasks.put(tileKey, tileFrameTask);
			addedFrameTasks.add(tileFrameTask);
			tileFrameTaskCache.invalidate(tileKey);
		}

		// ロード中の場合終了
		if (loadingTileKeys.contains(tileKey)) {
			return;
		}

		// ロード開始
		loadingTileKeys.add(tileKey);
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					if (Thread.currentThread().isInterrupted()) {
						return;
					}
					if (zoom != tileKey.getZoom()) {
						return;
					}
					tileFrameTaskCache.get(tileKey);
				} catch (ExecutionException e) {
					tileCache.invalidate(tileKey);
					Log.w(TAG, e);
				} finally {
					loadingTileKeys.remove(tileKey);
				}
			}
		};

		try {
			executorService.submit(runnable);
		} catch (RejectedExecutionException e) {
			Log.w(TAG, e);
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
		double from = rotationSmoother.getSmoothMotion();
		double to = Utility.getNearestRadian(from,
				Math.toRadians(event.orientationDegree));
		Log.i(TAG, "changeOrientation got=" + event.orientationDegree
				+ " from=" + from + " to=" + to);
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
		this.commonLogic.dispose();
		this.commonLogic = commonLogic;
		tileCache.setCommonLogic(commonLogic);
		setNextPlatform();
		changeLocation();
	}

	public void setZoomLevel(int newZoom) {
		syncNextZoom.set(Optional.of(newZoom));
	}

	public void setAutoZoom(boolean newAutoZoom) {
		syncNextAutoZoom.set(Optional.of(newAutoZoom));
	}

	public void onResumeActivity() {
		executorService.shutdown();
		executorService = Executors.newFixedThreadPool(NUM_THREADS);
	}

	public void onPauseActivity() {
		executorService.shutdown();
	}
}
