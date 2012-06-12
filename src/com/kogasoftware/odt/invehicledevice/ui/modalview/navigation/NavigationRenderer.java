package com.kogasoftware.odt.invehicledevice.ui.modalview.navigation;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.Log;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.eventbus.Subscribe;
import com.javadocmd.simplelatlng.LatLng;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.event.LocationReceivedEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.OrientationChangedEvent;

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
	private final LatLng currentLatLng = new LatLng(35.658517, 139.701334);

	private long framesBy10seconds = 0l;
	private long lastReportMillis = 0l;
	private final List<FrameTask> frameTasks = new LinkedList<FrameTask>();
	private final Queue<FrameTask> addedFrameTasks = new ConcurrentLinkedQueue<FrameTask>();
	private final Queue<FrameTask> removedFrameTasks = new ConcurrentLinkedQueue<FrameTask>();
	private final Set<TileKey> loadingTileKeys = new CopyOnWriteArraySet<TileKey>();
	private CommonLogic commonLogic = new CommonLogic();
	private int width = 0;
	private int height = 0;
	private final AtomicInteger zoom = new AtomicInteger(3);
	private final AtomicInteger nextZoom = new AtomicInteger(zoom.get());
	private final TileCache tileCache;
	private final LoadingCache<TileKey, TileFrameTask> textureCache;
	private final Integer NUM_THREADS = 10;
	private volatile ExecutorService executorService = Executors
			.newFixedThreadPool(NUM_THREADS);

	// private final Set<TileKey> texturedTileKeys = new
	// CopyOnWriteArraySet<TileKey>();

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

	private final RemovalListener<TileKey, TileFrameTask> textureRemovalListener = new RemovalListener<TileKey, TileFrameTask>() {
		@Override
		public void onRemoval(
				RemovalNotification<TileKey, TileFrameTask> notification) {
			// removedFrameTasks.add(notification.getValue());
			// texturedTileKeys.remove(notification.getKey());
		}
	};

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
		textureCache = CacheBuilder.newBuilder().initialCapacity(32)
				.maximumSize(48).removalListener(textureRemovalListener)
				.build(textureCacheLoader);
	}

	/**
	 * 描画のために毎フレーム呼び出される
	 */
	@Override
	public void onDrawFrame(GL10 gl) {
		final long millis = System.currentTimeMillis();

		// 現在の方向を取得
		float angle = Utility.getNearestRadian(0.0,
				-rotationSmoother.getSmoothMotion(millis)).floatValue();

		// 現在地を取得
		double latitude = latitudeSmoother.getSmoothMotion(millis);
		double longitude = longitudeSmoother.getSmoothMotion(millis);

		// latitude = 35.658517;
		// longitude = 139.701334;
		// latitude = 0;
		// longitude = 0;

		currentLatLng.setLatitudeLongitude(latitude, longitude);

		// ズームを修正
		if (zoom.get() != nextZoom.get()) {
			zoom.set(nextZoom.get());
			textureCache.cleanUp();
		}

		// フレームレートの計算
		framesBy10seconds++;
		if (millis - lastReportMillis > 1000) {
			Log.i(TAG, "onDrawFrame() fps=" + (double) framesBy10seconds / 10
					+ ", lat=" + latitude + ", lon=" + longitude + ", zoom="
					+ zoom.get() + ", angle=" + angle);
			framesBy10seconds = 0l;
			lastReportMillis = millis;
		}

		// 上下左右で追加で表示に必要なタイルを準備
		int extraTiles = (int) Math.floor(Math.max(height, width)
				/ TileKey.TILE_LENGTH / 2 + 1);
		TileKey centerTileKey = new TileKey(currentLatLng, zoom.get());

		Map<TileKey, TileFrameTask> inactiveTileFrameTasks = new HashMap<TileKey, TileFrameTask>(
				activeTileFrameTasks);
		for (int x = -extraTiles; x <= extraTiles; ++x) {
			for (int y = -extraTiles; y <= extraTiles; ++y) {
				for (TileKey tileKey : centerTileKey.getRelativeTileKey(x, y)
						.asSet()) {
					// LRUをするため、見えているタイルの参照回数を増やす
					// textureCache.getIfPresent(tileKey);
					// タイルのロードと追加
					addTile(tileKey);
					inactiveTileFrameTasks.remove(tileKey);
				}
			}
		}
		for (TileKey inactiveTileKey : inactiveTileFrameTasks.keySet()) {
			activeTileFrameTasks.remove(inactiveTileKey);
		}
		removedFrameTasks.addAll(inactiveTileFrameTasks.values());

		FrameState frameState = new FrameState(gl, millis, angle,
				currentLatLng, zoom.get(), addedFrameTasks, removedFrameTasks);

		// 追加予約されているFrameTaskを追加
		while (true) {
			FrameTask frameTask = addedFrameTasks.poll();
			if (frameTask == null) {
				break;
			}
			frameTask.onAdd(frameState);
			frameTasks.add(frameTask);
		}

		// 削除予約されているFrameTaskを削除
		while (true) {
			FrameTask frameTask = removedFrameTasks.poll();
			if (frameTask == null) {
				break;
			}
			frameTask.onRemove(frameState);
			frameTasks.remove(frameTask);
		}

		// 描画用バッファをクリア
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		// 射影行列を現在地にあわせて修正
		PointF center = getPoint(currentLatLng, zoom.get());
		gl.glMatrixMode(GL10.GL_PROJECTION);
		// 現在選択されている行列(射影行列)に、単位行列をセット
		gl.glLoadIdentity();

		// 平行投影用のパラメータをセット
		float left = center.x + -width / 2f;
		float right = center.x + width / 2f;
		float bottom = center.y + -height / 2f;
		float top = center.y + height / 2f;
		GLU.gluOrtho2D(gl, left, right, bottom, top);

		// モデル全体の回転
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glTranslatef(center.x, center.y, 0);
		gl.glRotatef((float) Math.toDegrees(angle), 0, 0, 1);
		gl.glTranslatef(-center.x, -center.y, 0);

		// FrameTaskをひとつずつ描画
		for (FrameTask frameTask : frameTasks) {
			frameTask.onDraw(frameState);
		}
	}

	private void addTile(final TileKey tileKey) {
		if (activeTileFrameTasks.containsKey(tileKey)) {
			return;
		}

		// テクスチャが表示されていない場合
		TileFrameTask tileFrameTask = textureCache.getIfPresent(tileKey);
		if (tileFrameTask != null) {
			// キャッシュにあった場合、追加
			activeTileFrameTasks.put(tileKey, tileFrameTask);
			addedFrameTasks.add(tileFrameTask);
			textureCache.invalidate(tileKey);
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
					if (zoom.get() != tileKey.getZoom()) {
						return;
					}
					textureCache.get(tileKey);
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

	public void setCommonLogic(CommonLogic commonLogic) {
		this.commonLogic.dispose();
		this.commonLogic = commonLogic;
		tileCache.setCommonLogic(commonLogic);
	}

	public boolean zoomIn() {
		int temp = zoom.get();
		if (temp > 15) {
			return false;
		}
		nextZoom.set(temp + 1);
		return true;
	}

	public boolean zoomOut() {
		int temp = zoom.get();
		if (temp <= 1) {
			return false;
		}
		nextZoom.set(temp - 1);
		return true;
	}

	public void onResumeActivity() {
		executorService.shutdown();
		executorService = Executors.newFixedThreadPool(NUM_THREADS);
	}

	public void onPauseActivity() {
		executorService.shutdown();
	}
}
