package com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.tilepipeline;

import android.graphics.Bitmap;

import com.google.common.base.Predicate;

public class BitmapPipeQueue<K> extends PipeQueue<K, Bitmap> {
	public BitmapPipeQueue(int limit, Predicate<K> isValid) {
		super(limit, isValid);
	}

	@Override
	protected void disposeValue(Bitmap bitmap) {
		if (!bitmap.isRecycled()) {
			bitmap.recycle();
		}
	}
}
