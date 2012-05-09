package com.kogasoftware.odt.invehicledevice.navigation;

import java.io.InputStream;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.common.io.Closeables;
import com.kogasoftware.odt.invehicledevice.R;

public class DroidSprite extends Sprite {
	private final Integer x;
	private final Integer y;

	public DroidSprite(Resources resources, int x, int y) {
		this.x = x;
		this.y = y;
		InputStream inputStream = resources.openRawResource(R.drawable.droid);
		Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
		Closeables.closeQuietly(inputStream);
		setBitmap(bitmap);
	}

	@Override
	public void onDraw(FrameState frameState) {
		draw(frameState, x.doubleValue(), y.doubleValue());
	}
}
