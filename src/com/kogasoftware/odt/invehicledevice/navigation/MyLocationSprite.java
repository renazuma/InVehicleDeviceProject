package com.kogasoftware.odt.invehicledevice.navigation;

import java.io.InputStream;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.common.io.Closeables;
import com.kogasoftware.odt.invehicledevice.R;

public class MyLocationSprite extends Sprite {

	public MyLocationSprite(Resources resources) {
		InputStream inputStream = resources.openRawResource(R.drawable.point);
		Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
		Closeables.closeQuietly(inputStream);
		setBitmap(bitmap);
	}

	@Override
	void onDraw(FrameState frameState) {
		Double alpha = (Math
				.abs(Math.sin(frameState.getMilliSeconds() / 8000d)) / 2 + 0.5);
		draw(new DrawParams(frameState).alpha(alpha));
	}
}
