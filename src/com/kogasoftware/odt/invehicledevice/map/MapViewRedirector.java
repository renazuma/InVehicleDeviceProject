package com.kogasoftware.odt.invehicledevice.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import com.google.android.maps.MapView;
import com.kogasoftware.odt.invehicledevice.LogTag;

/**
 * 子Viewの画像をBitmapSynchronizerへ渡す
 * 
 * @author ksc
 * 
 */
public class MapViewRedirector extends FrameLayout {
	private static final String T = LogTag.get(MapViewRedirector.class);

	private MapSynchronizer mapSynchronizer = new EmptyMapSynchronizer();
	private MapView mapView = null;
	private Boolean superThrownNullPointerException = false;

	public MapViewRedirector(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		if (superThrownNullPointerException || mapView == null
				|| mapSynchronizer == null) {
			return;
		}
		mapSynchronizer.write(new MapSynchronizer.Accessor() {
			@Override
			public void run(MapSnapshot mapSnapshot) {
				if (mapSnapshot == null) {
					throw new RuntimeException("mapSnapshot == null");
				}
				Bitmap bitmap = mapSnapshot.bitmap;
				if (bitmap == null) {
					throw new RuntimeException("mapSnapshot.bitmap == null");
				}
				if (bitmap.isRecycled()) {
					throw new RuntimeException(
							"mapSnapshot.bitmap.isRecycled()");
				}
				try {
					MapViewRedirector.super.dispatchDraw(new Canvas(bitmap));
				} catch (NullPointerException e) {
					// XXX アクティビティ終了時にこの例外が起こってしまうことがあるため、その場合ビューの動作を静かに停止する
					Log.e(T,
							"NullPointerException on MapViewRedirector.super.dispatchDraw(new Canvas(bitmap))");
					e.printStackTrace();
					superThrownNullPointerException = true;
					return;
				}
				mapSnapshot.center = mapView.getMapCenter();
			}
		});
	}

	public void init(MapSynchronizer mapSynchronizer, MapView mapView) {
		this.mapView = mapView;
		this.mapSynchronizer = mapSynchronizer;
	}
}
