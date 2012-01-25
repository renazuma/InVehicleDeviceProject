package com.kogasoftware.odt.invehicledevice;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.google.common.collect.Lists;

public class OverlayLinearLayout extends LinearLayout {
	private static final Queue<OverlayLinearLayout> attachedInstances = new ConcurrentLinkedQueue<OverlayLinearLayout>();

	public static List<OverlayLinearLayout> getAttachedInstances() {
		return Lists.newLinkedList(attachedInstances);
	}

	public OverlayLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		attachedInstances.add(this);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		attachedInstances.remove(this);
	}

	public void hide() {
		setVisibility(View.GONE);
	}
}
