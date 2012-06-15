package com.kogasoftware.odt.invehicledevice.ui.modalview.navigation;

import android.content.Context;

import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;

/**
 * 地図タイルファイルの
 * Web → ファイル → メモリ → テクスチャ のロードのパイプラインを管理するクラス
 */
public class TilePipeline {
	private volatile CommonLogic commonLogic = new CommonLogic();
	
	public TilePipeline(Context context) {
	}

	/**
	 * 指定されたzoomLevel以外のデータをパイプラインから消去する
	 */
	public void changeZoomLevel(int zoomLevel) {
	}

	/**
	 * 指定されたTileFrameTaskがメモリに存在したら取得し、存在しない場合はロードを開始する
	 */
	public Optional<TileFrameTask> pollOrStartLoad(TileKey tileKey) {
		return Optional.absent();
	}

	public void setCommonLogic(CommonLogic commonLogic) {
		this.commonLogic.dispose();
		this.commonLogic = commonLogic;
	}
	
//	public TileFrameTask xload(TileKey key) throws Exception {
//		File file = tileCache.get(key);
//		if (!file.exists()) {
//			tileCache.invalidate(key);
//			throw new IOException("!\"" + file + "\".exists()");
//		}
//
//		Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//		if (bitmap == null) {
//			tileCache.invalidate(key);
//			throw new IOException("BitmapFactory.decodeFile(" + file
//					+ ") failed");
//		}
//		return new TileFrameTask(key, bitmap);
//	}
	
}
