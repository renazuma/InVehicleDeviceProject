package com.kogasoftware.odt.invehicledevice.ui.modalview.navigation;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.Log;

import com.google.common.base.Optional;
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

	private final MotionSmoother latitudeSmoother = new SimpleMotionSmoother();
	private final MotionSmoother longitudeSmoother = new SimpleMotionSmoother();
	private final LatLng currentLatLng = new LatLng(0, 0);
	private long framesBy10seconds = 0l;
	private long lastReportMillis = 0l;
	private final List<FrameTask> frameTasks = new LinkedList<FrameTask>();
	private final Queue<FrameTask> addedFrameTasks = new ConcurrentLinkedQueue<FrameTask>();
	private final Queue<FrameTask> removedFrameTasks = new ConcurrentLinkedQueue<FrameTask>();
	private CommonLogic commonLogic = new CommonLogic();
	private volatile GL10 gl = new EmptyGL10();
	private int zoom = 1;
	private volatile Optional<Integer> nextZoom = Optional.absent();
	private final TileCache tileCache;
	private final LoadingCache<TileKey, TileFrameTask> textureCache;
	private final Set<TileKey> texturedTileKeys = new CopyOnWriteArraySet<TileKey>();
	private final CacheLoader<TileKey, TileFrameTask> textureCacheLoader = new CacheLoader<TileKey, TileFrameTask>() {
		@Override
		public TileFrameTask load(TileKey key) throws Exception {
			File file = tileCache.get(key);
			Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
			if (bitmap == null) {
				throw new IOException("BitmapFactory.decodeFile(" + file
						+ ") failed");
			}
			int id = Texture.generate(gl);
			Texture.update(gl, id, bitmap);
			bitmap.recycle();
			return new TileFrameTask(key, id);
		}
	};

	private final RemovalListener<TileKey, TileFrameTask> textureRemovalListener = new RemovalListener<TileKey, TileFrameTask>() {
		@Override
		public void onRemoval(
				RemovalNotification<TileKey, TileFrameTask> notification) {
			removedFrameTasks.add(notification.getValue());
			texturedTileKeys.remove(notification.getKey());
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
		textureCache = CacheBuilder.newBuilder()
				.removalListener(textureRemovalListener)
				.build(textureCacheLoader);
	}

	/**
	 * 描画のために毎フレーム呼び出される
	 */
	@Override
	public void onDrawFrame(GL10 gl) {
		this.gl = gl;

		final long millis = System.currentTimeMillis();

		// 現在の方向を取得
		float angle = Utility.getNearestRadian(0.0,
				2 * Math.PI - rotationSmoother.getSmoothMotion(millis))
				.floatValue();

		// 現在地を取得
		double latitude = latitudeSmoother.getSmoothMotion(millis);
		double longitude = longitudeSmoother.getSmoothMotion(millis);
		currentLatLng.setLatitudeLongitude(latitude, longitude);

		// ズームを修正
		if (nextZoom.isPresent()) {
			zoom = nextZoom.get();
			nextZoom = Optional.absent();
		}

		// フレームレートの計算
		framesBy10seconds++;
		if (millis - lastReportMillis > 10000) {
			Log.i(TAG, "onDrawFrame() fps=" + (double) framesBy10seconds / 10
					+ ", lat=" + latitude + ", lon=" + longitude + ", zoom="
					+ zoom + ", angle=" + angle);
			framesBy10seconds = 0l;
			lastReportMillis = millis;
		}

		// 表示に必要なタイルを準備
		{
			TileKey tileKey = new TileKey(currentLatLng, zoom);
			if (!texturedTileKeys.contains(tileKey)) {
				// テクスチャがロードされていない場合
				TileFrameTask tileFrameTask = textureCache
						.getIfPresent(tileKey);
				if (tileFrameTask == null) {
					// キャッシュに無い場合、キャッシュにロード
					textureCache.refresh(tileKey);
				} else {
					// キャッシュに有った場合、追加
					texturedTileKeys.add(tileKey);
					addedFrameTasks.add(tileFrameTask);
				}
			}
		}

		// 描画用バッファをクリア
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		FrameState frameState = new FrameState(gl, millis, angle,
				currentLatLng, addedFrameTasks, removedFrameTasks);

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

		// FrameTaskをひとつずつ描画
		for (FrameTask frameTask : frameTasks) {
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
		this.gl = gl;

		Log.i(TAG, "onSurfaceChanged() width=" + width + " height=" + height);

		// ビューポートをサイズに合わせてセットしなおす
		gl.glViewport(0, 0, width, height);
		// 射影行列を選択
		gl.glMatrixMode(GL10.GL_PROJECTION);
		// 現在選択されている行列(射影行列)に、単位行列をセット
		gl.glLoadIdentity();

		// 平行投影用のパラメータをセット
		GLU.gluOrtho2D(gl, -width / 2f, width / 2f, -height / 2f, height / 2f);
	}

	/**
	 * サーフェスが生成される際・または再生成される際に呼び出される
	 */
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		this.gl = gl;

		Log.i(TAG, "onSurfaceCreated()");
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

	public void setCommonLogic(CommonLogic commonLogic) {
		this.commonLogic.dispose();
		this.commonLogic = commonLogic;
		tileCache.setCommonLogic(commonLogic);
	}
}
