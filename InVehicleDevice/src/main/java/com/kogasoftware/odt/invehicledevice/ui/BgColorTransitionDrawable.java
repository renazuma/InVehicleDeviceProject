/**
 * Original source: "https://groups.google.com/forum/#!topic/android-developers/D98RChFz67s"
 */

package com.kogasoftware.odt.invehicledevice.ui;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;

/**
 * 背景色を変更可能なDrawable
 */
public class BgColorTransitionDrawable extends TransitionDrawable {
	private final int interval;

	public BgColorTransitionDrawable(int color) {
		this(color, 50);
	}

	public BgColorTransitionDrawable(int color, int interval) {
		super(new ColorDrawable[]{new ColorDrawable(color),
				new ColorDrawable(color)});
		this.interval = interval;
		setCrossFadeEnabled(true);
		setId(0, 0);
		setId(1, 1);
	}

	public void changeColor(int color) {
		setDrawableByLayerId(0, getDrawable(1));
		setDrawableByLayerId(1, new ColorDrawable(color));
		startTransition(interval);
	}
}
