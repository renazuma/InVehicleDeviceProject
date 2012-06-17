package com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.tilepipeline;

import java.util.Comparator;

import android.graphics.Bitmap;

public class BitmapPipeQueue<K> extends PipeQueue<K, Bitmap> {

	public BitmapPipeQueue(int limit, OnDropListener<K> onDropListener,
			Comparator<K> comparator) {
		super(limit, onDropListener, comparator);
	}

	@Override
	protected void disposeValue(Bitmap bitmap) {
		if (!bitmap.isRecycled()) {
			bitmap.recycle();
		}
	}
}
