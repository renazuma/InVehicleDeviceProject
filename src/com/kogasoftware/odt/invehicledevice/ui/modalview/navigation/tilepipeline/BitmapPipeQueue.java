package com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.tilepipeline;

import android.graphics.Bitmap;

public class BitmapPipeQueue<K> extends PipeQueue<K, Bitmap> {

	public BitmapPipeQueue(int limit, OnDropListener<K> onDropListener) {
		super(limit, onDropListener);
	}

	@Override
	protected void disposeValue(Bitmap bitmap) {
		if (!bitmap.isRecycled()) {
			bitmap.recycle();
		}
	}
}
